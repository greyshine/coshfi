package de.greyshine.coffeeshopfinder.web;

import de.greyshine.coffeeshopfinder.utils.Utils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@Slf4j
public class UserController {

    @PostMapping(value = "/api/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public void register(@RequestBody RegisterForm registerForm) {

        log.info("FORM {}", registerForm);
        Utils.validateAndThrow(registerForm);

    }

    @Data
    public static class RegisterForm {

        @NotBlank
        private String name;

        @NotBlank
        private String password;

        @NotEmpty
        private List<String> address;

        @NotBlank
        @Email
        private String email;
    }
}
