package de.greyshine.coffeeshopfinder.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.validation.*;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.apache.commons.lang3.StringUtils.*;

@Slf4j
public abstract class Utils {

    public final static DateTimeFormatter DTF_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    public final static DateTimeFormatter DTF_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public final static DateTimeFormatter DTF_TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final static Random RANDOM = new Random();

    private final static Object SYNC_OBJECT = new Object();

    private static final char[] CC_CHARS = "abcdefghigjklmnopqrstuvwxyzABCDEFGHIJKLMOPQRSTUVWYZ0123456789".toCharArray();

    private Utils() {
    }

    public static char getRandomLetter() {
        return CC_CHARS[getRandom(0, CC_CHARS.length - 1)];
    }

    public static String getRandomLetters(int amountLetters) {

        Assert.isTrue(amountLetters >= 0, "parameter must be >= 0");

        final StringBuffer sb = new StringBuffer();
        for (int i = 0, l = amountLetters; i < l; i++) {
            sb.append(CC_CHARS[getRandom(0, CC_CHARS.length - 1)]);
        }

        return sb.toString();
    }

    @SneakyThrows
    public static String toHash(String s) {

        Assert.notNull(s, "value to be hashed must not be null");

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

    /**
     * @param file to be converted
     * @return the canonical file or the absolut file or null when passed null
     */
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

    public static String doIfNotBlank(String arg, Function<String, String> function) {

        Assert.notNull(function, "");
        return isBlank(arg) ? null : function.apply(arg);
    }

    public static boolean isEquals(Object o1, Object o2, boolean isNullsEqual) {
        if (o1 == null && o2 == null) {
            return isNullsEqual;
        } else if (o1 == o2) {
            return true;
        } else if (o1 == null || o2 == null) {
            return false;
        } else {
            return o1.equals(o2);
        }
    }

    public static boolean isEqualsTrimmed(String s1, String s2, boolean isNullEquals) {
        return isEquals(trimToNull(s1), trimToNull(s2), isNullEquals);
    }

    public static boolean isEqualsIgnoreCaseTrimmed(String s1, String s2, boolean isNullEquals) {
        return isEquals(trimToLowercaseNull(s1), trimToLowercaseNull(s2), isNullEquals);
    }

    public static String trimToLowercaseNull(String s) {
        if (s == null || (s = s.trim()).isBlank()) {
            return null;
        }
        return s.toLowerCase(Locale.ROOT);
    }

    public static String trimToUppercaseNull(String s) {
        if (s == null || (s = s.trim()).isBlank()) {
            return null;
        }
        return s.toUpperCase(Locale.ROOT);
    }

    public static Exception executeSafe(Runnable2 runnable) {

        Assert.notNull(runnable, "Runnable must not be null");

        try {
            runnable.run();
            return null;
        } catch (Exception e) {
            return e;
        }
    }

    public static <T> T executeSafe(Supplier2<T> supplier, T defaultValue) {

        Assert.notNull(supplier, "Supplier must not be null");

        try {

            return supplier.get();

        } catch (Exception e) {

            log.info("Exception on execution, returning default: " + toString(e));
            return defaultValue;
        }
    }

    public static byte[] read(File file) throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            return read(is);
        }
    }

    public static byte[] read(InputStream is) throws IOException {
        return is.readAllBytes();
    }

    public static void throwIllegalState(String message) {
        throw new IllegalStateException(isBlank(message) ? "?" : message);
    }

    public static <T> T throwNotYetImplemented() {
        throwIllegalState("not yet implemented");
        return null;
    }

    public static boolean isOneOf(Object test, Object... testObjects) {

        Assert.notNull(testObjects, "Objects to tests are null");

        for (Object object : testObjects) {
            if (isEquals(test, object, true)) {
                return true;
            }
        }

        return false;
    }

    public static String toString(Supplier<Object> supplier, boolean ignoreException) {

        if (supplier == null) {
            return null;
        }

        try {

            return toString(supplier.get());

        } catch (Exception e) {

            if (ignoreException) {
                return null;
            }

            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        }

    }

    public static String toString(Object object) {

        // TODO: make Runnabke be more extensive logged


        return object == null ? "null" : String.valueOf(object);
    }

    public static int getRandom(int min, int max) {

        if (min == max) {
            return min;
        } else if (min > max) {
            final int t = min;
            min = max;
            max = t;
        }

        return RANDOM.nextInt(max - min + 1) + min;
    }

    public static <T> T executeSynced(Object syncObject, Supplier2<T> supplier) throws Exception {

        Assert.notNull(supplier, "Supplier must not be null");

        synchronized (syncObject == null ? SYNC_OBJECT : syncObject) {
            return supplier.get();
        }
    }

    public static void executeSynced(Object syncObject, Runnable2 runnable) throws Exception {

        Assert.notNull(runnable, "Runnable must not be null");

        synchronized (syncObject == null ? SYNC_OBJECT : syncObject) {
            runnable.run();
        }
    }

    @SneakyThrows
    public static String getUrlencoded(String value) {
        return value == null ? null : URLEncoder.encode(value, "UTF-8");
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

    public static class AtomicObject<T> {

        @Getter
        @Setter
        private T object;

        public boolean isNull() {
            return object == null;
        }

        public AtomicObject<T> value(T object) {
            this.setObject(object);
            return this;
        }

        @Override
        public String toString() {
            return String.valueOf(object);
        }
    }
}
