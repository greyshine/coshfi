package de.greyshine.coffeeshopfinder.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class MapController {

    // /api/v1/locations
    @PostMapping( value = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<LocationV1> locations() {

        final ArrayList<LocationV1> results = new ArrayList<>();

        final LocationV1 l1= new LocationV1();
        l1.setName("LOC:DELFT");
        l1.setLat( "52.012191" );
        l1.setLon( "4.359729" );
        results.add(l1);

        final LocationV1 l2= new LocationV1();
        l2.setName("LOC:OceanFront");
        l2.setLat( "52.098444" );
        l2.setLon( "4.263856" );
        results.add(l2);

        return results;
    }


}
