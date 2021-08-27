package de.greyshine.coffeeshopfinder.utils;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import javax.validation.*;
import java.io.*;
import java.math.BigDecimal;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.function.Supplier;

@Slf4j
public abstract class Utils {

    private Utils() {
    }

    @SneakyThrows
    public static String toHash(String s) {

        if (s == null) {
            return null;
        }

        s = s.length() + s;

        final MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(s.getBytes(StandardCharsets.UTF_8));

        final byte[] hashBytes = md.digest();

        final StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString().toLowerCase(Locale.ROOT);
    }

    public static long write(File file, String s) throws IOException {

        Assert.notNull(file, "File is null");
        Assert.isTrue(!file.exists() ||!file.isDirectory(), "File is a directory: "+file.getAbsolutePath());

        try (FileWriter fw = new FileWriter(file) ) {
            fw.write( s==null?"":s);
        }

        return file.length();
    }

    public static String readString(File file) throws IOException {

        Assert.notNull(file, "File is null");
        Assert.isTrue(!file.isDirectory(), "File is a directory: "+file.getAbsolutePath());

        final CharBuffer cb = CharBuffer.allocate( file.length() < 1 || (int)file.length() > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)file.length() );

        try (FileReader fr = new FileReader(file)) {
            fr.read( cb );
        }

        return cb.toString();
    }

    public static <T> void validateAndThrow(T object) {

        if ( object == null ) {
            throw new ConstraintViolationException("Cannot validate null", Collections.emptySet());
        }

        final Set<ConstraintViolation<T>> violations = validate(object);

        if (violations.isEmpty()) {
            return;
        }

        final String info;

        if ( violations.size()==1 ) {

            final ConstraintViolation<T> violation = violations.iterator().next();
            info = violation.getMessage()+" ("+ object+")";

        } else {
            info = violations.size() +" Validation fail(s): "+ object;
        }

        throw new ConstraintViolationException( info , violations );
    }

    public static <T> Set<ConstraintViolation<T>> validate(T object) {

        if (object == null) {
            return Collections.emptySet();
        }

        final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();

        final Set<ConstraintViolation<T>> violations = validator.validate(object);

        if ( log.isInfoEnabled()) {
            violations.forEach( v->log.debug("{}", v) );
        }

        return violations;
    }

    public static <K,V> void putIfAbsent(Map<K,V> map, K key, Supplier<V> supplier ) {

        if ( map == null || map.containsKey(key) || supplier == null ) { return; }
        map.put(key, supplier.get() );
    }
}
