package org.development.blogApi.infrastructure.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailManager {

    private final EmailSender emailSender;

    @Autowired
    public EmailManager(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendEmailConfirmationMessage(String to, String confirmationCode) {
        String subject = "Email Confirmation";

        String mailText = String.format("<h1>Thank for your registration</h1>\n" +
                "<p>To finish registration please follow the link below:\n" +
                "<a href='https://somesite.com/confirm-email?code=%s'>complete registration</a>\n" +
                "</p>", confirmationCode);

        this.emailSender.send(to, subject, mailText);
    }

    public void sendPasswordRecoveryMessage(String to, String recoveryCode) {
        String subject = "Email Password Recovery";

        String mailText = String.format("<h1>Password recovery</h1>\n" +
                "<p>To finish password recovery please follow the link below:\n" +
                "<a href='https://somesite.com/password-recovery?recoveryCode=%s'>recovery password</a>\n" +
                "</p>", recoveryCode);

        this.emailSender.send(to, subject, mailText);
    }

}
