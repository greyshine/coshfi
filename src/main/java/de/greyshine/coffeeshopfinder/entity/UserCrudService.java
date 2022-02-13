package de.greyshine.coffeeshopfinder.entity;

import de.greyshine.coffeeshopfinder.service.EmailService;
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
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static de.greyshine.coffeeshopfinder.utils.Utils.*;
import static org.apache.commons.lang3.StringUtils.*;
import static org.springframework.util.Assert.*;

@Service
@Slf4j
public class UserCrudService extends JsonCrudService {

    private static final AtomicLong IDS = new AtomicLong(0);
    private static final Map<String, TokenInfo> tokenMap = new HashMap<>();
    private static final int MAX_BAD_LOGINS = 6;

    private final EmailService emailService;
    private final ValidationService validationService;
    private final long tokenInfoTimeToLive = 10 * 60 * 1000;

    public UserCrudService(@Autowired JsonService jsonService, @Autowired ValidationService validationService, @Autowired EmailService emailService) {
        super(jsonService);
        this.emailService = emailService;
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

        userEntity.setEmail(userEntity.getEmail().toLowerCase(Locale.ROOT));
        userEntity.setPassword(Utils.toHash(userEntity.getPassword()));

        final UserEntity existingUserEntity = iterateSingle(UserEntity.class, Sync.LOCAL, user -> {

            boolean isHit = Utils.isEqualsIgnoreCaseTrimmed(userEntity.getLogin(), user.getLogin(), false);
            if (isHit) {
                log.info("Refusing User creation; existing login: {}", user);
                return IterationResult.USE_QUIT;
            }

            isHit = Utils.isEqualsIgnoreCaseTrimmed(userEntity.getName(), user.getName(), false);
            if (isHit) {
                log.info("Refusing User creation; existing name: {}", user);
                return IterationResult.USE_QUIT;
            }

            isHit = user.getConfirmationCode() == null && Utils.isEqualsIgnoreCaseTrimmed(userEntity.getEmail(), user.getEmail(), false);
            if (isHit) {
                log.info("Refusing User creation; existing email: {}", user);
                return IterationResult.USE_QUIT;
            }

            return IterationResult.IGNORE;
        });

        Assert.isNull(existingUserEntity, "There is already an user existing: " + existingUserEntity);

        userEntity.setId(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(LocalDateTime.now()) + "_" + Long.toString(IDS.addAndGet(1), 16));
        userEntity.setConfirmationCode(createConfirmationCode());

        return super.create(userEntity);
    }

    public boolean isLoginOrName(final String login, final String name) {

        isTrue(trimToNull(login) != null || trimToNull(name) != null, "Login and User are null");

        final List<UserEntity> userEntities = super.iterate(UserEntity.class, Sync.LOCAL, (userEntity) -> {

            if (Utils.isEqualsTrimmed(userEntity.getLogin(), login, false)) {
                return IterationResult.USE_QUIT;
            } else if (Utils.isEqualsTrimmed(userEntity.getName(), name, false)) {
                return IterationResult.USE_QUIT;
            }

            return IterationResult.IGNORE;
        });

        return !userEntities.isEmpty();
    }

    public boolean isLogin(final String login) {

        return null != iterateSingle(UserEntity.class, Sync.LOCAL, userEntity -> {

            if (Utils.isEquals(userEntity.getLogin(), login, false)) {
                return IterationResult.USE_QUIT;
            }

            return IterationResult.IGNORE;
        });
    }

    public boolean isName(final String name) {
        return null != iterateSingle(UserEntity.class, Sync.LOCAL, userEntity -> {
            if (Utils.isEquals(userEntity.getName(), name, false)) {
                return IterationResult.USE_QUIT;
            }

            return IterationResult.IGNORE;
        });
    }

    public String createConfirmationCode() {

        final StringBuffer sb = new StringBuffer();

        for (int i = 0, l = Utils.getRandom(3, 5); i < l; i++) {
            sb.append(Utils.getRandomLetter());
        }

        sb.append('-');

        for (int i = 0, l = Utils.getRandom(3, 6); i < l; i++) {
            sb.append(Utils.getRandomLetter());
        }

        return sb.toString();
    }

    @SneakyThrows
    public String executeLogin(String login, String password, String confirmationcode) {

        if (isBlank(login) || isBlank(password)) {
            throw new LoginException(login);
        }

        final UserEntity userEntity = iterateSingle(UserEntity.class, Sync.GLOBAL,
                user -> login.equalsIgnoreCase(user.getLogin()) ? IterationResult.USE_QUIT : IterationResult.IGNORE);

        boolean doUpdate = false;

        if (userEntity == null) {

            throw new LoginException(login);

        } else if (!Utils.isEquals(userEntity.getPassword(), Utils.toHash(password), false)) {

            userEntity.increaseBadLogins();
            super.update(Sync.GLOBAL, userEntity);
            throw new LoginException(login);

        } else if (userEntity.getBadlogins() > MAX_BAD_LOGINS) {

            userEntity.increaseBadLogins();
            update(Sync.GLOBAL, userEntity);
            throw new LoginException(login, "BAD_LOGINS");

        } else if (userEntity.getConfirmationCode() != null && !userEntity.getConfirmationCode().equals(confirmationcode)) {

            throw new LoginException(login, "CONFIRMATION_CODE");
        }

        if (userEntity.getConfirmationCode() != null) {
            userEntity.setConfirmationCode(null);
            doUpdate = true;
        }

        if (userEntity.getBadlogins() > 0) {
            userEntity.setBadlogins(0);
            doUpdate = true;
        }

        if (doUpdate) {
            super.update(Sync.GLOBAL, userEntity);
        }

        final TokenInfo tokenInfo = new TokenInfo(userEntity.getId());
        executeSynced(tokenMap, () -> tokenMap.put(tokenInfo.getToken(), tokenInfo));
        log.info("created {}", tokenInfo);

        return tokenInfo.getToken();
    }

    @SneakyThrows
    public TokenInfo getTokenInfo(String token) {
        return executeSynced(tokenMap, () -> tokenMap.get(token));
    }

    public void removeInvalidTokens() throws Exception {

        executeSynced(tokenMap, () -> {

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

    public UserEntity getByLogin(String login) {

        Assert.notNull(login, "Login must not be null");

        final List<UserEntity> users = iterate(UserEntity.class, Sync.LOCAL, user -> login.equals(user.getLogin()) ? IterationResult.USE_QUIT : IterationResult.IGNORE);

        Assert.isTrue(users.isEmpty() || users.size() == 1, "Unexpected amount of users with login=" + login);

        return users.isEmpty() ? null : users.get(0);
    }

    @SneakyThrows
    public boolean updateToken(String token) {

        final TokenInfo tokenInfo = getTokenInfo(token);

        return tokenInfo != null && tokenInfo.updateLastAccess();
    }

    public void resetConfirmationCode(String email) {

        isTrue(isNotBlank(email), "Email must not be blank");

        final String emailLc = email.trim().toLowerCase(Locale.ROOT);

        final List<UserEntity> userEntities =
                iterate(UserEntity.class, Sync.LOCAL, (userEntity) -> emailLc.equals(userEntity.getEmail().toLowerCase(Locale.ROOT)) ? IterationResult.USE_QUIT : IterationResult.IGNORE_QUIT);

        if (userEntities.isEmpty()) {

            log.info("no user found for email: {}", email);
            return;
        }

        userEntities.forEach(userEntity -> {

            try {

                String confirmationcode = userEntity.getConfirmationCode();

                if (confirmationcode == null) {
                    confirmationcode = createConfirmationCode();
                    userEntity.setConfirmationCode(confirmationcode);
                    super.update(Sync.LOCAL, userEntity);
                }

                final Map<String, String> params = new HashMap<>();
                params.put("name", userEntity.getName());
                params.put("confirmationcode", userEntity.getConfirmationCode());
                params.put("link", "https://www.coffeeshopfinder.de/login?login=" +
                        Utils.getUrlencoded(userEntity.getLogin()) + "?cc=" + confirmationcode);
                emailService.sendEmailByTemplate(userEntity.getEmail(), "confirmationcode", userEntity.getLanguage(), params, null, null);

            } catch (Exception e) {
                log.error("{}", Utils.toString(e));
            }
        });
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
