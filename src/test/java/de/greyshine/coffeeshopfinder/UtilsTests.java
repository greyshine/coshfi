package de.greyshine.coffeeshopfinder;


import de.greyshine.latlon.Latlon;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class UtilsTests {

    public static final String[] PROGRAM_ARGS = {
            "--datadir=target/test-datadir",
            "--email-template-dir=src/main/email-templates"
    };

    @Test
    public void testLatlon() {

        log.info("{}", Latlon.latlon("12.21,23.12"));
    }

}
