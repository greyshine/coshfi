package de.greyshine.coffeeshopfinder.web;

import de.greyshine.coffeeshopfinder.entity.RegistrationCrudService;
import de.greyshine.coffeeshopfinder.entity.RegistrationEntity;
import de.greyshine.coffeeshopfinder.service.ValidationService;
import de.greyshine.coffeeshopfinder.utils.Utils;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private ValidationService validationService;

    @Autowired
    private RegistrationCrudService registrationCrudService;

    @PostMapping(value = "/api/register", produces = MediaType.APPLICATION_JSON_VALUE)
    @SneakyThrows
    public void register(@RequestBody RegisterForm registerForm) {

        Utils.validateAndThrow(registerForm);

        validationService.validateUsername(registerForm.getName());
        validationService.validateLogin(registerForm.getLogin());
        validationService.validatePassword(registerForm.getPassword());

        registrationCrudService.create(registerForm.toRegistrationEntity());
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

        public RegistrationEntity toRegistrationEntity() {

            final RegistrationEntity re = new RegistrationEntity();
            re.setName(this.getName());
            re.setLogin(this.getLogin());
            re.setEmail(this.getEmail());
            re.setPassword(this.getPassword());

            return re;
        }
    }
}
