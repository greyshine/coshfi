package de.greyshine.coffeeshopfinder.web;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@Slf4j
public class IndexController {

    @Value("${app.version}")
    private String version;

    @GetMapping(value = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Information getInformation() {

        final Information information = new Information();

        return information;
    }

    @Getter
    public class Information {
        private final String time = LocalDateTime.now().format( DateTimeFormatter.ISO_LOCAL_DATE_TIME );
        private final String version = IndexController.this.version;
    }
}
