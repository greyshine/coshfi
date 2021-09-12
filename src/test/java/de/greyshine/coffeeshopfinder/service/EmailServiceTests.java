package de.greyshine.coffeeshopfinder.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.TestPropertySources;

import javax.mail.MessagingException;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@SpringBootTest(
        args = {"--datadir=app-testdata"},
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@TestPropertySources({
        @TestPropertySource("classpath:application-test.properties"),
        @TestPropertySource("classpath:email.properties"),
})
@Slf4j
public class EmailServiceTests {

    @Autowired
    private EmailService emailService;

    @Test
    public void testSimpleSend() throws MessagingException {

        //emailService.sendEmail( "kuemmel.dss@gmx.de", "Test Subject"+ LocalDateTime.now(), LocalDateTime.now().toString());

        final Map<String, String> data = new HashMap<>();
        data.put("date", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        data.put("variable", "<b>VARY</b>");

        final Set<EmailService.Attachment> attachments = new LinkedHashSet<>();
        attachments.add(new EmailService.Attachment(new File("src/main/email-templates/testmail.txt")));
        attachments.add(new EmailService.Attachment(new File("src/main/email-templates/testimage.jpg")));

        final Set<EmailService.InlineContent> inlineContents = new HashSet<>();
        inlineContents.add(new EmailService.InlineContent("base64TestImage", new File("src/main/email-templates/testimage.jpg")));

        emailService.sendEmailByTemplate("kuemmel.dss@gmx.de", "testmail", "de", data, attachments, inlineContents);
    }

}
