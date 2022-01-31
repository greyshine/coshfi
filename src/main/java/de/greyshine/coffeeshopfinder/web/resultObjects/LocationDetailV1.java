package de.greyshine.coffeeshopfinder.web.resultObjects;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class LocationDetailV1 extends WebResult {

    @Getter
    private final List<LocationV1> neighbours = new ArrayList<>();
    @Getter
    @Setter
    private LocationV1 location;

    public LocationDetailV1(LocationV1 locationV1) {

        super(WebResult.V1);

        this.location = locationV1;
    }
}
