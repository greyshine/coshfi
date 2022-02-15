package de.greyshine.coffeeshopfinder.web;

import de.greyshine.coffeeshopfinder.entity.Location;
import de.greyshine.coffeeshopfinder.service.location.LocationsService;
import de.greyshine.coffeeshopfinder.web.builder.AddressBuilder;
import de.greyshine.coffeeshopfinder.web.resultObjects.LocationDetailV1;
import de.greyshine.coffeeshopfinder.web.resultObjects.LocationV1;
import de.greyshine.coffeeshopfinder.web.resultObjects.LocationsV1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

import static de.greyshine.coffeeshopfinder.utils.Utils.isEqualsTrimmed;
import static de.greyshine.latlon.Latlon.latlon;

@RestController
@Slf4j
public class MapController {

    private final LocationsService locationsService;
    private final ExceptionHandling exceptionHandling;

    public MapController(@Autowired LocationsService locationsService, @Autowired ExceptionHandling exceptionHandling) {
        this.locationsService = locationsService;
        this.exceptionHandling = exceptionHandling;
    }

    @GetMapping(value = "/api/v1/location/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public LocationDetailV1 fetchLocation(@PathVariable(value = "id") String id) throws IOException {

        final var location = locationsService.get(id);
        if (location == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "location.id=" + id);
        }

        Assert.notNull(location, "No location found (id=" + id + ")");

        final var locationDetailV1 = new LocationDetailV1(location2LocationV1(location));

        final var ownerId = locationDetailV1.getLocation().getOwnerId();

        for (Location locationByOwner : locationsService.getByOwner(ownerId)) {

            // exclude the primary hit itself
            if (isEqualsTrimmed(location.getId(), locationByOwner.getId(), false)) {
                continue;
            }

            locationDetailV1.getNeighbours().add(location2LocationV1(locationByOwner));
        }

        return locationDetailV1;
    }

    @GetMapping(value = "/api/v1/locations", produces = MediaType.APPLICATION_JSON_VALUE)
    public LocationsV1 fetchLocations(@RequestParam("nw") String nw, @RequestParam("se") String se, @RequestParam(value = "z") int zoom) throws IOException {

        log.info("fetchLocations nw={}, se={}, z={}", nw, se, zoom);

        final var locations = new LocationsV1();

        // TODO: remove false in IF
        if (false && zoom < 7) {
            return locations;
        }

        final var northWest = latlon(nw);
        final var southEast = latlon(se);

        // TODO Locations kriegen ein Marker wo der Datenbestand herkommt. zum beispiel Unkrautkarten, Google oder
        //  selbst eingetragen.

        // je nach Marker wird bei einem bestimmten zoom level < 8 nicht mehr angezeigt
        locationsService.handleAll(location -> {

            final var locationV1 = location2LocationV1(location);
            locations.add(locationV1);

            return true;

        }, LocationsService.Parameter.getByBoxCoordinates(northWest, southEast));

        return locations;
    }

    private LocationV1 location2LocationV1(Location location) {

        final var locationV1 = new LocationV1();

        log.info("location2LocationV1 {}", location);

        locationV1.setId(location.getId());
        locationV1.setOwnerId(location.getOwnerId());
        locationV1.setName(location.getName());

        locationV1.setLat(location.getLatlon().getLat());
        locationV1.setLon(location.getLatlon().getLon());

        AddressBuilder.build(location, locationV1);

        return locationV1;
    }
}
