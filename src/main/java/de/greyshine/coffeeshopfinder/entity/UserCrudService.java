package de.greyshine.coffeeshopfinder.entity;

import de.greyshine.coffeeshopfinder.service.ValidationService;
import de.greyshine.coffeeshopfinder.utils.Utils;
import de.greyshine.coffeeshopfinder.web.LoginException;
import de.greyshine.json.crud.JsonCrudService;
import de.greyshine.json.crud.JsonService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static de.greyshine.coffeeshopfinder.utils.Utils.*;
import static org.apache.commons.lang3.StringUtils.*;
import static org.springframework.util.Assert.*;

@Service
@Slf4j
public class UserCrudService extends JsonCrudService {

    private static final AtomicLong IDS = new AtomicLong(0);
    private static final Map<String, TokenInfo> tokenMap = new HashMap<>();
    private final ValidationService validationService;
    private final long tokenInfoTimeToLive = 10 * 60 * 1000;

    public UserCrudService(@Autowired JsonService jsonService, @Autowired ValidationService validationService) {
        super(jsonService);
        this.validationService = validationService;
    }

    public String create(UserEntity userEntity) {

        notNull(userEntity, UserEntity.class.getCanonicalName() + " is null");
        isNull(userEntity.getId(), UserEntity.class.getCanonicalName() + " is null");
        isTrue(validationService.isValidLogin(userEntity.getLogin()), UserEntity.class.getCanonicalName() + " illegal login: " + userEntity.getLogin());
        notNull(userEntity.getPassword(), "password is null");

        isTrue(!isLoginOrName(userEntity.getLogin(), userEntity.getName()), "Login or user is used: login=" + userEntity.getLogin() + ", user=" + userEntity.getName());

        isTrue(!isLogin(userEntity.getLogin()), "Login already exists: " + userEntity.getLogin());
        isTrue(!isName(userEntity.getName()), "User already exists: " + userEntity.getName());

        userEntity.setPassword(Utils.toHash(userEntity.getPassword()));

        userEntity.setId(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(LocalDateTime.now()) +
                "_" +
                Long.toString(IDS.addAndGet(1), 16));

        userEntity.setConfirmationCode(createConfirmationCode());

        return super.create(userEntity);

    }

    public boolean isLoginOrName(final String login, final String name) {

        isTrue(trimToNull(login) != null || trimToNull(name) != null, "Login and User are null");

        final AtomicBoolean result = new AtomicBoolean(false);

        super.iterate(UserEntity.class, Sync.LOCAL, (re) -> {

            if (Utils.isEqualsTrimmed(re.getLogin(), login, false)) {
                result.set(true);
                return false;
            } else if (Utils.isEqualsTrimmed(re.getName(), name, false)) {
                result.set(true);
                return false;
            }

            return null;
        });

        return result.get();
    }

    public boolean isLogin(final String login) {

        final AtomicBoolean result = new AtomicBoolean(false);

        super.iterate(UserEntity.class, Sync.LOCAL, userEntity -> {

            if (isEqualsTrimmed(login, userEntity.getLogin(), false)) {
                result.set(true);
                return false;
            }

            return null;
        });

        return result.get();
    }

    public boolean isName(final String user) {

        final AtomicBoolean result = new AtomicBoolean(false);

        super.iterate(UserEntity.class, Sync.LOCAL, userEntity -> {

            if (isEqualsTrimmed(user, userEntity.getName(), false)) {
                result.set(true);
                return false;
            }

            return null;
        });

        return result.get();
    }

    public String createConfirmationCode() {

        final StringBuffer sb = new StringBuffer();

        for (int i = 0, l = Utils.getRandom(3, 5); i < l; i++) {
            sb.append(Utils.getRandomLetter());
        }

        sb.append('-');

        for (int i = 0, l = Utils.getRandom(3, 5); i < l; i++) {
            sb.append(Utils.getRandomLetter());
        }

        return sb.toString();
    }


    @SneakyThrows
    public String executeLogin(String login, String password) {

        if (isBlank(login) || isBlank(password)) {
            throw new LoginException(login);
        }

        final String passwordHash = Utils.toHash(password);

        final Utils.AtomicObject<UserEntity> userEnityAo = new Utils.AtomicObject<>();

        iterate(UserEntity.class, Sync.GLOBAL, (user) -> {

            if (login.equals(user.getLogin())) {
                userEnityAo.setObject(user);
                return false;
            }

            return true;
        });

        final UserEntity userEntity = userEnityAo.getObject();

        if (userEntity == null) {
            throw new LoginException(login);
        } else if (!passwordHash.equals(userEntity.getPassword())) {
            userEntity.increaseBadLogins();
        } else if (isNotBlank(userEntity.getConfirmationCode())) {
            throw new LoginException(login, "CONFIRMATION_CODE");
        } else if (userEntity.getBadlogins() > 6) {
            throw new LoginException(login, "BAD_LOGINS");
        }

        if (userEntity.getBadlogins() > 0) {
            userEntity.setBadlogins(0);
            super.update(Sync.GLOBAL, userEntity);
        }

        final TokenInfo tokenInfo = new TokenInfo(userEntity.getId());
        executeSynced(this.tokenMap, () -> this.tokenMap.put(tokenInfo.getToken(), tokenInfo));
        log.info("created {}", tokenInfo);

        return tokenInfo.getToken();
    }

    @SneakyThrows
    public TokenInfo getTokenInfo(String token) {
        return executeSynced(this.tokenMap, () -> this.tokenMap.get(token));
    }

    public void removeInvalidTokens() throws Exception {

        executeSynced(this.tokenMap, () -> {

            tokenMap.values().forEach(tokenInfo -> {

                final Boolean isValid = executeSafe(() -> tokenInfo.isAccessible(), null);

                if (Boolean.TRUE.equals(isValid)) {
                    return;
                } else if (isValid == null) {
                    log.error("Failure on getting valid state of token: " + tokenInfo);
                    return;
                }

                tokenMap.remove(tokenInfo.getToken());
                log.info("removed invalid token: {}", tokenInfo);
            });

        });
    }

    @SneakyThrows
    public boolean logout(String token) {

        final TokenInfo tokenInfo = executeSynced(tokenMap, () -> tokenMap.remove(token));

        log.info("logout: {} -> {}", token, tokenInfo != null);

        return tokenInfo != null;
    }

    public UserEntity get(String login) {

        Assert.notNull(login, "Login must not be null");

        final List<UserEntity> users = iterate(UserEntity.class, Sync.LOCAL, user -> {

            if (login.equals(user.getLogin())) {
                return false;
            }

            return true;
        });

        Assert.isTrue(users.isEmpty() || users.size() == 1, "Unexpected amount of users with login=" + login);

        return users.isEmpty() ? null : users.get(0);
    }

    @SneakyThrows
    public boolean updateToken(String token) {

        final TokenInfo tokenInfo = getTokenInfo(token);

        return tokenInfo != null && tokenInfo.updateLastAccess();
    }


    @Data
    @RequiredArgsConstructor
    public class TokenInfo {
        private final LocalDateTime creation = LocalDateTime.now();
        private final String token = getRandomLetters(32);
        @NonNull
        private final String userId;

        @Setter(AccessLevel.NONE)
        private long lastAccess = System.currentTimeMillis();

        public boolean updateLastAccess() throws Exception {

            if (!isAccessible()) {
                return false;
            }

            lastAccess = System.currentTimeMillis();
            return true;
        }

        public boolean isAccessible() throws Exception {
            return lastAccess + tokenInfoTimeToLive <= System.currentTimeMillis() || !executeSynced(tokenMap, () -> tokenMap.containsKey(token));
        }
    }
}
