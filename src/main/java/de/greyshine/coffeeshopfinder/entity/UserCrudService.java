package de.greyshine.coffeeshopfinder.entity;

import de.greyshine.coffeeshopfinder.service.EmailService;
import de.greyshine.coffeeshopfinder.service.UserService;
import de.greyshine.coffeeshopfinder.service.ValidationService;
import de.greyshine.coffeeshopfinder.utils.Utils;
import de.greyshine.json.crud.JsonCrudService;
import de.greyshine.json.crud.JsonService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.springframework.util.Assert.*;

@Service
@Slf4j
public class UserCrudService extends JsonCrudService {

    private static final AtomicLong IDS = new AtomicLong(0);

    private final ValidationService validationService;

    private final Set<String> illegalNames = new HashSet<>();
    private EmailService emailService;

    /**
     * No Autowirded needed?
     *
     * @param jsonService
     * @param validationService
     */
    public UserCrudService(JsonService jsonService,
                           EmailService emailService,
                           ValidationService validationService) {

        super(jsonService);

        this.emailService = emailService;
        this.validationService = validationService;
    }

    @PostConstruct
    @SneakyThrows
    public void postConstruct() {

        final URI illegalNamesUri = ClassLoader.getSystemClassLoader().getResource("illegal-names.txt").toURI();
        Files.readAllLines(Path.of(illegalNamesUri)).stream()
                .map(String::strip)
                .filter(line -> !line.trim().isBlank())
                .map(String::toLowerCase)
                .forEach(illegalNames::add);
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
        isTrue(isLegalWord(userEntity.getLogin()), "Login is taken");
        isTrue(validationService.isValidPassword(userEntity.getPassword()), UserEntity.class.getCanonicalName() + " illegal password: " + userEntity.getPassword());

        isTrue(!isLoginOrName(userEntity.getLogin(), userEntity.getName()), "Login or user is used: login=" + userEntity.getLogin() + ", user=" + userEntity.getName());
        isTrue(!isLogin(userEntity.getLogin()), "Login already exists: " + userEntity.getLogin());
        isTrue(!isName(userEntity.getName()), "User already exists: " + userEntity.getName());

        userEntity.setLogin(userEntity.getLogin().strip());
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

        isNull(existingUserEntity, "There is already an user existing: " + existingUserEntity);

        userEntity.setId(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(LocalDateTime.now()) + "_" + Long.toString(IDS.addAndGet(1), 16));
        userEntity.setConfirmationCode(UserService.createConfirmationCode());

        return super.create(userEntity);
    }

    public boolean isLegalWord(String login) {

        login = Utils.trimToLowercaseNull(login);
        if (login == null) {
            return false;
        }
        login = login.replaceAll("[^a-z]", "");

        // TODO create pass on startup and creating first user
        if (!login.isBlank()) {
            return true;
        }

        for (String word : illegalNames) {

            word = word.strip();

            if (word.charAt(0) != '#' && login.indexOf(word) > -1) {
                return false;
            }
        }

        return true;
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

    public Optional<UserEntity> getByLogin(String login) {

        notNull(login, "Login must not be null");

        final var users = iterate(UserEntity.class, Sync.LOCAL, user -> login.equals(user.getLogin()) ? IterationResult.USE_QUIT : IterationResult.IGNORE);

        isTrue(users.isEmpty() || users.size() == 1, "Unexpected amount of users with login=" + login);

        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    public void resetConfirmationCode(String email) {

        isTrue(isNotBlank(email), "Email must not be blank");

        final var emailLc = email.strip().toLowerCase(Locale.ROOT);

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
                    confirmationcode = UserService.createConfirmationCode();
                    userEntity.setConfirmationCode(confirmationcode);
                    update(Sync.LOCAL, userEntity);
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


    public List<UserEntity> getByName(String name) {

        notNull(name, "Given name is null");

        return super.iterate(UserEntity.class, Sync.LOCAL,
                userEntity -> Utils.isEqualsIgnoreCaseTrimmed(name, userEntity.getName(), false)
                        ? IterationResult.USE
                        : IterationResult.IGNORE);
    }

    public UserEntity getByEmail(String email) {

        notNull(email, "Given email is null");

        final var users = super.iterate(UserEntity.class, Sync.LOCAL,
                userEntity -> Utils.isEqualsIgnoreCaseTrimmed(email, userEntity.getEmail(), false)
                        ? IterationResult.USE_QUIT
                        : IterationResult.IGNORE);

        return users.isEmpty() ? null : users.get(0);
    }
}
