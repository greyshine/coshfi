package de.greyshine.json.crud;

import de.greyshine.coffeeshopfinder.utils.Utils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class JsonCrudService {

    public static final Object SYNC_GLOBAL = new Object() {
        @Override
        public String toString() {
            return "SYNC for GLOBAL";
        }
    };

    public final Object SYNC_LOCAL = new Object() {
        @Override
        public String toString() {
            return "SYNC for " + JsonCrudService.this;
        }
    };

    private final JsonService jsonService;

    public <T extends Entity> T iterateSingle(Class<T> clazz, Sync sync, Utils.Function2<T, IterationResult> function) {

        final List<T> items = iterate(clazz, sync, function);

        if (items.size() > 1) {
            log.warn("There is more than one result. Using only the first!");
        }

        return items.isEmpty() ? null : items.get(0);
    }

    public JsonCrudService(@Autowired JsonService jsonService) {

        Assert.notNull(jsonService, "JsonService must not be null");
        this.jsonService = jsonService;
    }

    public <T extends Entity> String create(T item) {

        Assert.notNull(item, "item to persist is null");

        jsonService.createDirIfNotExists(item.getClass());

        Sync sync = Sync.NONE;
        String id = item.getId();
        if (id == null) {
            id = UUID.randomUUID().toString();
            item.setId(id);
        } else {
            sync = Sync.LOCAL;
        }

        return executeSynced(sync, () -> {

            item.updateCreated();
            final long bytes = jsonService.save(item, false);
            log.debug("stored {}#{} with {} bytes", item.getClass().getCanonicalName(), item.getId(), bytes);

            return item.getId();
        });
    }

    public <T extends Entity> T read(Class<T> entityClass, String id, Sync sync) {

        Assert.notNull(entityClass, "entity class is null");
        Assert.notNull(id, "id is null");

        jsonService.isExistingDir(entityClass);

        return executeSynced(sync, () -> jsonService.load(entityClass, id));
    }

    public <T extends Entity> void update(Sync sync, T item) {

        Assert.notNull(item, "entity to update is null");
        Assert.isTrue(jsonService.isExistingDir(item.getClass()), "no existing directory for item: " + item);

        executeSynced(sync, () -> {
            item.updateUpdated();
            jsonService.save(item, true);
            return item;
        });
    }

    public <T extends Entity> String upsert(Sync sync, T item) {

        Assert.notNull(item, "Item is null");

        synchronized (Sync.getDefault(sync)) {

            if (jsonService.isPersisted(item)) {

                update(sync, item);
                return item.getId();

            } else {

                return create(item);
            }
        }
    }

    public <T extends Entity> boolean delete(Sync sync, Class<T> clazz, String id) {

        Assert.notNull(clazz, "");
        Assert.isTrue(jsonService.isExistingDir(clazz), "no existing directory for " + clazz);
        Assert.isTrue(id != null && !id.isBlank(), "id must not be blank");

        return executeSynced(sync, () -> jsonService.delete(clazz, id));
    }

    public <T extends Entity> List<T> iterate(Class<T> clazz, Sync sync, Utils.Function2<T, IterationResult> function) {

        Assert.notNull(clazz, "Class must be set");
        sync = Sync.getDefault(sync);
        Assert.notNull(function, "Function must be set");

        // TOD apply SYNC
        final Iterator<String> idsIter = jsonService.getIds(clazz);

        final List<T> results = new ArrayList<>(0);

        while (idsIter.hasNext()) {

            final String id = idsIter.next();

            try {

                final T item = jsonService.load(clazz, id);

                IterationResult iterationResult = function.apply(item);
                iterationResult = iterationResult != null ? iterationResult : IterationResult.IGNORE;

                if (iterationResult.isUse()) {
                    results.add(item);
                }

                if (iterationResult.isQuit()) {
                    break;
                }

            } catch (Exception exception) {
                log.warn("Unable to handle item with ID={}", id, exception);
            }
        }

        return results;
    }

    public enum IterationResult {

        USE(true, false),
        USE_QUIT(true, true),
        IGNORE(false, false),
        IGNORE_QUIT(false, true);

        public final boolean use;
        public final boolean quit;

        IterationResult(boolean isUse, boolean isQuit) {
            this.use = isUse;
            this.quit = isQuit;
        }

        public boolean isQuit() {
            return quit;
        }

        public boolean isUse() {
            return use;
        }
    }


    @SneakyThrows
    public <T> String toString(T item) {
        return jsonService.serialize(item);
    }

    protected Object getSyncObject(Sync sync) {
        if (Sync.GLOBAL == sync) {
            return SYNC_GLOBAL;
        } else if (Sync.LOCAL == sync) {
            return SYNC_LOCAL;
        } else {
            return null;
        }
    }

    @SneakyThrows
    private <S> S executeSynced(Sync sync, Utils.Supplier2<S> supplier) {

        Assert.notNull(supplier, "No supplier given.");

        Object syncObject = null;

        if (Sync.GLOBAL == sync) {
            syncObject = SYNC_GLOBAL;
        } else if (Sync.LOCAL == sync) {
            syncObject = SYNC_LOCAL;
        } else {
            return supplier.get();
        }

        synchronized (syncObject) {
            return supplier.get();
        }
    }

    public enum Sync {
        /**
         * All over locked of everything
         */
        GLOBAL,
        /**
         * Just the used instance of CrudService locks
         */
        LOCAL,

        /**
         * Nothing is synced
         */
        NONE;

        static boolean isNone(Sync sync) {
            return NONE == sync || sync == null;
        }

        public static Sync getDefault(Sync sync) {
            return sync == null ? NONE : sync;
        }
    }


}
