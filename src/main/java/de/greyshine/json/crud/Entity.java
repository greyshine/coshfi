package de.greyshine.json.crud;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@EqualsAndHashCode
public abstract class Entity {

    public static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Getter
    @Setter
    @NotBlank(message = "id is mandatory")
    private String id;

    @NotBlank(message = "created is mandatory")
    @Getter
    private String created = LocalDateTime.now().format(DTF);
    @Getter
    private String updated;
    @Getter
    private String deleted;

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

        this.created = LocalDateTime.now().format(DTF);
    }

    final void updateUpdated() {
        this.updated = LocalDateTime.now().format(DTF);
    }

    final void updateDeleted() {
        this.deleted = LocalDateTime.now().format(DTF);
    }

    /**
     * @return true if deleted date is given otherwise false
     */
    public boolean isDeleted() {
        return deleted != null;
    }

    public String toString() {

        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append("(id=").append(this.id);
        sb.append(", c/u/d=");
        sb.append(this.created);
        sb.append('/').append(updated == null ? '-' : updated);
        sb.append('/').append(deleted == null ? '-' : deleted);

        for( Field f : getClass().getDeclaredFields() ) {

            final int mods = f.getModifiers();

            if ( Modifier.isStatic(mods) ) {
                continue;
            }

            try {
                f.setAccessible(true);
                sb.append( new StringBuilder().append( ", " ).append( f.getName() ).append('=').append( f.get( this ) ) );
            } catch (IllegalAccessException e) {
                // intended ignore
            }
        }

        return sb.append( ')' ).toString();
    }
}
