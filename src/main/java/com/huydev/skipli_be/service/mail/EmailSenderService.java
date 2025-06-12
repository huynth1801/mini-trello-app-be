package com.huydev.skipli_be.service.mail;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

@Service
@AllArgsConstructor
public class EmailSenderService {
    private final Configuration freemarkerConfig;
    private final JavaMailSender javaMailSender;

    public void sendVerificationToken(String toEmail, Map<String, Object> attributes) {
        String text = getEmailContent("verify-email.ftlh", attributes);
        sendEmail(toEmail, "Verify your email", text);
    }

    public void sendBoardInvitationEmail(String toEmail, Map<String, Object> attributes) {
        try {
            String text = getEmailContent("board-invitation.ftlh", attributes);
            sendEmail(toEmail, "You're invited to join a board!", text);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send board invitation email", e);
        }
    }

    private String getEmailContent(String templateName, Map<String, Object> model) {
        try (StringWriter writer = new StringWriter()) {
            freemarkerConfig.getTemplate(templateName).process(model, writer);
            return writer.toString();
        } catch (TemplateException | IOException e) {
            throw new RuntimeException("Can't load template email", e);
        }
    }

    private void sendEmail(String toEmail, String subject, String text) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("noreply@huydev.com");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(text, true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Can't send email", e);
        }
    }
}
