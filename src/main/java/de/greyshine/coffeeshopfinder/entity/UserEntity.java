package de.greyshine.coffeeshopfinder.entity;

import de.greyshine.json.crud.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserEntity extends Entity {

    @NotBlank(message = "login is mandatory")
    private String login;

    @NotBlank(message = "password is mandatory")
    public String password;

    @NotBlank(message = "email is mandatory")
    private  String email;

    public String confirmationCode;
    public int badlogins;

    private String name;
    /**
     * ';' separated lines of an Address
     */
    private String address;

    private String language;

    @Valid
    public final List<Location> locations = new ArrayList<>(1);

    public void increaseBadLogins() {
        badlogins++;
    }


}
