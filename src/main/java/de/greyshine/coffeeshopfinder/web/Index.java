package de.greyshine.coffeeshopfinder.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class Index {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String index() {

        log.info("index");

        return "index.html";
    }

}
