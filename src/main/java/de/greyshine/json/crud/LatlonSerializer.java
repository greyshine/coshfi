package de.greyshine.json.crud;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import de.greyshine.latlon.Latlon;

import java.io.IOException;

public class LatlonSerializer extends StdSerializer<Latlon> {

    public static final LatlonSerializer INSTANCE = new LatlonSerializer();

    public LatlonSerializer() {
        super(Latlon.class);
    }

    @Override
    public void serialize(Latlon latlon, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        final String value = latlon == null ? null : latlon.toJsonString();
        jsonGenerator.getCodec().writeValue(jsonGenerator, value);
    }
}
