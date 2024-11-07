package org.development.blogApi.infrastructure.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements EmailSender {

    @Value("${mail.use-sout}")
    private boolean useSout;

    @Value("${mail.sender-email}")
    private String senderEmail;

    private JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    @Override
    public void send(String to, String subject, String mailText) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(mailText);

        if (useSout) {
            System.out.println(mailText);
        } else {
            mailSender.send(message);
        }
    }
}
