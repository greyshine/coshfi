package de.greyshine.json.crud;

import de.greyshine.coffeeshopfinder.utils.Utils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

@Service
@Slf4j
public class JsonCrudService {

    public static final Object SYNC_GLOBAL = new Object() {
        @Override
        public String toString() {
            return "SYNC for GLOBAL";
        }
    };

    private static final Map<Class<?>, Object> LOCAL_SYNC_OBJECTS = new HashMap<>();

    private final JsonService jsonService;

    public <T extends Entity> T iterateSingle(Class<T> clazz, Sync sync, Utils.Function2<T, IterationResult> function) {

        final var items = iterate(clazz, sync, function);

        if (items.size() > 1) {
            log.warn("There is more than one result. Using only the first!");
        }

        return items.isEmpty() ? null : items.get(0);
    }

    public JsonCrudService(@Autowired JsonService jsonService) {

        notNull(jsonService, "JsonService must not be null");
        this.jsonService = jsonService;
    }

    public <T extends Entity> String create(T item) {

        notNull(item, "item to persist is null");

        jsonService.createDirIfNotExists(item.getClass());

        return executeSynced(Sync.NONE, item.getClass(), () -> {

            item.beforeCreate();
            item.updateCreated();

            final var bytes = jsonService.save(item, false);
            log.debug("stored {}#{} with {} bytes", item.getClass().getCanonicalName(), item.getId(), bytes);

            return item.getId();
        });
    }

    public <T extends Entity> Optional<T> read(Class<T> entityClass, String id, Sync sync) {
        return executeSynced(sync, entityClass, () -> jsonService.load(entityClass, id));
    }

    public <T extends Entity> void update(Sync sync, T item) {

        notNull(item, "entity to update is null");
        isTrue(jsonService.isExistingDir(item.getClass()), "no existing directory for item: " + item);

        executeSynced(sync, item.getClass(), () -> {

            item.beforeUpdate();
            item.updateUpdated();
            jsonService.save(item, true);
            return item;
        });
    }

    public <T extends Entity> String upsert(Sync sync, T item) {

        notNull(item, "Item is null");

        synchronized (Sync.getDefault(sync)) {

            if (jsonService.isPersisted(item)) {

                update(sync, item);
                return item.getId();

            } else {

                return create(item);
            }
        }
    }

    /**
     * @param sync
     * @param clazz
     * @param id
     * @param physicalDelete
     * @param <T>
     * @return
     */
    public <T extends Entity> boolean delete(Sync sync, Class<T> clazz, String id, boolean physicalDelete) {

        notNull(clazz, "");
        isTrue(jsonService.isExistingDir(clazz), "no existing directory for " + clazz);
        isTrue(id != null && !id.isBlank(), "id must not be blank");

        final Utils.Supplier2<Boolean> deleteSupplier = () -> {

            final Optional<T> optEntity = read(clazz, id, sync);

            if (optEntity.isEmpty()) {
                return false;
            }

            final T entity = optEntity.get();

            entity.updateDeleted();
            entity.beforeDelete(physicalDelete);
            entity.updateDeleted();

            update(sync, optEntity.get());

            if (physicalDelete) {

                return jsonService.deleteFile(clazz, id);

            } else {

                optEntity.get().updateDeleted();

                return true;
            }
        };

        return executeSynced(sync, clazz, deleteSupplier);
    }

    public <T extends Entity> List<T> iterate(Class<T> clazz, Sync sync, Utils.Function2<T, IterationResult> function) {

        notNull(clazz, "Class must be set");
        notNull(function, "Function must be set");

        final Utils.Supplier2<List<T>> supplier = () -> {

            final var idsIter = jsonService.getIds(clazz);
            final var results = new ArrayList<T>(0);

            while (idsIter.hasNext()) {

                final var id = idsIter.next();

                try {

                    final var item = jsonService.load(clazz, id);

                    if (item.isEmpty()) {

                        log.warn("Expected to receive an item for id={}, class={}", id, clazz.getCanonicalName());
                        continue;

                    } else if ( item.get().isDeleted() ) {
                        // ignore items marked as deleted
                        continue;
                    }

                    var iterationResult = function.apply(item.get());
                    iterationResult = iterationResult != null ? iterationResult : IterationResult.IGNORE;

                    if (iterationResult.isUse()) {
                        results.add(item.get());
                    }

                    if (iterationResult.isQuit()) {
                        break;
                    }

                } catch (Exception exception) {
                    log.warn("Unable to handle item with ID={}", id, exception);
                }
            }

            return results;
        };

        return executeSynced(sync, clazz, supplier);
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

    @SneakyThrows
    private <S> S executeSynced(Sync sync, Class<?> clazz, Utils.Supplier2<S> supplier) {

        notNull(supplier, "No supplier given.");

        final var syncObject = Sync.getDefault(sync).getSyncObject(clazz);

        if (syncObject == null) {
            return supplier.get();
        } else {
            synchronized (syncObject) {
                return supplier.get();
            }
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

        private static final Map<Class, Object> syncObjects = new HashMap<>();

        public static Sync getDefault(Sync sync) {
            return sync == null ? NONE : sync;
        }

        public static boolean isNone(Sync sync) {
            return sync == null || sync.isNone();
        }

        public boolean isNone() {
            return this == Sync.NONE;
        }

        public boolean isGlobal() {
            return this == Sync.GLOBAL;
        }

        public boolean isLocal() {
            return this == Sync.LOCAL;
        }

        /**
         * NONE must never be returned!
         *
         * @param clazz
         * @return
         */
        public Object getSyncObject(Class<?> clazz) {

            if (this == NONE) {
                return null;
            } else if (this == GLOBAL) {
                return SYNC_GLOBAL;
            } else if (clazz == null) {
                log.warn("received no class on LOCAL sync, will use GLOBAL sync instead!");
                return SYNC_GLOBAL;
            }

            var syncObject = syncObjects.get(clazz);

            if (syncObject == null) {

                syncObject = new Object() {

                    @Override
                    public String toString() {
                        return "LOCAL_SYNC:" + clazz.getCanonicalName();
                    }
                };

                LOCAL_SYNC_OBJECTS.put(clazz, syncObject);
            }

            return syncObject;
        }
    }
}
