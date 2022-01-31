package de.greyshine.json.crud;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import de.greyshine.latlon.Latlon;

import java.io.IOException;

public class LatlonDeserializer extends StdDeserializer<Latlon> {

    public static final LatlonDeserializer INSTANCE = new LatlonDeserializer();

    public LatlonDeserializer() {
        super(Latlon.class);
    }

    @Override
    public Latlon deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        final JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        if (node == null) {
            return null;
        }

        final String value = node.asText(null);

        final Latlon latlon = Latlon.latlon(value);

        if (!latlon.isValid()) {
            throw new IllegalStateException("Invalid lat/lon: " + latlon);
        }

        return latlon;
    }

}
