package de.greyshine.coffeeshopfinder.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import de.greyshine.coffeeshopfinder.utils.validation.LatLonSetOrUnset;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@LatLonSetOrUnset
public class Latlon {

    private BigDecimal lat;
    private BigDecimal lon;

    public Latlon(String s) {
        latlon(s);
    }

    public static Latlon latlon(String s) {

        final Latlon latlon = new Latlon();

        if (s == null || s.trim().isEmpty()) {
            return latlon;
        }

        BigDecimal lat, lon;

        try {

            final String[] ps = s.split(",", -1);
            lat = new BigDecimal(ps[0]);
            lon = new BigDecimal(ps[1]);
        } catch (Exception e) {
            throw new IllegalArgumentException("cannot evaluate: \"" + s + "\"");
        }

        latlon.lat = lat;
        latlon.lon = lon;

        return latlon;
    }

    public Latlon lon(BigDecimal l) {
        this.lon = l;
        return this;
    }

    public Latlon lon(String l) {
        return lon(l == null ? null : new BigDecimal(l));
    }

    public Latlon lat(BigDecimal l) {
        this.lat = l;
        return this;
    }

    public Latlon lat(String l) {
        return lat(l == null ? null : new BigDecimal(l));
    }

    public boolean isValid() {
        return (this.lat == null && this.lon == null) || (this.lat != null && this.lon != null);
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

    public static class Serializer extends StdSerializer<Latlon> {

        public Serializer() {
            super(Latlon.class);
        }

        @Override
        public void serialize(Latlon latlon, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            final String value = latlon == null ? null : latlon.toJsonString();
            jsonGenerator.getCodec().writeValue(jsonGenerator, value);
        }
    }

    public static class Deserializer extends StdDeserializer<Latlon> {

        public Deserializer() {
            super(Latlon.class);
        }

        @Override
        public Latlon deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

            final JsonNode node = jsonParser.getCodec().readTree(jsonParser);

            if (node == null) {
                return null;
            }

            final String value = node.asText(null);

            final Latlon latlon = new Latlon(value);

            if (!latlon.isValid()) {
                throw new IllegalStateException("Invalid lat/lon: " + latlon);
            }

            return latlon;
        }
    }


}
