package de.greyshine.coffeeshopfinder.web.builder;

import de.greyshine.coffeeshopfinder.entity.Location;
import de.greyshine.coffeeshopfinder.web.resultObjects.LocationV1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import static de.greyshine.coffeeshopfinder.utils.Utils.doIfNotBlank;

@Slf4j
public abstract class AddressBuilder {

    static final AddressBuilder DEFAULT = new DefaultBuilder();
    static final AddressBuilder DE = new DeAddressBuilder();
    static final AddressBuilder US = new UsAddressBuilder();

    public static void build(Location location, LocationV1 locationV1) {

        String result = null;

        if ("DE".equalsIgnoreCase(location.getCountry())) {

            result = DE.build(location);

        } else if ("US".equalsIgnoreCase(location.getCountry())) {

            result = US.build(location);

        } else {

            result = DEFAULT.build(location);
        }

        result = removeBlanks(result);

        locationV1.setAddress(result);
    }

    static String removeBlanks(String str) {
        return str == null ? null : str
                .replaceAll(" , ,", ", ")
                .replaceAll("  ", " ")
                .replaceAll("\n ", "\n")
                .replaceAll("\n\n", "\n");
    }

    public abstract String build(Location location);

    public static class DefaultBuilder extends AddressBuilder {

        @Override
        public String build(Location location) {

            Assert.notNull(location, "location must not be null");

            final StringBuilder sb = new StringBuilder();

            doIfNotBlank(location.getStreet(), street -> sb.append(street.strip()).toString());
            doIfNotBlank(location.getZip(), zip -> sb.append(", ").append(zip.strip()).toString());
            doIfNotBlank(location.getCity(), city -> sb.append(", ").append(city.strip()).toString());
            doIfNotBlank(location.getState(), state -> sb.append(", ").append(state.strip()).toString());
            doIfNotBlank(location.getCountry(), country -> sb.append(", ").append(country.strip()).toString());

            final String result = sb.toString().strip();
            return !result.startsWith(", ") ? result : result.substring(2);
        }
    }

}
