package de.greyshine.coffeeshopfinder.entity;

import de.greyshine.coffeeshopfinder.utils.Latlon;
import de.greyshine.coffeeshopfinder.service.JsonService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolationException;
import java.io.IOException;

@SpringBootTest(args ={"--datadir=data"})
@Slf4j
public class EntityTests {

    @Autowired
    private JsonService jsonService;

    @Test
    public void testRead() throws IOException {

        log.info( "{}", jsonService.load("user1") );
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

        String userEntityJson = jsonService.save( userEntity );
        log.info( "1\n{}", userEntityJson );

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
}
