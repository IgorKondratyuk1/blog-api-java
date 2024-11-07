package org.development.blogApi.infrastructure.email;

public interface EmailSender {
    void send(String to, String subject, String mailText);
}
