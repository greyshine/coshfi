package de.greyshine.coffeeshopfinder.web.resultObjects;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class LocationsV1 extends WebResult {

    @Getter
    private final List<LocationV1> data = new ArrayList<>();

    public LocationsV1() {
        super(WebResult.V1);
    }

    public LocationsV1 add(LocationV1 location) {

        if (location != null) {
            data.add(location);
        }

        return this;
    }
}
