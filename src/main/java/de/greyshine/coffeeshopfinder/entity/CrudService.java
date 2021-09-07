package de.greyshine.coffeeshopfinder.entity;

import de.greyshine.coffeeshopfinder.service.JsonService;
import de.greyshine.coffeeshopfinder.utils.Utils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;


import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
public abstract class CrudService<T extends Entity> {

    public static final Object SYNC_GLOBAL = new Object() {
        @Override
        public String toString() {
            return "SYNC for GLOBAL";
        }
    };
    public final Object SYNC_LOCAL = new Object() {
        @Override
        public String toString() {
            return "SYNC for " + CrudService.this;
        }
    };
    private final Class<T> entityClass;

    /*
    private final Map<String, T> items = new HashMap<>();
    */

    @Autowired
    private JsonService jsonService;

    public CrudService(Class<T> entityClass) {
        Assert.notNull(entityClass, "entity class is null");
        this.entityClass = entityClass;
    }

    @PostConstruct
    public void postConstruct() {
        jsonService.createDirIFNotExists(entityClass);
    }

    public <T extends Entity> String create(T item) {

        Assert.notNull(item, "item to persist is null");

        Sync sync = Sync.NONE;
        String id = item.getId();
        if (id == null) {
            id = UUID.randomUUID().toString();
            item.setId( id );
        } else {
            sync = Sync.LOCAL;
        }

        return executeSynced( sync, ()->{
            item.updateCreated();
            final long bytes = jsonService.save(item, false);
            log.debug( "stored {}#{} with {} bytes", item.getClass().getCanonicalName(), item.getId(), bytes );
            return item.getId();
        } );
    }

    public T read(String id, Sync sync) {
        return executeSynced( sync, ()->jsonService.load( this.entityClass, id ) );
    }

    public <T extends Entity> void update(Sync sync, T item) {

        executeSynced( sync, ()->{
            item.updateUpdated();
            jsonService.save(item, true);
            return item;
        } );
    }

    public <T extends Entity> String upsert(Sync sync, T item) {

        Assert.notNull( item, "Item is null" );

        synchronized ( Sync.getDefault(sync) ) {

            if ( jsonService.isPersisted( item ) ) {

                update(sync, item);
                return item.getId();

            } else {

                return create( item );
            }
        }
    }

    public <T extends Entity> boolean delete(Sync sync, Class<T> clazz, String id) {

        return executeSynced( sync, ()-> {
            return jsonService.delete(clazz, id);
        } );
    }

    public List<T> iterate(Sync sync, Utils.Function2<T, Boolean> function) {

        sync = Sync.getDefault(sync);
        Assert.notNull(function, "Consumer is null");

        final Iterator<String> idsIter = jsonService.getIds( this.entityClass );

        final List<T> results = new ArrayList<>(0);

        while( idsIter.hasNext() ) {

            final String id = idsIter.next();

            try {

                final T item = jsonService.load( this.entityClass, id );

                final Boolean result = function.apply(item);

                if (Boolean.TRUE == result) {
                    results.add(item);
                } else if (Boolean.FALSE == result) {
                    break;
                }

            } catch (Exception exception) {
                log.warn( "Unable to handle item with ID={}", id, exception );
            }
        }

        return results;
    }

    public Object getSyncObject(Sync sync) {
        if ( Sync.GLOBAL == sync ) {
            return SYNC_GLOBAL;
        } else if ( Sync.LOCAL == sync ) {
            return SYNC_LOCAL;
        } else {
            return null;
        }
    };

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

    @SneakyThrows
    private <S> S executeSynced( Sync sync, Utils.Supplier2<S> supplier ) {

        Assert.notNull( supplier, "No supplier given." );

        Object syncObject = null;

        if ( Sync.GLOBAL == sync ) {
            syncObject = SYNC_GLOBAL;
        } else if (Sync.LOCAL == sync) {
            syncObject = SYNC_LOCAL;
        } else {
            return supplier.get();
        }

        synchronized ( syncObject ) {
            return supplier.get();
        }
    }
}
