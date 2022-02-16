package de.greyshine.latlon;

import de.greyshine.coffeeshopfinder.utils.validation.LatLonSetOrUnset;
import lombok.Data;
import org.springframework.util.Assert;

import java.math.BigDecimal;

@Data
@LatLonSetOrUnset
public class Latlon {

    public static final BigDecimal LAT_MAX_NORTH = new BigDecimal("90");
    public static final BigDecimal LAT_MIN_SOUTH = new BigDecimal("-90");

    public static final BigDecimal LON_MIN_WEST = new BigDecimal("-180");
    public static final BigDecimal LON_MAX_EAST = new BigDecimal("180");

    private BigDecimal lat;
    private BigDecimal lon;

    public Latlon(BigDecimal lat, BigDecimal lon) {

        Assert.notNull(lat, "lat value must not be null");
        Assert.notNull(lon, "lon value must not be null");

        this.lat = normalizeLat(lat);
        this.lon = normalizeLon(lon);
    }

    public static Latlon latlon(String s) {

        try {

            final String[] ps = s.split(",", -1);

            if (ps.length != 2) {
                throw new IllegalArgumentException("unexpected format");
            }

            return new Latlon(new BigDecimal(ps[0].strip()), new BigDecimal(ps[1].strip()));

        } catch (Exception e) {
            throw new IllegalArgumentException("cannot evaluate: \"" + s + "\"; " + e.getMessage());
        }
    }

    public static BigDecimal normalizeLat(BigDecimal lat) {

        Assert.notNull(lat, "Latitude is null");

        // TODO: modulo geht schneller

        while (lat.compareTo(LAT_MIN_SOUTH) < 0) {
            lat = lat.add(LAT_MAX_NORTH);
        }

        while (lat.compareTo(LAT_MAX_NORTH) > 0) {
            lat = lat.subtract(LAT_MIN_SOUTH);
        }

        return lat;
    }

    public static BigDecimal normalizeLon(BigDecimal lon) {

        Assert.notNull(lon, "longitude is null");

        // TODO: modulo geht schneller

        while (lon.compareTo(LON_MIN_WEST) < 0) {
            lon = lon.add(LON_MAX_EAST);
        }

        while (lon.compareTo(LON_MAX_EAST) > 0) {
            lon = lon.subtract(LON_MAX_EAST);
        }

        return lon;
    }

    public Latlon lon(BigDecimal l) {
        this.lon = l;
        return this;
    }

    public Latlon lon(String l) {
        return lon(lon == null ? null : new BigDecimal(l.strip()));
    }

    public Latlon lat(BigDecimal l) {
        this.lat = l;
        return this;
    }

    public Latlon lat(String lat) {
        return lat(lat == null ? null : new BigDecimal(lat.strip()));
    }

    public boolean isValid() {
        return (this.lat == null && this.lon == null) || (this.lat != null && this.lon != null);
    }

    public boolean isAtNorthOf(Latlon c) {

        Assert.notNull(c, "Latlon must not be null");

        final boolean r = this.lat.compareTo(c.getLat()) > 0;
        //log.info( "isAtNorthOf this={}, arg={} -> {}", this.lat, c.lat, r );
        return this.lat.compareTo(c.getLat()) > 0;
    }

    public boolean isAtSouthOf(Latlon c) {

        Assert.notNull(c, "Latlon must not be null");

        final boolean r = this.lat.compareTo(c.getLat()) < 0;
        //log.info( "isAtSouthOf this={}, arg={} -> {}", this.lat, c.lat, r );
        return r;
    }

    public boolean isAtWestOf(Latlon c) {

        Assert.notNull(c, "Latlon must not be null");

        final boolean r = this.lon.compareTo(c.getLon()) < 0;
        //log.info( "isAtWestOf this={}, arg={} -> {}", this.lat, c.lat, r );
        return r;
    }

    public boolean isAtEastOf(Latlon c) {

        Assert.notNull(c, "Latlon must not be null");

        final boolean r = this.lon.compareTo(c.getLon()) > 0;
        //log.info( "isAtEastOf this={}, arg={} -> {}", this.lat, c.lat, r );
        return r;
    }

    public String toJsonString() {

        if (!isValid()) {
            //TODO throw new IllegalStateException("Cannot print lat==null or lon==null");
            return "?,?";
        } else if (this.lat == null && this.lon == null) {
            return null;
        }

        return lat.toPlainString() + "," + lon.toPlainString();
    }

    public String toString() {
        return "Latlon (" + toJsonString() + ")";
    }

}