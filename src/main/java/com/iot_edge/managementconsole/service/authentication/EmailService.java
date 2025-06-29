package com.iot_edge.managementconsole.service.authentication;
//
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//
//import java.util.Map;
//
///*@Service
//public class EmailService {
//
//    private final JavaMailSender mailSender;
//
//    public EmailService(JavaMailSender mailSender) {
//        this.mailSender = mailSender;
//    }
//
//    public void sendEmail(String to, String subject, String body) {
//
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//            message.setFrom("IotEdge Team <care.iotdge@gmail.com>");
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(body, true);
//            mailSender.send(message);
//        } catch (MessagingException e) {
//            throw new RuntimeException("Failed to send email", e);
//        }
//    }
//
//    public String compileTemplate(String emailTemplateName, Map<String, Object> emailOTPTemplateData) {
//    }
//}*/
//
//import com.samskivert.mustache.Mustache.Compiler;
//import com.samskivert.mustache.Template;
//import com.sendgrid.Method;
//import com.sendgrid.Request;
//import com.sendgrid.Response;
//import com.sendgrid.SendGrid;
//import com.sendgrid.helpers.mail.Mail;
//import com.sendgrid.helpers.mail.objects.Attachments;
//import com.sendgrid.helpers.mail.objects.Content;
//import com.sendgrid.helpers.mail.objects.Email;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.Reader;
//import java.nio.charset.StandardCharsets;
//import java.util.Base64;
//import java.util.List;
//
//@Service
//@Slf4j
//public class EmailService {
//
//    @Value("${sendgrid.api_key}")
//    private String sendGridApiKey;
//
//    @Value("${sendgrid.from_email}")
//    private String fromEmail;
//
//    private final Compiler mustacheCompiler;
//    private final JavaMailSender mailSender;
//
//    public EmailService(Compiler mustacheCompiler, JavaMailSender mailSender) {
//        this.mustacheCompiler = mustacheCompiler;
//        this.mailSender = mailSender;
//    }
//
////    @Transactional(rollbackFor = Exception.class)
////    public Response sendEmail(String toEmail, String subject, String contentString) throws IOException {
////
////        log.info("Sending email with SendGrid to {}", toEmail);
////
////        Email from = new Email(fromEmail); // Your email registered with SendGrid
////        Email to = new Email(toEmail);
////        Content content = new Content("text/html", contentString);
////        Mail mail = new Mail(from, subject, to, content);
////
////        SendGrid sg = new SendGrid(sendGridApiKey);
////        Request request = new Request();
////        request.setMethod(Method.POST);
////        request.setEndpoint("mail/send");
////        request.setBody(mail.build());
////
////        return sg.api(request);
////    }
//
//
//
//
//    public void sendEmail(String to, String subject, String body) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//            message.setFrom("IotEdge Team <care.iotdge@gmail.com>");
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(body, true);
//            mailSender.send(message);
//        } catch (MessagingException e) {
//            throw new RuntimeException("Failed to send email", e);
//        }
//    }
//
//    public String compileTemplate(String templateName, Map<String, Object> model) throws IOException {
//        try (Reader templateReader = new InputStreamReader(new ClassPathResource("email-templates/" + templateName).getInputStream())) {
//            Template template = mustacheCompiler.compile(templateReader);
//            return template.execute(model);
//        }
//    }
//
//    @Transactional(rollbackFor=Exception.class)
//    public Response sendEmailWithAttachment(String toEmail, String subject, String contentString, List<MultipartFile> files) throws IOException {
//
//        log.info("Sending email with SendGrid with Attachment to {}", toEmail);
//
//        Email from = new Email(fromEmail); // Your email registered with SendGrid
//        Email to = new Email(toEmail);
//        Content content = new Content("text/html", contentString);
//        Mail mail = new Mail(from, subject, to, content);
//
//        if (files != null && !files.isEmpty()) {
//            for (MultipartFile file : files) {
//                Attachments attachment = createAttachment(file);
//                mail.addAttachments(attachment);
//            }
//        }
//
//        SendGrid sg = new SendGrid(sendGridApiKey);
//        Request request = new Request();
//        request.setMethod(Method.POST);
//        request.setEndpoint("mail/send");
//        request.setBody(mail.build());
//
//        return sg.api(request);
//    }
//
//    private Attachments createAttachment(MultipartFile file) throws IOException {
//        byte[] encodedFileContent = Base64.getEncoder().encode(file.getBytes());
//        Attachments attachment = new Attachments();
//        attachment.setDisposition("attachment");
//        attachment.setType(file.getContentType());
//        attachment.setFilename(file.getOriginalFilename());
//        attachment.setContent(new String(encodedFileContent, StandardCharsets.UTF_8));
//        return attachment;
//    }
//}
//



import com.sendgrid.*;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class EmailService {

    @Value("${sendgrid.api_key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from_email}")
    private String fromEmail;

    private final Mustache.Compiler mustacheCompiler;
    private final JavaMailSender mailSender;

    public EmailService(Mustache.Compiler mustacheCompiler, JavaMailSender mailSender) {
        this.mustacheCompiler = mustacheCompiler;
        this.mailSender = mailSender;
    }

    // Compile a Mustache template from resources
    public String compileTemplate(String templateName, Map<String, Object> model) throws IOException {
        String path = "email-templates/" + templateName + ".mustache";
        Resource resource = new ClassPathResource(path);

        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            Template template = mustacheCompiler.compile(reader);
            return template.execute(model);
        }
    }

    // Send email via JavaMail (SMTP)
    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            helper.setFrom(new InternetAddress(fromEmail, "IotEdge Team"));

            mailSender.send(message);
            log.info("SMTP email sent to {}", to);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to send SMTP email", e);
        }
    }

    // Send email via SendGrid (basic HTML)
    @Transactional(rollbackFor = Exception.class)
    public Response sendEmailWithSendGrid(String toEmail, String subject, String htmlBody) throws IOException {
        validateSendGridApiKey();

        Email from = new Email(fromEmail);
        Email to = new Email(toEmail);
        Content content = new Content("text/html", htmlBody);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        log.info("Sending email with SendGrid to {}", toEmail);
        return sg.api(request);
    }

    // Send email with attachments using SendGrid
    @Transactional(rollbackFor = Exception.class)
    public Response sendEmailWithAttachment(String toEmail, String subject, String htmlBody, List<MultipartFile> files) throws IOException {
        validateSendGridApiKey();

        Email from = new Email(fromEmail);
        Email to = new Email(toEmail);
        Content content = new Content("text/html", htmlBody);
        Mail mail = new Mail(from, subject, to, content);

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                mail.addAttachments(createAttachment(file));
            }
        }

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        log.info("Sending email with SendGrid + attachments to {}", toEmail);
        return sg.api(request);
    }

    private Attachments createAttachment(MultipartFile file) throws IOException {
        byte[] fileContent = Base64.getEncoder().encode(file.getBytes());
        Attachments attachment = new Attachments();
        attachment.setDisposition("attachment");
        attachment.setType(file.getContentType() != null ? file.getContentType() : "application/octet-stream");
        attachment.setFilename(file.getOriginalFilename());
        attachment.setContent(new String(fileContent, StandardCharsets.UTF_8));
        return attachment;
    }

    private void validateSendGridApiKey() {
        if (sendGridApiKey == null || sendGridApiKey.isEmpty()) {
            throw new IllegalStateException("SendGrid API key is not configured.");
        }
    }

    // Send email with attachments using JavaMail
    @Transactional(rollbackFor = Exception.class)
    public void sendEmailWithAttachments(
            String to,
            String subject,
            String htmlBody,
            List<MultipartFile> attachments
    ) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // true = multipart
            helper.setFrom("IoT Edge Team <care.iotdge@gmail.com>");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // HTML content

            if (attachments != null && !attachments.isEmpty()) {
                for (MultipartFile file : attachments) {
                    helper.addAttachment(
                            Objects.requireNonNull(file.getOriginalFilename()),
                            new ByteArrayResource(file.getBytes())
                    );
                }
            }

            mailSender.send(message);
        } catch (MessagingException | IOException e) {
            throw new RuntimeException("Failed to send email with attachments", e);
        }
    }

}

