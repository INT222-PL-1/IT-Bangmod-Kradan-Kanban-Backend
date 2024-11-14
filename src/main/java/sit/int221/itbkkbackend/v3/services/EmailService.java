package sit.int221.itbkkbackend.v3.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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

}

