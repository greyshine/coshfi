package de.greyshine.coffeeshopfinder.entity;

import de.greyshine.json.crud.JsonCrudService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SpringBootTest(args = {"--datadir=target/test-datadir"})
@Slf4j
public class EntityTests {

    @Autowired
    private RegistrationCrudService registrationCrudService;

    @BeforeAll
    public static void beforeAll() throws IOException {

        final File file = new File("target/test-datadir").getCanonicalFile();

        if (!file.isDirectory()) {
            file.mkdirs();
            Assert.isTrue(file.isDirectory(), "Root directory is not accessible: " + file.getAbsolutePath());
        }

        log.info("Root dir: {}", file.getAbsolutePath());
    }

    @Test
    public void cycle() {

        RegistrationEntity re = new RegistrationEntity();
        re.setLogin("login");
        re.setPassword("aA!123456");
        re.setName("This is my name");
        re.setEmail("some@email.com");
        registrationCrudService.create(re);

        log.info("persisted={}", re);
        log.info("id={}", re.getId());

        final List<RegistrationEntity> res =
                registrationCrudService.iterate(RegistrationEntity.class, JsonCrudService.Sync.NONE, registrationEntity -> {
                    log.info("{}", registrationEntity);
                    return null;
                });
    }



    /*
    @Test
    public void testRead() throws IOException {
        log.info( "{}", jsonService.load( UserEntity.class, "user1") );
    }

    @Test
    public void testWrite() throws IOException {

        final UserEntity userEntity = new UserEntity();
        userEntity.setUser("test");

        final Location location = new Location();
        location.setId("some-id");
        location.setName("Kronkel");
        location.setAddress("Vlaamsegas 26-36, 6511 HR Nijmegen, Netherlands");

        location.setLatlon( new Latlon("51.8437756,5.8641647") );
        userEntity.getLocations().add( location );

        String userEntityJson = jsonService.serialize(userEntity);
        long dataSize = jsonService.save( userEntity, false );
        log.info( "1\n{}", userEntity );

        UserEntity userEntity2 = jsonService.deserialize( UserEntity.class, userEntityJson );
        log.info( "1-back\n{}", jsonService.serialize( userEntity2 ) );

        location.setLatlon(null);
        userEntityJson = jsonService.serialize( userEntity );
        log.info( "2\n{}", userEntityJson );
        userEntity2 = jsonService.deserialize( UserEntity.class, userEntityJson );
        log.info( "2-back\n{}", jsonService.serialize( userEntity2 ) );


        location.setLatlon( new Latlon().lat("123.123") );
        Assertions.assertThrows( ConstraintViolationException.class , ()->jsonService.serialize( userEntity ) );
    }
    */
}
