package de.greyshine.coffeeshopfinder.entity;

import de.greyshine.coffeeshopfinder.utils.Latlon;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Slf4j
public class Location extends Entity {

    @NotBlank(message = "id is mandatory")
    private String id;
    private String name;
    /**
     * ; separated lines of an Address
     */
    private String address;
    private String phone;
    private String www;
    private String instagram;
    private String twitter;
    private String tiktok;
    private String facebook;

    private String email;
    private String description;

    @Valid
    private Latlon latlon;
    @NotNull
    private EType type;

    private String openingtimes;

    public enum EType {
        Shop,
        Bar,
        Pharmacy,
    }
}
