package de.greyshine.coffeeshopfinder.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.validation.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;

@Slf4j
public abstract class Utils {

    public final static DateTimeFormatter DTF_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    public final static DateTimeFormatter DTF_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public final static DateTimeFormatter DTF_TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

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
        Assert.isTrue(!file.exists() || !file.isDirectory(), "File is a directory: " + file.getAbsolutePath());

        try (FileWriter fw = new FileWriter(file)) {
            fw.write(s == null ? "" : s);
        }

        return file.length();
    }

    public static String readString(File file) throws IOException {
        return readString(file, StandardCharsets.UTF_8);
    }

    public static String readString(File file, Charset charset) throws IOException {

        Assert.notNull(file, "File is null");
        Assert.isTrue(!file.isDirectory(), "File is a directory: " + file.getAbsolutePath());
        Assert.isTrue(file.canRead(), "File cannote be read: " + file.getAbsolutePath());

        return Files.readString(file.toPath(), charset != null ? charset : StandardCharsets.UTF_8);
    }

    public static <T> void validateAndThrow(T object) {

        if (object == null) {
            throw new ConstraintViolationException("Cannot validate null", Collections.emptySet());
        }

        final Set<ConstraintViolation<T>> violations = validate(object);

        if (violations.isEmpty()) {
            return;
        }

        final String info;

        if (violations.size() == 1) {

            final ConstraintViolation<T> violation = violations.iterator().next();
            info = violation.getMessage() + " (" + object + ")";

        } else {
            info = violations.size() + " Validation fail(s): " + object;
        }

        if (log.isDebugEnabled()) {
            log.warn("{} violations on {}:", violations.size(), object);
            for (ConstraintViolation cv : violations) {
                log.warn("{}", cv);
            }
        }

        throw new ConstraintViolationException(info, violations);
    }

    public static <T> Set<ConstraintViolation<T>> validate(T object) {

        if (object == null) {
            return Collections.emptySet();
        }

        final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();

        final Set<ConstraintViolation<T>> violations = validator.validate(object);

        if (log.isInfoEnabled()) {
            violations.forEach(v -> log.debug("{}", v));
        }

        return violations;
    }

    public static <K, V> void putIfAbsent(Map<K, V> map, K key, Supplier<V> supplier) {
        if (map == null || map.containsKey(key) || supplier == null) {
            return;
        }
        map.put(key, supplier.get());
    }

    public static String getIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return "TODO: get IP " + request;
    }

    public static String loadString(File file) throws IOException {
        return loadString(file, null);
    }

    public static String loadString(File file, Charset charset) throws IOException {

        Assert.notNull(file, "File is null");
        Assert.isTrue(file.isFile() && file.canRead(), "File is not accessible as file");

        charset = charset != null ? charset : StandardCharsets.UTF_8;

        final StringBuilder sb = new StringBuilder();

        try (FileReader fr = new FileReader(file, charset)) {
            while (fr.ready()) {
                sb.append((char) fr.read());
            }
        }

        return sb.toString();
    }

    public static File getFile(File file) {
        try {
            return file == null ? null : file.getCanonicalFile();
        } catch (IOException exception) {
            return file.getAbsoluteFile();
        }
    }

    public static <T> T getDefault(T object, Supplier<T> supplier) {
        return object != null || supplier == null ? object : supplier.get();
    }

    public static String getDefaultString(String s, Supplier<String> supplier) {
        return isNotBlank(s) ? s.trim() : supplier == null ? s : supplier.get();
    }

    public static boolean isEqualsTrimmed(String s1, String s2, boolean isNullEquals) {

        s1 = trimToNull(s1);
        s2 = trimToNull(s2);

        return s1==null && s2==null ? isNullEquals : StringUtils.equals(s1, s2);
    }

    @FunctionalInterface
    public interface Runnable2 {
        void run() throws Exception;
    }

    @FunctionalInterface
    public interface Supplier2<T> {

        /**
         * Gets a result.
         *
         * @return a result
         */
        T get() throws Exception;
    }

    @FunctionalInterface
    public interface Consumer2<T> {

        void accept(T t) throws Exception;

        default Consumer2<T> andThen(Consumer2<? super T> after) {
            Objects.requireNonNull(after);
            return (T t) -> {
                accept(t);
                after.accept(t);
            };
        }
    }

    @FunctionalInterface
    public interface Function2<T, R> {

        static <T> Function2<T, T> identity() {
            return t -> t;
        }

        R apply(T t) throws Exception;

        default <V> Function2<V, R> compose(Function2<? super V, ? extends T> before) {
            Objects.requireNonNull(before);
            return (V v) -> apply(before.apply(v));
        }

        default <V> Function2<T, V> andThen(Function2<? super R, ? extends V> after) {
            Objects.requireNonNull(after);
            return (T t) -> after.apply(apply(t));
        }
    }
}
