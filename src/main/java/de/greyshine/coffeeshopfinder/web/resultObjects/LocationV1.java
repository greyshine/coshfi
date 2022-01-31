package de.greyshine.coffeeshopfinder.web.resultObjects;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LocationV1 {

    private String id;
    private String ownerId;

    private String name;

    private BigDecimal lon;
    private BigDecimal lat;

    private String type;

    private String address;
    private String www;
    private String email;
    private String phone;


    public void setLat(String lat) {
        setLat(lat == null ? null : new BigDecimal(lat));
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public void setLon(String lon) {
        setLon(lon == null ? null : new BigDecimal(lon));
    }

    public void setLon(BigDecimal lon) {
        this.lon = lon;
    }

}
