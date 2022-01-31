package de.greyshine.latlon;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.math.BigDecimal;

import static de.greyshine.coffeeshopfinder.utils.Utils.throwNotYetImplemented;

@Slf4j
@ToString
public class Box {

    private final Latlon nw;
    private final Latlon se;

    public Box(Latlon nw, Latlon se) {

        Assert.notNull(nw, "Latlon north-west is null");
        Assert.notNull(se, "Latlon south-east is null");

        if (!nw.isAtNorthOf(se)) {

            final Latlon t = nw;
            nw = se;
            se = t;
        }

        this.nw = nw;
        this.se = se;
    }

    public boolean isWithin(Latlon latlon) {

        Assert.notNull(latlon, "latlon must not be null");

        final StringBuilder sb = new StringBuilder();
        sb.append("\nthis: " + this);
        sb.append("\ncoor: " + latlon);
        sb.append("\nn: " + latlon.isAtNorthOf(this.nw));
        sb.append("\ne: " + latlon.isAtEastOf(this.nw));
        sb.append("\ns: " + latlon.isAtSouthOf(this.se));
        sb.append("\nw: " + latlon.isAtWestOf(this.se));
        //log.info( ""+ sb );

        if (latlon.isAtNorthOf(this.nw)) {
            return false;
        } else if (latlon.isAtWestOf(this.nw)) {
            return false;
        } else if (latlon.isAtSouthOf(this.se)) {
            return false;
        } else return !latlon.isAtEastOf(this.se);
    }

    public BigDecimal diameter() {
        throwNotYetImplemented();
        return null;
    }
}
