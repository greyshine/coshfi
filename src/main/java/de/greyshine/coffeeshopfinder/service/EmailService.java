package de.greyshine.coffeeshopfinder.service;

import de.greyshine.coffeeshopfinder.EmailConfiguration;
import de.greyshine.coffeeshopfinder.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.*;

@Service
@Slf4j
public class EmailService {

    private EmailConfiguration configuration;

    @Autowired
    private JavaMailSender javaMailSender;

    public EmailService(@Autowired EmailConfiguration configuration) {
        this.configuration = configuration;
    }

    @PostConstruct
    public void postConstruct() {

        Assert.notNull(configuration.getTemplateDir(), "no email template dir");
        final var emailTemplateDir = Utils.getFile(configuration.getTemplateDir());
        Assert.isTrue(emailTemplateDir.isDirectory() && emailTemplateDir.canRead(), "Cannot access email template dir: " + emailTemplateDir.getAbsolutePath());

        log.info("email-templates: {}", emailTemplateDir);
    }

    @Bean
    public JavaMailSender getJavaMailSender() {

        Assert.notNull(configuration, "No configuration is set");
        Assert.isTrue(isNotBlank(configuration.getUsername()), "Username is blank");

        final var mailSender = new JavaMailSenderImpl();
        mailSender.setHost(configuration.getHost());
        mailSender.setPort(configuration.getPort());

        mailSender.setUsername(configuration.getUsername());
        mailSender.setPassword(configuration.getPassword());

        final var properties = mailSender.getJavaMailProperties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.debug", String.valueOf(configuration.isDebug()));

        return mailSender;
    }

    @SneakyThrows
    public void sendEmailByTemplate(final String receiverEmail, final String filenamePrefix, final String lang,
                                    Map<String, String> params,
                                    Set<Attachment> attachments,
                                    Set<InlineContent> inlineContents) {

        Assert.isTrue(isNotBlank(filenamePrefix), "No filenamePrefix defined");

        params = params != null ? params : Collections.emptyMap();
        inlineContents = inlineContents != null ? inlineContents : Collections.emptySet();

        var filename = filenamePrefix + (lang == null ? "" : "." + lang) + ".txt";
        var templateFile = new File(configuration.getTemplateDir(), filename);

        if (!templateFile.exists() && lang != null) {
            filename = filenamePrefix + ".txt";
            templateFile = new File(configuration.getTemplateDir(), filename);
        }

        templateFile = Utils.getFile(templateFile);
        Assert.isTrue(templateFile.isFile(), "The file seems not to exist: " + templateFile);

        final var content = Utils.readString(templateFile);

        var subject = content.substring(0, content.indexOf('\n')).trim();
        var message = content.substring(content.indexOf('\n') + 1).trim();

        for (InlineContent inlineContent : inlineContents) {
            final var k = "${" + inlineContent.variable + "}";
            message = message.replace(k, inlineContent.toBase64());
        }

        for (var variable : params.keySet()) {

            if (isBlank(variable)) {
                continue;
            }

            final var k = "${" + variable + "}";

            subject = subject.replace(k, params.get(variable));
            message = message.replace(k, params.get(variable));
        }

        sendEmail(receiverEmail, subject, message, null, attachments);
    }

    @SneakyThrows
    public void sendEmail(String receiverEmail, String subject, String htmlBody, String textBody) {
        sendEmail(receiverEmail, subject, htmlBody, textBody, Collections.emptySet());
    }

    /**
     * https://www.baeldung.com/spring-email
     *
     * @param receiverEmail
     * @param subject
     * @param htmlBody
     * @param attachments
     * @throws MessagingException
     */
    @SneakyThrows
    public void sendEmail(String receiverEmail, String subject, String htmlBody, String textBody, Set<Attachment> attachments) throws MessagingException {

        attachments = attachments != null ? attachments : Collections.emptySet();

        if (!configuration.isActive()) {
            log.warn("email is not active. No sending of email to {}, {}", receiverEmail, subject);
            log.debug("email-content would be ({} attachments):\n{}", attachments.size(), htmlBody);
            return;
        }

        final var message = javaMailSender.createMimeMessage();

        final var helper = new MimeMessageHelper(message, true);

        log.debug("sender: {}", configuration.getSender());

        helper.setFrom(configuration.getSender());
        helper.setTo(receiverEmail);
        helper.setSubject(trimToEmpty(subject));

        textBody = trimToNull(textBody);
        if (textBody != null) {
            log.debug("textBody:\n{}", textBody);
            helper.setText(textBody, false);
        }

        htmlBody = StringUtils.trimToNull(htmlBody);
        if (htmlBody != null) {
            log.debug("htmlBody:\n{}", htmlBody);
            helper.setText(htmlBody, true);
        }

        attachments = attachments != null ? attachments : Collections.emptySet();
        for (Attachment attachment : attachments) {
            helper.addAttachment(attachment.name, () -> new FileInputStream(attachment.file), attachment.contentType);
        }

        javaMailSender.send(message);
    }

    @AllArgsConstructor
    public static class Attachment {

        private final String name;
        private final String contentType;
        private final File file;

        @SneakyThrows
        public Attachment(File file) {

            file = Utils.getFile(file);

            Assert.notNull(file, "Given File is null");
            Assert.isTrue(file.isFile(), "Given File is no file: " + file.getAbsolutePath());

            this.name = nameWithoutDot(file);
            this.contentType = Files.probeContentType(file.toPath());
            this.file = file;
        }

        private static String nameWithoutDot(File file) {

            if (file == null) {
                return null;
            }

            final int idx = file.getName().lastIndexOf('.');
            if (idx < 0) {
                return file.getName();
            }

            return file.getName().substring(0, idx);
        }
    }

    @Data
    public static class InlineContent {

        public final String variable;
        public final String contentType;
        private final String data;

        @SneakyThrows
        public InlineContent(String variable, File file) {

            Assert.isTrue(isNotBlank(variable), "variable is blank");
            Assert.notNull(file, "File is null");
            Assert.isTrue(file.isFile() && file.canRead(), "File cannot be read");

            this.variable = variable;
            this.contentType = Files.probeContentType(file.toPath());

            final var bytes = Utils.read(file);
            data = Base64.getUrlEncoder().encodeToString(bytes);
        }

        public String toBase64() {
            return "data:" + contentType + ";base64," + data;
        }
    }
}
