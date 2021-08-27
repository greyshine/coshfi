package de.greyshine.coffeeshopfinder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.greyshine.coffeeshopfinder.entity.UserEntity;
import de.greyshine.coffeeshopfinder.utils.Latlon;
import de.greyshine.coffeeshopfinder.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@Slf4j
public class JsonService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${datadir}")
    private File dataDir;

    @PostConstruct
    public void postConstruct() {

        Assert.notNull(dataDir, "datadir is null");
        Assert.isTrue(dataDir.isDirectory(), "datadir is no dir");
        log.info("datadir={}", dataDir.getAbsolutePath());

        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        log.info("objectMapper={}", objectMapper);

        final SimpleModule module = new SimpleModule();
        module.addDeserializer(Latlon.class, new Latlon.Deserializer());
        module.addSerializer(Latlon.class, new Latlon.Serializer());
        objectMapper.registerModule(module);
    }

    public File getDataDir() {
        return dataDir.getAbsoluteFile();
    }

    public UserEntity load(String name) throws IOException {

        Assert.isTrue(isNotBlank(name), "Name to load is null");

        final File jsonFile = new File(dataDir, name + ".json");
        final String s = Utils.readString( jsonFile );

        if ( isBlank(s) || "null".equalsIgnoreCase( s.trim() ) ) {
            return null;
        }

        return objectMapper.readValue(s, UserEntity.class);
    }

    public String save(UserEntity userEntity) throws IOException {

        Assert.notNull(userEntity, "UserEntity must not be null");

        final String userEntityString = serialize(userEntity);
        final File jsonFile = new File(dataDir, userEntity.getUser() + ".json");
        Utils.write(jsonFile, userEntityString);

        return userEntityString;
    }

    public <T> String serialize(T object) throws IOException {

        if (object == null) {
            return null;
        }

        Utils.validateAndThrow(object);

        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        objectMapper.writeValue(os, object);
        return os.toString(StandardCharsets.UTF_8);
    }

    public <T> T deserialize(Class<T> clazz, String string) throws IOException {

        if (string == null) {
            return null;
        }

        T result;

        try (ByteArrayInputStream is = new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8))) {
            result = objectMapper.readValue(string.getBytes(StandardCharsets.UTF_8), clazz);
        }

        Utils.validateAndThrow(result);

        return result;
    }
}
