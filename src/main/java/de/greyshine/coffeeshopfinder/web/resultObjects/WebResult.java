package de.greyshine.coffeeshopfinder.web.resultObjects;

import lombok.Getter;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public abstract class WebResult {

    public static final String V1 = "1.0.0";

    @Getter
    private final String version;

    @Getter
    private final String time = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    protected WebResult(String version) {

        Assert.isTrue(isNotBlank(version), "version is blank");

        this.version = version;
    }

}
