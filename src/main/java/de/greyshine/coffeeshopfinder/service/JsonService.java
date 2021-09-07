package de.greyshine.coffeeshopfinder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.greyshine.coffeeshopfinder.entity.Entity;
import de.greyshine.coffeeshopfinder.utils.Latlon;
import de.greyshine.coffeeshopfinder.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@Slf4j
public class JsonService {

    private static final Collection<String> EMPTY_STRINGS = Collections.emptySet();
    private static final FileFilter FILEFILTER_JSON = file->file!=null&&file.getName().toLowerCase(Locale.ROOT).endsWith(".json");

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

    public <T extends Entity> T load(Class<T> entityClass, String id) throws IOException {

        Assert.isTrue(isNotBlank(id), "ID to load is null");

        final File jsonFile = getFile( entityClass, id );
        if ( !jsonFile.exists() || jsonFile.isDirectory() || !jsonFile.canRead() ) {
            throw new IOException("Cannot access "+ jsonFile);
        }

        final String s = Utils.readString( jsonFile );

        if ( isBlank(s) || "null".equalsIgnoreCase( s.trim() ) ) {
            return null;
        }

        return objectMapper.readValue(s, entityClass);
    }

    public <T extends Entity> File getFile(T item) {

        Assert.notNull( item, "item is null" );
        Assert.isTrue( isNotBlank(item.getId()), "item's ID is blank" );

        return getFile( item.getClass(), item.getId() );
    }

    public <T extends Entity> File getFile(Class<T> clazz, String id ) {

        Assert.notNull( clazz, "Class is null" );
        Assert.isTrue( isNotBlank(id), "ID is blank" );

        final File dir = new File(dataDir, clazz.getCanonicalName());
        final File file = new File( dir, id+".json" );
        log.debug("getFile({}, {}):{}, exists={}, {} bytes", clazz.getCanonicalName(), id, file.getAbsolutePath(), file.isFile(), !file.isFile()?-1:file.length() );
        return file;
    }

    public <T extends Entity> long save(T item, boolean allowOverwrite) throws IOException {

        Assert.notNull(item, "item is null");
        Assert.isTrue(item.getId()!=null&&!item.getId().isBlank(), "item's id is null");

        final String jsonString = serialize(item);
        final File file = getFile(item);

        if ( !allowOverwrite ) {
            Assert.isTrue( !file.exists(), "File already exists (file="+ file.getCanonicalPath() +")" );
        }

        Utils.write(file, jsonString);

        return file.length();
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

    public <T extends Entity> void createDirIFNotExists(Class<T> entityClass) {

        Assert.notNull(entityClass, "Class is null");

        final File dir = new File(dataDir, entityClass.getCanonicalName());

        if ( dir.exists() && dir.isDirectory() && dir.canRead() ) {
            return;
        }

        Assert.isTrue( !dir.exists(), "Cannot access "+ dir );

        dir.mkdirs();

        Assert.isTrue( dir.exists() && dir.isDirectory() && dir.canRead(), "Cannot access "+ dir +" after trying to create" );

        log.info("created repository dir: {}", dir);
    }

    public <T extends Entity> boolean delete(Class<T> clazz, String id) {

        final File file = getFile( clazz, id );
        if ( !file.exists() ) {
            return false;
        }

        file.delete();

        Assert.isTrue( !file.exists(), "File (id="+id+")was not deleted" );

        return true;
    }

    public <T extends Entity> Iterator<String> getIds(Class<T> entityClass) {

        Assert.notNull(entityClass, "No entity class defined");

        final File dir = new File( dataDir, entityClass.getCanonicalName() );

        if ( !dir.exists() ) {
            return EMPTY_STRINGS.iterator();
        }

        // TODO direct returning the iterator instead of first collecting and then creating an iter
        final Set<String> ids = new LinkedHashSet();
        for(File aFile : dir.listFiles( FILEFILTER_JSON )) {
            final String filename = aFile.getName();
            ids.add( filename.substring(0, filename.lastIndexOf('.') ) );
        }
        return ids.iterator();
    }

    public <T extends Entity> boolean isPersisted(T item) {

        Assert.notNull( item, "item is null" );

        final String id = item.getId();

        if ( isBlank( id ) ) {
            return false;
        }

        final File file = new File( dataDir, item.getClass().getCanonicalName()+ File.separator + item.getId() +".json"  );
        if (file.exists()) {
            Assert.isTrue( file.isFile() && file.canRead(), "Cannot access file: "+ file.getAbsolutePath() );
        }
        return file.exists();
    }
}
