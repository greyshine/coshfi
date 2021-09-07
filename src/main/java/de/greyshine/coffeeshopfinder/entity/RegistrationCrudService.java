package de.greyshine.coffeeshopfinder.entity;

import static org.apache.commons.lang3.StringUtils.trimToNull;

import de.greyshine.coffeeshopfinder.service.ValidationService;
import de.greyshine.coffeeshopfinder.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class RegistrationCrudService extends CrudService<RegistrationEntity> {

    @Autowired
    private ValidationService validationService;

    @Autowired
    private UserCrudService userCrudService;

    public RegistrationCrudService() {
        super(RegistrationEntity.class);
    }

    public String create(RegistrationEntity registrationEntity) {

        Assert.notNull(registrationEntity, RegistrationEntity.class.getCanonicalName() + " is null");
        Assert.isNull(registrationEntity.getId(), RegistrationEntity.class.getCanonicalName() + " is null");
        Assert.isTrue( validationService.isValidLogin( registrationEntity.getLogin() ), RegistrationEntity.class.getCanonicalName() + " illegal login: "+ registrationEntity.getLogin() );
        Assert.isTrue( validationService.isValidPassword( registrationEntity.getPassword() ), RegistrationEntity.class.getCanonicalName() + " illegal login: "+ registrationEntity.getPassword() );

        Assert.isTrue( !isLoginOrName( registrationEntity.getLogin(), registrationEntity.getName() ), "Login or user is used: login="+ registrationEntity.getLogin()+", user="+ registrationEntity.getName() );

        Assert.isTrue( userCrudService.isLogin( registrationEntity.getLogin() ), "Login already exists: "+ registrationEntity.getLogin() );
        Assert.isTrue( userCrudService.isName(registrationEntity.getName()), "User already exists: "+ registrationEntity.getName() );

        registrationEntity.setId(UUID.randomUUID().toString());

        // TODO synchronize all registrations
        // TODO check that it does not exist
        // - as another registration
        // - as an existing login

        return super.create(registrationEntity);
    }

    public boolean isLoginOrName(final String login, final String name) {

        Assert.isTrue( trimToNull(login) != null || trimToNull(name) != null, "Login and User are null" );

        final AtomicBoolean result = new AtomicBoolean(false);

        super.iterate( Sync.LOCAL, (re)->{

            if ( Utils.isEqualsTrimmed( re.getLogin(), login, false) ) {
                result.set(true);
                return false;
            } else if ( Utils.isEqualsTrimmed( re.getName(), name, false) ) {
                result.set(true);
                return false;
            }

            return null;
        } );

        return result.get();
    }

}