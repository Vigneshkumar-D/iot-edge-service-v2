package com.iot_edge.managementconsole.service.authentication;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;

/*@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String body) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            message.setFrom("IotEdge Team <care.iotdge@gmail.com>");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public String compileTemplate(String emailTemplateName, Map<String, Object> emailOTPTemplateData) {
    }
}*/

import com.samskivert.mustache.Mustache.Compiler;
import com.samskivert.mustache.Template;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class EmailService {

    @Value("${sendgrid.api_key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from_email}")
    private String fromEmail;

    private final Compiler mustacheCompiler;

    public EmailService(Compiler mustacheCompiler) {
        this.mustacheCompiler = mustacheCompiler;
    }

    @Transactional(rollbackFor = Exception.class)
    public Response sendEmail(String toEmail, String subject, String contentString) throws IOException {

        log.info("Sending email with SendGrid to {}", toEmail);

        Email from = new Email(fromEmail); // Your email registered with SendGrid
        Email to = new Email(toEmail);
        Content content = new Content("text/html", contentString);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        return sg.api(request);
    }

    public String compileTemplate(String templateName, Map<String, Object> model) throws IOException {
        try (Reader templateReader = new InputStreamReader(new ClassPathResource("email-templates/" + templateName).getInputStream())) {
            Template template = mustacheCompiler.compile(templateReader);
            return template.execute(model);
        }
    }

    @Transactional(rollbackFor=Exception.class)
    public Response sendEmailWithAttachment(String toEmail, String subject, String contentString, List<MultipartFile> files) throws IOException {

        log.info("Sending email with SendGrid with Attachment to {}", toEmail);

        Email from = new Email(fromEmail); // Your email registered with SendGrid
        Email to = new Email(toEmail);
        Content content = new Content("text/html", contentString);
        Mail mail = new Mail(from, subject, to, content);

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                Attachments attachment = createAttachment(file);
                mail.addAttachments(attachment);
            }
        }

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        return sg.api(request);
    }

    private Attachments createAttachment(MultipartFile file) throws IOException {
        byte[] encodedFileContent = Base64.getEncoder().encode(file.getBytes());
        Attachments attachment = new Attachments();
        attachment.setDisposition("attachment");
        attachment.setType(file.getContentType());
        attachment.setFilename(file.getOriginalFilename());
        attachment.setContent(new String(encodedFileContent, StandardCharsets.UTF_8));
        return attachment;
    }
}

