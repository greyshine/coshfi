package de.greyshine.coffeeshopfinder.entity;

import de.greyshine.json.crud.JsonCrudService;
import de.greyshine.json.crud.JsonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

import static de.greyshine.coffeeshopfinder.utils.Utils.isEqualsTrimmed;

@Service
@Slf4j
public class UserCrudService extends JsonCrudService {

    public UserCrudService(@Autowired JsonService jsonService) {
        super(jsonService);
    }

    public boolean isLogin(final String login) {

        final AtomicBoolean result = new AtomicBoolean(false);

        super.iterate(UserEntity.class, Sync.LOCAL, userEntity -> {

            if (isEqualsTrimmed(login, userEntity.getLogin(), false)) {
                result.set(true);
                return false;
            }

            return null;
        });

        return result.get();
    }

    public boolean isName(final String user) {

        final AtomicBoolean result = new AtomicBoolean(false);

        super.iterate(UserEntity.class, Sync.LOCAL, userEntity -> {

            if (isEqualsTrimmed(user, userEntity.getName(), false)) {
                result.set(true);
                return false;
            }

            return null;
        });

        return result.get();
    }
}
