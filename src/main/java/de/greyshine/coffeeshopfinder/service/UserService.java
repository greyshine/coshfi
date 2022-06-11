package de.greyshine.coffeeshopfinder.service;

import de.greyshine.coffeeshopfinder.entity.UserCrudService;
import de.greyshine.coffeeshopfinder.entity.UserEntity;
import de.greyshine.coffeeshopfinder.utils.Utils;
import de.greyshine.coffeeshopfinder.web.LoginException;
import de.greyshine.json.crud.JsonCrudService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static de.greyshine.coffeeshopfinder.utils.Utils.*;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

@Service
@Slf4j
public class UserService {

    private static final ThreadLocal<String> TL_TOKENS = new ThreadLocal<>();
    /**
     * Map\<token,UserInfo\>
     */
    private static final Map<String, UserInfo> activeUserMap = new HashMap<>();
    private static final int MAX_BAD_LOGINS = 6;
    private static final String PASSWORD_LCS = "abcdefghijklmnopqrstuvwxyz";
    private static final String PASSWORD_UCS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String PASSWORD_NUMERICS = "0123456789";
    private static final String PASSWORD_SPECIALS = "!\"§$%&/()=?+*#'<>,;.:-_@[]|";
    private static final String PASSWORDCHARS = PASSWORD_LCS + PASSWORD_UCS + PASSWORD_NUMERICS + PASSWORD_SPECIALS;
    private final long tokenInfoTimeToLive = 10 * 60 * 1000;
    private final UserCrudService userCrudService;
    private final ValidationService validationService;
    private final EmailService emailService;
    private boolean isTerminated = false;

    public UserService(UserCrudService userCrudService, ValidationService validationService, EmailService emailService) {
        this.userCrudService = userCrudService;
        this.validationService = validationService;
        this.emailService = emailService;
    }

    public static String createPassword() {

        int pwdLength = 15;
        var password = Utils.getRandom(PASSWORDCHARS, pwdLength);

        var isValid = false;
        while (!isValid) {

            var lowerCaseIndex = getRandom(0, pwdLength - 1);
            var upperCaseIndex = getRandom(0, pwdLength - 1);

            if (upperCaseIndex == lowerCaseIndex) {
                continue;
            }

            var numberIndex = getRandom(0, pwdLength - 1);
            if (numberIndex == upperCaseIndex || numberIndex == lowerCaseIndex) {
                continue;
            }

            var specialIndex = getRandom(0, password.length() - 1);
            if (specialIndex == upperCaseIndex || specialIndex == lowerCaseIndex || specialIndex == numberIndex) {
                continue;
            }

            password = password.substring(0, lowerCaseIndex) + getRandom(PASSWORD_LCS, 1) + password.substring(lowerCaseIndex + 1);
            password = password.substring(0, upperCaseIndex) + getRandom(PASSWORD_UCS, 1) + password.substring(upperCaseIndex + 1);
            password = password.substring(0, numberIndex) + getRandom(PASSWORD_NUMERICS, 1) + password.substring(numberIndex + 1);
            password = password.substring(0, specialIndex) + getRandom(PASSWORD_SPECIALS, 1) + password.substring(specialIndex + 1);

            isValid = true;
        }

        return password;
    }

    public static String createConfirmationCode() {

        final var sb = new StringBuilder();

        for (int i = 0, l = Utils.getRandom(3, 5); i < l; i++) {
            sb.append(Utils.getRandomLetter());
        }

        sb.append('-');

        for (int i = 0, l = Utils.getRandom(3, 6); i < l; i++) {
            sb.append(Utils.getRandomLetter());
        }

        return sb.toString();
    }

    @PostConstruct
    @SneakyThrows
    public void postConstruct() {

        final var executor = Executors.newFixedThreadPool(1);
        executor.submit(() -> {

            while (!isTerminated) {
                Utils.sleep(6000, () -> isTerminated);
                removeInvalidTokens();
            }
        });
    }

    @PreDestroy
    public void preDestroy() {
        isTerminated = true;
    }

    public String getCurrentToken() {
        return TL_TOKENS.get();
    }

    /**
     * Sets the current thread associated token.
     *
     * @param token
     */
    public void setCurrentToken(String token) {

        if (isBlank(token)) {
            TL_TOKENS.remove();
        } else {
            TL_TOKENS.set(token);
        }
    }

    @SneakyThrows
    public UserInfo executeLogin(String login, String password, String confirmationcode) {

        if (isBlank(login) || isBlank(password)) {
            throw new LoginException(login);
        }

        final var userEntity = userCrudService.iterateSingle(UserEntity.class, JsonCrudService.Sync.GLOBAL,
                user -> login.equalsIgnoreCase(user.getLogin()) ? JsonCrudService.IterationResult.USE_QUIT : JsonCrudService.IterationResult.IGNORE);

        var doUpdate = false;

        if (userEntity == null) {

            throw new LoginException(login);

        } else if (!Utils.isEquals(userEntity.getPassword(), Utils.toHashPassword(password), false)) {

            log.info("in: {} -> {}", password, Utils.toHashPassword(password));
            log.info("user: {}", userEntity.getPassword());

            userEntity.increaseBadLogins();
            userCrudService.update(JsonCrudService.Sync.GLOBAL, userEntity);
            throw new LoginException(login);

        } else if (userEntity.getBadlogins() > MAX_BAD_LOGINS) {

            userEntity.increaseBadLogins();
            userCrudService.update(JsonCrudService.Sync.GLOBAL, userEntity);
            throw new LoginException(login, "BAD_LOGINS");

        } else if (userEntity.getConfirmationCode() != null && !userEntity.getConfirmationCode().equals(confirmationcode)) {

            throw new LoginException(login, "CONFIRMATION_CODE");
        }

        userEntity.setConfirmationCode(null);
        userEntity.setBadlogins(0);
        userEntity.updateLastLogin();

        userCrudService.update(JsonCrudService.Sync.GLOBAL, userEntity);

        final var userInfo = new UserInfo(userEntity.getLogin(), userEntity.getRightsAndRoles());
        executeSynced(activeUserMap, () -> activeUserMap.put(userInfo.getToken(), userInfo));
        log.info("created {}", userInfo);

        return userInfo;
    }

    @SneakyThrows
    public boolean logout(String token) {

        final var userInfo = executeSynced(activeUserMap, () -> activeUserMap.remove(token));

        log.info("logout: {} -> {}", token, userInfo != null);

        return userInfo != null;
    }

    public boolean isUserAllowedWithCurrentToken(String user) {

        if (isBlank(user)) {
            return false;
        }

        final Optional<UserInfo> userInfoOptional = getUserInfo();
        if (userInfoOptional.isEmpty()) {
            return false;
        }
        ;

        return Utils.isEquals(getCurrentToken(), userInfoOptional.get().getToken(), false);
    }

    /**
     * @return UserInfo reagrding the current Thread
     */
    public Optional<UserInfo> getUserInfo() {
        return getUserInfo(TL_TOKENS.get());
    }

    @SneakyThrows
    public Optional<UserInfo> getUserInfo(String token) {
        return executeSynced(activeUserMap, () -> Optional.ofNullable(activeUserMap.get(token)));
    }

    public int removeInvalidTokens() {
        final AtomicInteger i = new AtomicInteger(0);
        synchronized (activeUserMap) {
            new HashSet<>(activeUserMap.values()).stream()
                    .filter(userInfo -> !userInfo.isTokenTimeValid())
                    .peek(userInfo -> log.info("removing invalid token: {}", userInfo))
                    .peek(userInfo -> i.addAndGet(1))
                    .map(userInfo -> userInfo.getToken())
                    .forEach(token -> activeUserMap.remove(token));
        }

        if (i.get() > 0) {
            log.info("Removed {} UserInfos", i.get());
        }

        return i.get();
    }

    @SneakyThrows
    public boolean updateUserInfo(String token) {

        final var userInfo = activeUserMap.get(token);

        return userInfo != null && userInfo.updateLastAccess();
    }

    public void resetPassword(String email) {

        isTrue(isNotBlank(email), "Provided email is blank");

        final UserEntity userEntity = userCrudService.getByEmail(email);

        notNull(userEntity, "No user by email: " + email);

        final String password = UserService.createPassword();
        userEntity.setPassword(Utils.toHashPassword(password));

        userCrudService.update(JsonCrudService.Sync.GLOBAL, userEntity);

        final var params = new HashMap<String, String>();
        params.put("name", userEntity.getName());
        params.put("passsword", password);
        params.put("link", "https://www.coffeeshopfinder.de/login?login=" + Utils.getUrlencoded(userEntity.getLogin()));

        emailService.sendEmailByTemplate(userEntity.getEmail(), "passwordreset", userEntity.getLanguage(), params, null, null);
    }

    @Data
    @RequiredArgsConstructor
    public class UserInfo {

        private final LocalDateTime creation = LocalDateTime.now();
        private final String token;

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
            this.token = buildToken(login);

            rightsAndRoles.forEach(rrs::add);
        }

        public boolean updateLastAccess() {

            if (!isTokenTimeValid()) {
                return false;
            }

            lastAccess = System.currentTimeMillis();
            return true;
        }

        private String buildToken(String login) {

            isTrue(isNotBlank(login), "login must not be blank");

            final long s = System.currentTimeMillis();

            final var sb = new StringBuilder();

            for (byte b : login.getBytes(StandardCharsets.UTF_8)) {
                sb.append(b < 0 ? b * -1 : b);
            }
            sb.append(login.length());

            final var l = Long.valueOf(sb.toString());
            sb.setLength(0);

            sb.append(Long.toString(l, 16));
            sb.append(Utils.getRandom(0, 1) == 0 ? '-' : '/');
            sb.append(Long.toString(System.currentTimeMillis(), 16));
            sb.append(Utils.getRandom(0, 1) == 0 ? '-' : '/');
            sb.append(getRandomLetters(32 * 3));

            final String token = sb.toString();

            log.info("token build time: {}ms", System.currentTimeMillis() - s);

            return token;
        }

        public boolean isTokenTimeValid() {

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
