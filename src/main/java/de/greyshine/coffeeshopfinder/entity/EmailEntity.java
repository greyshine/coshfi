package de.greyshine.coffeeshopfinder.entity;

import de.greyshine.coffeeshopfinder.service.EmailService;
import de.greyshine.coffeeshopfinder.utils.Utils;
import de.greyshine.json.crud.Entity;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Data
public class EmailEntity extends Entity {

    public static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmssSSS");
    static final AtomicLong IDS = new AtomicLong(0);
    private final List<Attachment> attachments = new ArrayList<>();
    private LocalDateTime sentTime;
    private String receiver;
    private String subject;
    private String contentHtml;
    private String contentText;
    private String error;
    private LocalDateTime errorTime;

    @Override
    public void beforeCreate() {
        setId(LocalDateTime.now().format(DTF) + "-" + Long.toString(IDS.getAndAdd(1), 16));
    }

    public EmailEntity markSent() {
        this.sentTime = LocalDateTime.now();
        return this;
    }

    public void setError(String error) {
        this.errorTime = LocalDateTime.now();
        this.error = error == null ? "" : error.strip();
    }

    public EmailEntity addAttachment(EmailService.Attachment attachment) {

        if (attachment == null) {
            return this;
        }

        final Attachment a = new Attachment();
        a.setName(attachment.getName());
        a.setContentType(attachment.getContentType());
        a.setData(attachment.getFile() == null || !attachment.getFile().isFile()
                ? null
                : Utils.toBase64(attachment.getFile()));

        this.attachments.add(a);
        return this;
    }

    public EmailEntity addAttachments(Set<EmailService.Attachment> attachments) {

        if (attachments == null) {
            return this;
        }

        attachments.forEach(attachment -> addAttachment(attachment));

        return this;
    }

    @Data
    public static final class Attachment {
        private String name;
        private String contentType;
        private String data;
    }

}
