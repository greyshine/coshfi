package de.greyshine.coffeeshopfinder.web.builder;

import de.greyshine.coffeeshopfinder.entity.Location;
import org.springframework.util.Assert;

import static de.greyshine.coffeeshopfinder.utils.Utils.getDefaultString;


/**
 * https://wmich.edu/writing/rules/addresses
 */
public class UsAddressBuilder extends AddressBuilder {

    public String build(Location location) {

        Assert.notNull(location, "location must not be null");

        final var sb = new StringBuilder();

        sb.append(getDefaultString(location.getStreet(), () -> "")).append('\n');
        sb.append(getDefaultString(location.getCity(), () -> "")).append(' ');
        sb.append(getDefaultString(location.getState(), () -> "")).append(' ');
        sb.append(getDefaultString(location.getZip(), () -> ""));

        return sb.toString();
    }

}
