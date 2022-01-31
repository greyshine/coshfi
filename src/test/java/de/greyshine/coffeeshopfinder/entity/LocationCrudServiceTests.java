package de.greyshine.coffeeshopfinder.entity;

import de.greyshine.latlon.Latlon;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.TestPropertySources;

import java.util.UUID;

@SpringBootTest(
        args = {"--datadir=app-testdata"},
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@TestPropertySources({
        @TestPropertySource("classpath:application-test.properties"),
        @TestPropertySource("classpath:email-off.properties"),
})
@Slf4j
public class LocationCrudServiceTests {

    @Test
    public void test() {

        final Location location = new Location();
        location.setName("Hocuspocus CBD");
        location.setId(UUID.randomUUID().toString());
        location.setStreet("Marensweg 12");
        location.setZip("3222");
        location.setCity("Town");
        location.setCountry("Switzerland");
        location.setLatlon(Latlon.latlon("47.485023,9.679567"));
        location.setType(Location.EType.BAR);
        location.setEmail("kontakt@hocuspocus-cbd.com");
        location.setWww("https://hocuspocus-cbd.com/");
        location.setOpeningtimes("9-19;9-19;9-19;9-19;9-19;12-16;12-16");

        location.setExtra("{\"\":{ \"url\":\"https://weedmaps.com/deliveries/hocuspocus-cbd/about\" }}");

        log.info("{}", location);


    }

}
