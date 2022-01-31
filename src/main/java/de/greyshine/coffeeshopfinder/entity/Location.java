package de.greyshine.coffeeshopfinder.entity;

import de.greyshine.json.crud.Entity;
import de.greyshine.latlon.Latlon;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
@ToString(callSuper = true)
public class Location extends Entity {

    private String ownerId;
    /**
     * street + housenumber or housenumber + street
     */
    private String street;

    private String name;
    private String zip;
    private String city;
    /**
     * Like US=USA, CA=Canada, DE=Germany
     */
    private String country;
    /**
     * Or some kind of area
     */
    private String state;
    private String youtube;
    private String phone;
    private String www;
    private String instagram;
    private String twitter;
    private String tiktok;
    private String facebook;
    private String extra;

    private String email;
    private String description;

    @Valid
    private Latlon latlon;

    @NotNull
    private EType type;

    private String openingtimes;

    public enum EType {
        SHOP,
        BAR,
        PHARMACY,
        DOCTOR
    }
}
