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
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static de.greyshine.coffeeshopfinder.utils.Utils.*;
import static org.apache.commons.lang3.StringUtils.*;
import static org.springframework.util.Assert.*;

@Service
@Slf4j
public class UserCrudService extends JsonCrudService {

    private static final AtomicLong IDS = new AtomicLong(0);
    private static final Map<String, UserInfo> activeUserMap = new HashMap<>();
    private static final int MAX_BAD_LOGINS = 6;

    private final EmailService emailService;
    private final ValidationService validationService;
    private final long tokenInfoTimeToLive = 10 * 60 * 1000;

    public UserCrudService(@Autowired JsonService jsonService, @Autowired ValidationService validationService, @Autowired EmailService emailService) {

        super(jsonService);

        this.emailService = emailService;
        this.validationService = validationService;
    }

    public static Map<String, UserInfo> getTokenMap() {
        return activeUserMap;
    }

    /**
     * Notice. tha password will be hashed
     *
     * @param userEntity
     * @return
     */
    public String create(UserEntity userEntity) {

        notNull(userEntity, UserEntity.class.getCanonicalName() + " is null");
        isNull(userEntity.getId(), UserEntity.class.getCanonicalName() + " is null");
        isTrue(validationService.isValidLogin(userEntity.getLogin()), UserEntity.class.getCanonicalName() + " illegal login: " + userEntity.getLogin());
        isTrue(validationService.isValidPassword(userEntity.getPassword()), UserEntity.class.getCanonicalName() + " illegal password: " + userEntity.getPassword());

        isTrue(!isLoginOrName(userEntity.getLogin(), userEntity.getName()), "Login or user is used: login=" + userEntity.getLogin() + ", user=" + userEntity.getName());

        isTrue(!isLogin(userEntity.getLogin()), "Login already exists: " + userEntity.getLogin());
        isTrue(!isName(userEntity.getName()), "User already exists: " + userEntity.getName());

        userEntity.setEmail(userEntity.getEmail().toLowerCase(Locale.ROOT));
        userEntity.setPassword(Utils.toHashPassword(userEntity.getPassword()));

        final UserEntity existingUserEntity = iterateSingle(UserEntity.class, Sync.LOCAL, user -> {

            var isHit = Utils.isEqualsIgnoreCaseTrimmed(userEntity.getLogin(), user.getLogin(), false);
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

    public boolean isLoginOrName(final String login, final String name) {

        isTrue(trimToNull(login) != null || trimToNull(name) != null, "Login and User are null");

        final var userEntities = super.iterate(UserEntity.class, Sync.LOCAL, (userEntity) -> {

            if (Utils.isEqualsTrimmed(userEntity.getLogin(), login, false)) {
                return IterationResult.USE_QUIT;
            } else if (Utils.isEqualsTrimmed(userEntity.getName(), name, false)) {
                return IterationResult.USE_QUIT;
            }

            return IterationResult.IGNORE;
        });

        return !userEntities.isEmpty();
    }

    public String createConfirmationCode() {

        final var sb = new StringBuffer();

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
    public UserInfo executeLogin(String login, String password, String confirmationcode) {

        if (isBlank(login) || isBlank(password)) {
            throw new LoginException(login);
        }

        final var userEntity = iterateSingle(UserEntity.class, Sync.GLOBAL,
                user -> login.equalsIgnoreCase(user.getLogin()) ? IterationResult.USE_QUIT : IterationResult.IGNORE);

        var doUpdate = false;

        if (userEntity == null) {

            throw new LoginException(login);

        } else if (!Utils.isEquals(userEntity.getPassword(), Utils.toHashPassword(password), false)) {

            log.info("in: {} -> {}", password, Utils.toHashPassword(password));
            log.info("user: {}", userEntity.getPassword());

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

        final var userInfo = new UserInfo(userEntity.getLogin(), userEntity.getRightsAndRoles());
        executeSynced(activeUserMap, () -> activeUserMap.put(userInfo.getToken(), userInfo));
        log.info("created {}", userInfo);

        return userInfo;
    }

    @SneakyThrows
    public Optional<UserInfo> getTokenInfo(String token) {
        return executeSynced(activeUserMap, () -> Optional.ofNullable(activeUserMap.get(token)));
    }

    public void removeInvalidTokens() throws Exception {

        executeSynced(activeUserMap, () -> {

            activeUserMap.values().forEach(tokenInfo -> {

                final var isValid = executeSafe(() -> tokenInfo.isAccessible(), null);

                if (Boolean.TRUE.equals(isValid)) {
                    return;
                } else if (isValid == null) {
                    log.error("Failure on getting valid state of token: " + tokenInfo);
                    return;
                }

                activeUserMap.remove(tokenInfo.getToken());
                log.info("removed invalid token: {}", tokenInfo);
            });

        });
    }

    @SneakyThrows
    public boolean logout(String token) {

        final var userInfo = executeSynced(activeUserMap, () -> activeUserMap.remove(token));

        log.info("logout: {} -> {}", token, userInfo != null);

        return userInfo != null;
    }

    public UserEntity getByLogin(String login) {

        Assert.notNull(login, "Login must not be null");

        final var users = iterate(UserEntity.class, Sync.LOCAL, user -> login.equals(user.getLogin()) ? IterationResult.USE_QUIT : IterationResult.IGNORE);

        Assert.isTrue(users.isEmpty() || users.size() == 1, "Unexpected amount of users with login=" + login);

        return users.isEmpty() ? null : users.get(0);
    }

    @SneakyThrows
    public boolean updateUserInfo(String token) {

        final var userInfo = activeUserMap.get(token);

        return userInfo != null && userInfo.updateLastAccess();
    }

    public void resetConfirmationCode(String email) {

        isTrue(isNotBlank(email), "Email must not be blank");

        final var emailLc = email.trim().toLowerCase(Locale.ROOT);

        final var userEntities =
                iterate(UserEntity.class, Sync.LOCAL, (userEntity) -> emailLc.equals(userEntity.getEmail().toLowerCase(Locale.ROOT)) ? IterationResult.USE_QUIT : IterationResult.IGNORE_QUIT);

        if (userEntities.isEmpty()) {

            log.info("no user found for email: {}", email);
            return;
        }

        userEntities.forEach(userEntity -> {

            try {

                var confirmationcode = userEntity.getConfirmationCode();

                if (confirmationcode == null) {
                    confirmationcode = createConfirmationCode();
                    userEntity.setConfirmationCode(confirmationcode);
                    super.update(Sync.LOCAL, userEntity);
                }

                final var params = new HashMap<String, String>();
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
    public class UserInfo {

        private final LocalDateTime creation = LocalDateTime.now();
        private final String token = getRandomLetters(32 * 4);

        @NonNull
        private final String login;

        /**
         * Rights and roles
         */
        private final Set<String> rrs = new HashSet<>();

        @Setter(AccessLevel.NONE)
        private long lastAccess = System.currentTimeMillis();

        public UserInfo(String login, Set<String> rightsAndRoles) {
            this.login = login;
            rightsAndRoles.forEach(rrs::add);
        }

        public boolean updateLastAccess() throws Exception {

            if (!isAccessible()) {
                return false;
            }

            lastAccess = System.currentTimeMillis();
            return true;
        }


        public boolean isAccessible() {

            if (!activeUserMap.containsKey(token)) {
                return false;
            } else if (lastAccess + tokenInfoTimeToLive < System.currentTimeMillis()) {
                activeUserMap.remove(token);
                return false;
            } else {
                return true;
            }
        }
    }
}
