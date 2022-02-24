package de.greyshine.json.crud;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.beans.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@EqualsAndHashCode
public abstract class Entity {

    public static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    public static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Getter
    @Setter
    @NotBlank(message = "id is mandatory")
    private String id;

    @Getter
    private final Meta _meta = new Meta();

    public void beforeCreate() {
    }

    public void beforeUpdate() {
    }

    public void beforeDelete(boolean physicalDelete) {
    }

    final void updateCreated() {

        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }

        this._meta.version++;
        this._meta.created = LocalDateTime.now();
    }

    final void updateUpdated() {
        this._meta.version++;
        this._meta.updated = LocalDateTime.now();
    }

    final void updateDeleted() {
        this._meta.version++;
        this._meta.deleted = LocalDateTime.now();
    }

    /**
     * @return true if deleted date is given otherwise false
     */
    @Transient
    public boolean isDeleted() {
        return _meta.deleted != null;
    }

    public String toString() {

        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append("(id=").append(this.id);
        sb.append(", c/u/d=");
        sb.append(this._meta.created);
        sb.append('/').append(_meta.updated == null ? '-' : _meta.updated);
        sb.append('/').append(_meta.deleted == null ? '-' : _meta.deleted);

        for (Field f : getClass().getDeclaredFields()) {

            final int mods = f.getModifiers();

            if (Modifier.isStatic(mods)) {
                continue;
            }

            sb.append(", ").append(f.getName()).append('=');
            try {

                f.setAccessible(true);
                sb.append(f.get(this));
            } catch (IllegalAccessException e) {
                // intended ignore
                sb.append('?');
            }
        }

        return sb.append(')').toString();
    }

    public static class Meta {

        @Getter
        private int version;

        @NotNull(message = "created is mandatory")
        @Getter
        private LocalDateTime created;

        @Getter
        private LocalDateTime updated;

        @Getter
        private LocalDateTime deleted;
    }
}
