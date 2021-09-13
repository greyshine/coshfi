package de.greyshine.coffeeshopfinder.entity;

import de.greyshine.coffeeshopfinder.utils.Latlon;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private LocationCrudService locationCrudService;

    @Test
    public void test() {

        Location location = new Location();
        location.setName("Hocuspocus CBD");
        location.setId(UUID.randomUUID().toString());
        location.setAddress("Marensweg 12; 3222 Town; Switzerland");
        location.setLatlon(Latlon.latlon("47.485023,9.679567"));
        location.setType(Location.EType.Bar);
        location.setEmail("kontakt@hocuspocus-cbd.com");
        location.setWww("https://hocuspocus-cbd.com/");
        location.setOpeningtimes("9-19;9-19;9-19;9-19;9-19;12-16;12-16");

        log.info("\n{}", locationCrudService.toString(location));


    }

}
