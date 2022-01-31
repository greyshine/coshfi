package de.greyshine.coffeeshopfinder.service.location;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import de.greyshine.coffeeshopfinder.entity.Location;
import de.greyshine.coffeeshopfinder.utils.Utils;
import de.greyshine.latlon.Latlon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@Slf4j
public class WmLocationsService {

    private final File fileLocations;

    private final List<Location> locations = new ArrayList<>();

    public WmLocationsService(@Value("${dir.locations}") File fileLocations) throws IOException {

        log.info("${dir.locations}={}", fileLocations);

        Assert.notNull(fileLocations, "--dir.locations=<PATH> path is null");
        Assert.isTrue(fileLocations.isFile() && fileLocations.canRead(), "fileLocations is not accessible: " + Utils.toString(() -> fileLocations.getAbsoluteFile(), true));

        this.fileLocations = fileLocations.getAbsoluteFile();

        reloadLocations();
    }

    public final static GsonBuilder registerTypeAdapter(GsonBuilder gsonBuilder) {

        gsonBuilder.registerTypeAdapter(Location.class, (JsonDeserializer<Location>) (jsonElement, type, jsonDeserializationContext) -> {

            final JsonObject locationJson = jsonElement.getAsJsonObject();

            final Location location = new Location();
            location.setName(locationJson.get("name").getAsString());
            location.setLatlon(Latlon.latlon(locationJson.get("latlon").getAsString()));
            location.setCountry(locationJson.get("country").getAsString());
            location.setState(locationJson.get("state").getAsString());
            location.setType(lookupType(locationJson.get("type").getAsString()));
            location.setStreet(locationJson.get("street").getAsString());
            location.setZip(locationJson.get("zip").getAsString());
            location.setCity(locationJson.get("city").getAsString());

            return location;
        });

        return gsonBuilder;
    }

    private static Location.EType lookupType(String type) {

        if (isBlank(type)) {
            return null;
        }

        switch (type.trim().toLowerCase(Locale.ROOT)) {

            case "delivery":
            case "store":
                return Location.EType.SHOP;
            case "doctor":
                return Location.EType.DOCTOR;
            case "dispensary": // Ausgabestelle?
            default:
                return null;
        }
    }

    private void reloadLocations() throws IOException {

        final long starttime = System.currentTimeMillis();

        final String jsonString = Files.readString(fileLocations.toPath(), StandardCharsets.UTF_8);

        final Gson gson = registerTypeAdapter(new GsonBuilder()).create();

        final LocationsService.DataWrapper dw = gson.fromJson(jsonString, LocationsService.DataWrapper.class);

        final List<Location> locations = enhanceLocations(dw.getLocations());

        this.locations.clear();
        this.locations.addAll(locations);

        final boolean doFake = true;
        if (doFake) {

            Utils.AtomicObject<String> id = new Utils.AtomicObject();
            this.locations.forEach(l -> {

                if (!"DE".equals(l.getCountry()) || !"Erfurt".equals(l.getCity())) {
                    return;
                }

                if (id.isNull()) {

                    id.setObject(l.getOwnerId());

                } else {

                    log.warn("Overwriting ownerId {} -> {}; {}", l.getOwnerId(), id.getObject(), l);
                    l.setOwnerId(id.getObject());
                }
            });

        }

        final long runtime = System.currentTimeMillis() - starttime;
        log.info("loaded {} locations: {}ms; (created={})", locations.size(), runtime, dw.created);
    }

    private List<Location> enhanceLocations(List<Location> locations) {

        final long starttime = System.currentTimeMillis();

        final List<Location> newLocations = new ArrayList<>();

        int removals = 0;

        for (Location location : locations) {

            if (location == null) {
                removals++;
                continue;
            }

            if (isBlank(location.getName())) {

                log.warn("Location without name: {}", location);
                removals++;
                continue;

            } else if (location.getName().toLowerCase(Locale.ROOT).contains("test")) {
                //log.warn("Location with 'test' in name: {}", location);
                removals++;
                continue;
            }

            location.setStreet(isBlank(location.getStreet()) || "null".equalsIgnoreCase(location.getStreet().trim()) ? null : location.getStreet().trim());
            location.setZip(isBlank(location.getZip()) || "null".equalsIgnoreCase(location.getZip().trim()) ? null : location.getZip().trim());
            location.setCity(isBlank(location.getCity()) || "null".equalsIgnoreCase(location.getCity().trim()) ? null : location.getCity().trim());
            location.setState(isBlank(location.getState()) || "null".equalsIgnoreCase(location.getState().trim()) ? null : location.getState().trim());
            location.setCountry(isBlank(location.getCountry()) || "null".equalsIgnoreCase(location.getCountry().trim()) ? null : location.getCountry().trim());
            location.setEmail(isBlank(location.getEmail()) || location.getEmail().toLowerCase(Locale.ROOT).contains("@weedmaps.") ? null : location.getEmail().trim());

            if (location.getCountry() != null) {

                //log.debug( "checking country: {}", location.getCountry().toLowerCase(Locale.ROOT) );

                switch (location.getCountry().toLowerCase(Locale.ROOT)) {

                    case "(select a country)":
                        location.setCountry(null);
                        break;
                    case "canada":
                    case "cananda":
                    case "canda":
                        location.setCountry("CA");
                        break;
                    case "usa":
                    case "united states":
                    case "unitied states":
                    case "austin":
                    case "ao":
                        location.setCountry("US");
                        break;
                    case "spain":
                    case "tenerife":
                        location.setCountry("ES");
                        break;
                    case "germany":
                    case "deutschland":
                        location.setCountry("DE");
                        break;
                    case "uruguay":
                        location.setCountry("UY");
                        break;
                }

                if (isBlank(location.getCountry()) && "CA".equals(location.getState()) && location.getZip().trim().startsWith("9")) {

                    location.setCountry("US");

                } else if (isBlank(location.getCountry()) && "PR".equals(location.getState())) {

                    location.setCountry("PR");
                    location.setState(null);

                } else if (isBlank(location.getCountry())) {
                    log.info("BLANK COUNTRY: {}", location);
                }

                if (isBlank(location.getCountry()) || !location.getCountry().matches("[A-Z]{2}")) {
                    log.warn("removing country '{}' from location={}", location.getCountry(), location);
                    location.setCountry(null);
                    removals++;
                    continue;
                }


                if ("DE".equals(location.getCountry())) {
                    location.setState(null);
                }

                newLocations.add(location);
            }
        }

        final Map<String, Integer> owners = new HashMap<>();
        int ids = 1;
        int ownerIds = 0;
        for (Location location : locations) {

            location.setId("GKid-" + ids);
            ids++;

            Integer ownerId = owners.get(location.getEmail());
            if (ownerId == null) {

                ownerId = ++ownerIds;
                if (location.getEmail() != null) {
                    owners.put(location.getEmail(), ownerId);
                }
            }

            location.setOwnerId("GKoid-" + ownerId);
        }

        log.info("enhancing took {}ms, {} removals", System.currentTimeMillis() - starttime, removals);

        return newLocations;
    }

    public Collection<? extends Location> get() {
        return new ArrayList<>(locations);
    }
}
