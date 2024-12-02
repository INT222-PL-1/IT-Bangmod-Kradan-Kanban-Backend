package sit.int221.itbkkbackend.v3.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import sit.int221.itbkkbackend.auth.CustomUserDetails;
import sit.int221.itbkkbackend.auth.entities.Users;
import sit.int221.itbkkbackend.v3.entities.UserV3;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendSimpleEmail(String toEmail,
                                String subject,
                                String body
    ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ITBKK-PL1 <itbkk.pl1@gmail.com>");
        message.setTo(toEmail);
        message.setText(body);
        message.setSubject(subject);
        message.setReplyTo("DO NOT REPLY <noreply@intproj23.sit.kmutt.ac.th>");
        mailSender.send(message);
    }

    public void sendHtmlEmail(String toEmail,
                              String subject,
                              String htmlBody
    ) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("ITBKK-PL1 <itbkk.pl1@gmail.com>");
        helper.setTo(toEmail);
        helper.setText(htmlBody, true);
        helper.setSubject(subject);
        helper.setReplyTo("DO NOT REPLY <noreply@intproj23.sit.kmutt.ac.th>");
        mailSender.send(message);
    }

    public void sendInvitationEmail(UserV3 toUser, String accessRight, CustomUserDetails senderDetails, String boardId, String boardName, String requestUrl) throws MessagingException, IOException {
        String senderName = senderDetails.getName();
        String toUsername = toUser.getName();
        String toEmail = toUser.getEmail();
        
        String subject = String.format("%s has invited you to collaborate with %s access right on %s board", senderName, accessRight, boardName);
        String link = String.format("%s/board/%s/collab/invitations", requestUrl, boardId);
        
        String template = null;
        String htmlBody = null;

        try {
            template = readResourceFile("templates/invitation_template.html");
        } catch (IOException e) {
            String body = "Hello,\n\n"
                    + "You have been invited to collaborate on the board: " + boardName + ".\n\n"
                    + "Please use the following link to either accept or decline the invitation:\n"
                    + link + "\n\n"
                    + "Thank you!\n\n";
            sendSimpleEmail(toEmail, subject, body);
            return;
        }
        htmlBody = template.replace("{{userName}}", toUsername)
                        .replace("{{boardName}}", boardName)
                        .replace("{{link}}", link)
                        .replace("{{senderName}}", senderName)
                        .replace("{{accessRight}}", accessRight);
        sendHtmlEmail(toEmail, subject, htmlBody);
    }

    private String readResourceFile(String filePath) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}

