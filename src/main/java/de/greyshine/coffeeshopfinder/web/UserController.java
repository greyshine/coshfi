package de.greyshine.coffeeshopfinder.web;

import de.greyshine.coffeeshopfinder.entity.UserCrudService;
import de.greyshine.coffeeshopfinder.entity.UserEntity;
import de.greyshine.coffeeshopfinder.service.ValidationService;
import de.greyshine.coffeeshopfinder.utils.Utils;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@RestController
@Slf4j
public class UserController {

    private final ValidationService validationService;
    private final UserCrudService userCrudService;

    public UserController(ValidationService validationService, UserCrudService userCrudService) {
        this.validationService = validationService;
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

        final var userInfo = userCrudService.executeLogin(loginRequestBody.getLogin(), loginRequestBody.getPassword(), loginRequestBody.getConfirmationcode());

        return ResponseEntity.ok(new LoginResponse(userInfo));
    }

    @PostMapping(value = "/api/logout")
    public void logout(HttpServletRequest request) {
        userCrudService.logout(request.getHeader("TOKEN"));
    }

    @PostMapping(value = "/api/login/renew", produces = MediaType.APPLICATION_JSON_VALUE)
    public void renew(@RequestBody RenewRequestBody renewRequestBody) {

        log.info("renew {}", renewRequestBody);

        Utils.validateAndThrow(renewRequestBody);

        userCrudService.resetConfirmationCode(renewRequestBody.email);
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

    public class LoginResponse {

        private final UserCrudService.UserInfo userInfo;

        private LoginResponse(UserCrudService.UserInfo userInfo) {
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
