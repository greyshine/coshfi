package de.greyshine.coffeeshopfinder.entity;

import de.greyshine.json.crud.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
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
