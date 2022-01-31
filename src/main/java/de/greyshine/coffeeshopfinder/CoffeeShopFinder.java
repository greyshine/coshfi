package de.greyshine.coffeeshopfinder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * TODO: disable session token
 * <p>
 * Startup parameters:
 * <p>
 * --dir.home=&lt;PATH&gt;
 */
@Slf4j
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@ComponentScan("de.greyshine")
@PropertySources({
        @PropertySource("classpath:application.properties"),
        @PropertySource(value = "classpath:ssl.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "classpath:email.properties", ignoreResourceNotFound = true)
})
public class CoffeeShopFinder implements ApplicationListener<ApplicationReadyEvent> {

    private final LocalDateTime appStart = LocalDateTime.now();
    private final Environment environment;

    @Value("${app.version}")
    private String version;

    @Value("${server.ssl.enabled:false}")
    private boolean sslEnabled;

    public CoffeeShopFinder(@Autowired Environment environment) {
        this.environment = environment;
    }

    public static void main(String[] args) {
        SpringApplication.run(CoffeeShopFinder.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {

        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                log.error("THIS is a development setting: allowed-origin=* FOR /**");
                // https://www.freecodecamp.org/news/access-control-allow-origin-header-explained/
                registry.addMapping("/**").allowedOrigins("*");
            }
        };
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {

        log.info("ssl-enabled: {}", this.sslEnabled);
        log.info("version: {}", version);
        log.info("web-port: {}", environment.getProperty("local.server.port"));
        log.info("home-dir: {}", environment.getProperty("data"));
        log.info("start-time: {}", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

        final long startuptime = System.currentTimeMillis() - appStart.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        log.info("start-durance: {}ms", startuptime);
    }
}