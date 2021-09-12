package de.greyshine.coffeeshopfinder;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "email")
@Data
public class EmailConfiguration {

    private File templateDir;

    private boolean active = true;

    private String host;
    private int port;

    private String username;
    private String password;
    private String sender;

    private boolean debug = false;
}
