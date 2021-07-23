package de.greyshine.coffeeshopfinder.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class MapController {

    @GetMapping( value = "/api/v1/locations", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<LocationV1> locations() {

        final ArrayList<LocationV1> results = new ArrayList<>();

        final LocationV1 l1= new LocationV1();
        l1.setName("LOC:DELFT");
        l1.setLat( "52.012191" );
        l1.setLon( "4.359729" );
        results.add(l1);

        final LocationV1 l2= new LocationV1();
        l1.setName("LOC:OceanFront");
        l1.setLat( "52.098444" );
        l1.setLon( "4.263856" );
        results.add(l2);

        return results;
    }


}
