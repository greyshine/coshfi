package de.greyshine.coffeeshopfinder.web;

import de.greyshine.coffeeshopfinder.entity.UserCrudService;
import de.greyshine.coffeeshopfinder.utils.Utils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Controller
@Slf4j
public class IndexController {

    private final UserCrudService userCrudService;

    private AtomicLong pingCalls = new AtomicLong(0);

    @Value("${app.version}")
    private String version;

    @Value("${server.sslforfree.wellKnownFile:}")
    private String sslForFreeName;
    private String sslForFreeContent;

    public IndexController(@Autowired UserCrudService userCrudService) {
        this.userCrudService = userCrudService;
    }

    @PostConstruct
    public void postConstruct() throws IOException {

        if (isNotBlank(sslForFreeName)) {

            final var file = Utils.getFile(new File(sslForFreeName));
            log.info("loading sslForFree-file: {}", file);

            sslForFreeContent = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            Assert.isTrue(!sslForFreeContent.isEmpty(), "no content in " + Utils.getFile(file));
            log.debug("loaded sslForFree-file successfully:\n{}", sslForFreeContent);
        }
    }

    @GetMapping(value = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Information getInformation() {
        return new Information();
    }

    @GetMapping(value = "/.well-known/pki-validation/{key}", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String sslForFree(@PathVariable(value = "key") String filenameKey, HttpServletRequest request) {

        if (sslForFreeContent == null) {
            log.debug("sslForFree was requested but is not configured: {}.txt", filenameKey, Utils.getIp(request));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (!sslForFreeName.equals(filenameKey)) {
            log.debug("sslForFree was requested with different name: {}.txt", filenameKey);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return sslForFreeContent;
    }

    @GetMapping(value = "/api/ping", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public boolean ping(@RequestHeader(value = "TOKEN", required = false) String token) {

        token = isBlank(token) ? null : token.strip();

        pingCalls.addAndGet(1L);

        if (token != null) {
            return userCrudService.updateUserInfo(token);
        }

        return true;
    }

    @Getter
    public class Information {
        private final String time = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        private final String version = IndexController.this.version;
    }
}
