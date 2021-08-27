package de.greyshine.coffeeshopfinder.entity;

import de.greyshine.coffeeshopfinder.utils.Latlon;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Slf4j
public class Location {

    @NotBlank(message = "id is mandatory")
    private String id;
    private String name;
    /**
     * ; separated lines of an Address
     */
    private String address;
    private String www;
    private String email;
    private String description;

    @Valid
    private Latlon latlon;
    @NotNull
    private EType type;


    public enum EType {

        Shop,
        Bar,
        Pharmacy,

    }
}
