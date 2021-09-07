package de.greyshine.coffeeshopfinder.entity;

import de.greyshine.coffeeshopfinder.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class UserCrudService extends CrudService<UserEntity>  {

    public UserCrudService() {
        super( UserEntity.class );
    }

    public boolean isLogin(final String login) {

        final AtomicBoolean result = new AtomicBoolean(false);

        super.iterate( Sync.LOCAL, userEntity->{

            if (Utils.isEqualsTrimmed( login, userEntity.getLogin(),false ) ) {
                result.set(true);
                return false;
            }

            return null;
        } );

        return result.get();
    }

    public boolean isName(final String user) {

        final AtomicBoolean result = new AtomicBoolean(false);

        super.iterate( Sync.LOCAL, userEntity->{

            if (Utils.isEqualsTrimmed( user, userEntity.getName(),false ) ) {
                result.set(true);
                return false;
            }

            return null;
        } );

        return result.get();
    }
}
