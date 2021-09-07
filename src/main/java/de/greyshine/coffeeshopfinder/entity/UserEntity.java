package de.greyshine.coffeeshopfinder.entity;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
@ToString(callSuper = true)
public class UserEntity extends Entity {

    @NotBlank(message = "login is mandatory")
    private String login;

    @NotBlank(message = "password is mandatory")
    public String password;

    @NotBlank(message = "email is mandatory")
    private  String email;

    public String confirmationcode;
    public int badlogins;

    private String name;
    /**
     * ';' separated lines of an Address
     */
    public String address;

    @Valid
    public final List<Location> locations = new ArrayList<>(1);
}
