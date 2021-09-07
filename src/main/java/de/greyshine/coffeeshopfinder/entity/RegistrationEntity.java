package de.greyshine.coffeeshopfinder.entity;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@ToString( callSuper = true )
public class RegistrationEntity extends Entity {

    @NotBlank(message = "login is mandatory")
    private String login;
    @NotBlank(message = "password is mandatory")
    private String password;
    @NotBlank(message = "name is mandatory")
    private String name;
    @NotBlank(message = "email is mandatory")
    private String email;
}
