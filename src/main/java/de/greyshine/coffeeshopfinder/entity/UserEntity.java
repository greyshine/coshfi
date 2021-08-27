package de.greyshine.coffeeshopfinder.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
public class UserEntity {

    @NotBlank(message = "user is mandatory")
    public String user;
    public String email;
    public String confirmationcode;
    public String password;
    public int badlogins;

    /**
     * ; separated lines of an Address
     */
    public String address;

    @Valid
    public final List<Location> locations = new ArrayList<>(1);

}
