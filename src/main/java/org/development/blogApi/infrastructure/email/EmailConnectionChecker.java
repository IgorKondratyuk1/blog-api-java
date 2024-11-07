package org.development.blogApi.infrastructure.email;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

//TODO only for Prod
//@Component
//public class EmailConnectionChecker {
//
//    @Value("${spring.mail.username}")
//    private String email;
//
//    @Autowired
//    private JavaMailSender javaMailSender;
//
//    @PostConstruct
//    public void checkEmailConnection() {
//        try {
//            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
//            helper.setTo(email);
//            helper.setSubject("Connection Test");
//            helper.setText("This is a test to check the email server connection.", true);
//
//            // Attempt to send a test email (Note: This email should be sent to a valid address in your domain)
//            javaMailSender.send(mimeMessage);
//            System.out.println("Connection to the email server is successful.");
//        } catch (MessagingException e) {
//            System.err.println("Failed to connect to the email server: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//}
