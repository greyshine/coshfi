package de.greyshine.coffeeshopfinder;


import de.greyshine.coffeeshopfinder.utils.Latlon;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class UtilsTests {

    @Test
    public void testLatlon() {

        log.info("{}", new Latlon("12.21,23.12"));
    }

}
