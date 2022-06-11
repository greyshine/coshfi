package de.greyshine.coffeeshopfinder.web;

import de.greyshine.coffeeshopfinder.entity.UserCrudService;
import de.greyshine.coffeeshopfinder.entity.UserEntity;
import de.greyshine.coffeeshopfinder.service.UserService;
import de.greyshine.coffeeshopfinder.service.ValidationService;
import de.greyshine.coffeeshopfinder.utils.Utils;
import de.greyshine.coffeeshopfinder.web.annotation.Tokenized;
import de.greyshine.json.crud.JsonCrudService;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;

@RestController
@Slf4j
public class UserController {

    private final ValidationService validationService;
    private final UserService userService;
    private final UserCrudService userCrudService;

    public UserController(ValidationService validationService, UserService userService, UserCrudService userCrudService) {
        this.validationService = validationService;
        this.userService = userService;
        this.userCrudService = userCrudService;
    }

    @PostMapping(value = "/api/register", produces = MediaType.APPLICATION_JSON_VALUE)
    @SneakyThrows
    public void register(@RequestBody RegisterForm registerForm) {

        Utils.validateAndThrow(registerForm);

        validationService.validateUsername(registerForm.getName());
        validationService.validateLogin(registerForm.getLogin());
        validationService.validatePassword(registerForm.getPassword());
        validationService.validateEmail(registerForm.getEmail());

        userCrudService.create(registerForm.toUserEntity());
    }

    @PostMapping(value = "/api/login", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequestBody loginRequestBody) {

        Utils.validateAndThrow(loginRequestBody);

        try {

            final var userInfo = userService.executeLogin(loginRequestBody.getLogin(), loginRequestBody.getPassword(), loginRequestBody.getConfirmationcode());
            return ResponseEntity.ok(new LoginResponse(userInfo));

        } catch (LoginException loginException) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, Utils.getDefaultString(loginException.getInfo(), () -> ""), loginException);
        }
    }

    @PostMapping(value = "/api/logout")
    public void logout(HttpServletRequest request) {
        userService.logout(request.getHeader("TOKEN"));
    }

    @PostMapping(value = "/api/login/renew", produces = MediaType.APPLICATION_JSON_VALUE)
    public void renewPassword(@RequestBody RenewRequestBody renewRequestBody) {

        log.info("renew {}", renewRequestBody);

        Utils.validateAndThrow(renewRequestBody);

        userService.resetPassword(renewRequestBody.email);
    }

    @GetMapping(value = "/api/user/{login}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Tokenized
    public ResponseEntity<UserDataResponse> getUserData(@PathVariable("login") String login) {

        if (!userService.isUserAllowedWithCurrentToken(login)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        final Optional<UserEntity> userEntityOptional = userCrudService.getByLogin(login);

        return userEntityOptional.map(userEntity -> {

            final var userData = new UserDataResponse();
            userData.setName(userEntity.getName());
            userData.setContactPerson(userEntity.getContactPerson());
            userData.setEmail(userEntity.getEmail());
            userData.setPhone(userEntity.getPhone());
            userData.setLanguage(userEntity.getLanguage());
            userData.setAddress(userEntity.getAddress());

            return ResponseEntity.ok(userData);

        }).get();

    }

    @PostMapping(value = "/api/user")
    @ResponseBody
    @Tokenized
    public void setUserData(@RequestBody UserDataRequestBody userDataRequestBody) {

        log.info("{}", userDataRequestBody);

        Utils.validateAndThrow(userDataRequestBody);

        final String token = userService.getCurrentToken();
        log.info("token={}", token);

        final String login = userService.getUserInfo().get().getLogin();
        final UserEntity userEntity = userCrudService.getByLogin(login).get();

        switch (userDataRequestBody.field) {

            case "name":

                if (isBlank(userDataRequestBody.getName())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }

                final var name = userDataRequestBody.getName().trim();
                final List<UserEntity> userEntities = userCrudService.getByName(name);
                for (UserEntity userEntity2 : userEntities) {
                    if (Utils.isEqualsIgnoreCaseTrimmed(name, userEntity2.getName(), true) && !Utils.isEquals(userEntity.getId(), userEntity2.getId(), true)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is already taken.");
                    }
                }

                userEntity.setName(userDataRequestBody.getName().trim());
                break;

            case "contactPerson":
                userEntity.setContactPerson(trimToNull(userDataRequestBody.getContactPerson()));
                break;

            case "address":

                final List<String> addressLines = new ArrayList<>();
                for (String line : Utils.getDefault(userDataRequestBody.address, () -> new String[0])) {
                    if (isBlank(line)) {
                        continue;
                    }
                    addressLines.add(line);
                }

                if (addressLines.isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Addresslines must not be empty!");
                }

                userEntity.addAddressLines(addressLines);
                break;

            case "email":

                userEntity.setEmail(userDataRequestBody.email);
                userEntity.setConfirmationCode(UserService.createConfirmationCode());

                break;

            case "phone":

                userEntity.setPhone(trimToNull(userDataRequestBody.getPhone()));
                break;

            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown field: " + userDataRequestBody.field);
        }

        userCrudService.update(JsonCrudService.Sync.LOCAL, userEntity);
    }

    @Data
    public static class RegisterForm {

        @NotBlank
        private String login;

        @NotBlank
        private String password;

        @NotBlank
        private String name;

        @NotBlank
        @Email
        private String email;

        public UserEntity toUserEntity() {

            final UserEntity re = new UserEntity();
            re.setName(this.getName());
            re.setLogin(this.getLogin());
            re.setPassword(this.getPassword());
            re.setEmail(this.getEmail());

            return re;
        }
    }

    @Data
    public static class LoginRequestBody {
        @NotBlank
        private String login;
        @NotBlank
        private String password;

        private String confirmationcode;
    }

    @Data
    public static class RenewRequestBody {

        @NotBlank
        private String email;
    }

    @Data
    public static class UserDataRequestBody {

        /**
         * Field determining which property of user is modified
         */
        @NotBlank
        private String field;

        private String name;

        private String[] address;

        private String contactPerson;
        private String phone;
        private String email;

        public void setAddress(String[] addressLines) {

            addressLines = Utils.getDefault(addressLines, () -> new String[0]);

            final var adl = new ArrayList<>(addressLines.length);

            for (String aLine : addressLines) {
                if (isBlank(aLine)) {
                    continue;
                }
                adl.add(aLine.trim());
            }

            address = adl.toArray(new String[adl.size()]);
        }
    }

    @Data
    public static class UserDataResponse {

        private List<String> address = new ArrayList<>(0);
        private String name;
        private String contactPerson;
        private String email;
        private String phone;
        private String language;
    }

    public class LoginResponse {

        private final UserService.UserInfo userInfo;

        private LoginResponse(UserService.UserInfo userInfo) {
            this.userInfo = userInfo;
        }

        public String getToken() {
            return userInfo.getToken();
        }

        public String getLogin() {
            return userInfo.getLogin();
        }

        public Set<String> getRrs() {
            return userInfo.getRrs();
        }
    }
}
