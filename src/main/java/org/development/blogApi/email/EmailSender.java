package org.development.blogApi.email;

public interface EmailSender {
    void send(String to, String subject, String mailText);
}
