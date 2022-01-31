package de.greyshine.coffeeshopfinder.service.location;

import com.google.gson.annotations.SerializedName;
import de.greyshine.coffeeshopfinder.entity.Location;
import de.greyshine.coffeeshopfinder.utils.Utils;
import de.greyshine.latlon.Box;
import de.greyshine.latlon.Latlon;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import static de.greyshine.coffeeshopfinder.utils.Utils.isEqualsTrimmed;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@Slf4j
public class LocationsService {

    //private final WmLocationsService wmLocationsService;

    private final List<Location> locations = new ArrayList<>();

    public LocationsService(@Autowired WmLocationsService wmLocationsService) throws IOException {

        Assert.notNull(wmLocationsService, "WMLocationsService is null");

        locations.addAll(wmLocationsService.get());
    }

    public List<Location> getLocations() throws IOException {
        return new ArrayList<>(locations);
    }


    /**
     * @param function
     * @param parameter
     * @throws IOException
     */
    public void handleAll(Function<Location, Boolean> function, Parameter parameter) throws IOException {

        if (function == null) {

            log.warn("function is null");
            return;
        }

        final long starttime = System.currentTimeMillis();
        int itemsHandled = 0;
        int itemsChecked = 0;

        final Collection<Location> locations = getLocations();

        for (Location location : locations) {

            if (location == null) {
                log.warn("a location is null; this should not happen ;-(");
                continue;
            } else if (isBlank(location.getName()) || location.getLatlon() == null) {
                log.warn("Location's name and/or latlon is blank: " + location);
                continue;
            }


            try {

                itemsChecked++;

                if (Utils.isOneOf(location.getCountry(), "DE", "Deutschland", "Germany")) {
                    //log.info("{}", location);
                }

                final boolean isHandle = parameter == null || isHandle(location, parameter);

                if (!isHandle) {
                    continue;
                }

                itemsHandled++;

                log.debug("handling: {}", location);

                final Boolean result = function.apply(location);

                if (Boolean.FALSE.equals(result)) {
                    return;
                }

            } catch (RuntimeException e) {

                // proceed on Exception?
                if (parameter != null && !parameter.stopOnException) {
                    continue;
                }

                throw e;
            }
        }

        log.info("handled {} item(s) ({} items checked), time: {}ms", itemsHandled, itemsChecked, System.currentTimeMillis() - starttime);
    }

    private boolean isHandle(Location location, Parameter parameter) {

        if (parameter == null) {
            return true;
        }

        if (parameter.getWithinBox() != null && !parameter.getWithinBox().isWithin(location.getLatlon())) {
            return false;
        }

        if (!parameter.getTypes().isEmpty() && parameter.getTypes().contains(location.getType())) {
            return false;
        }

        if (parameter.getId() != null && !isEqualsTrimmed(parameter.getId(), location.getId(), false)) {
            return false;
        }

        if (parameter.getOwnerId() != null && !isEqualsTrimmed(parameter.getOwnerId(), location.getOwnerId(), false)) {
            return false;
        }

        return true;
    }

    public Location get(String id) {

        Assert.isTrue(isNotBlank(id), "Id nust not be blank");

        for (Location location : locations) {
            if (isEqualsTrimmed(id, location.getId(), false)) {
                return location;
            }
        }

        return null;
    }

    public Collection<Location> getByOwner(String ownerId) throws IOException {

        Assert.isTrue(isNotBlank(ownerId), "Id nust not be blank");

        final Set<Location> locations = new HashSet<>();
        handleAll(locations::add, Parameter.getByOwnerId(ownerId));

        locations.forEach(l -> log.debug("getByOwner({}): {}", ownerId, l));

        return locations;
    }

    @Data
    public static class DataWrapper {

        public String created;

        @SerializedName("datas")
        public List<Location> locations = new ArrayList<>();
    }

    @Getter
    public static class Parameter {

        private final boolean stopOnException = true;

        private Set<Location.EType> types = new HashSet<>();

        private String id;
        private String ownerId;

        private Box withinBox;

        public static Parameter getByBoxCoordinates(Latlon nw, Latlon se) {
            return new Parameter().withinBox(new Box(nw, se));
        }

        public static Parameter getByOwnerId(String ownerId) {
            return new Parameter().ownerId(ownerId);
        }

        private Parameter ownerId(String ownerId) {
            this.ownerId = ownerId;
            return this;
        }

        public Parameter withinBox(Box box) {
            this.withinBox = box;
            return this;
        }

        public Parameter setType(Location.EType... types) {

            final Set<Location.EType> memberTypes = new HashSet<>();

            for (Location.EType type : types == null ? new Location.EType[0] : types) {
                if (type == null) {
                    continue;
                }
                memberTypes.add(type);
            }

            this.types = Collections.unmodifiableSet(memberTypes);
            return this;
        }

    }
}
