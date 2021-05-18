package com.vinylteam.vinyl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailSender {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private String username;
    private Session session;

    public MailSender(String username, String password, String host, String port, String auth) {
        this.username = username;

        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", auth);
        properties.put("mail.smtp.socketFactory.port", port);
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        session = Session.getInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
    }

    public boolean sendMail(String recipient, String subject, String mailBody) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(recipient)
            );
            message.setSubject(subject);
            message.setText(mailBody);

            Transport.send(message);
            logger.info("Email sent successfully for recipient : {}", recipient);

        } catch (MessagingException e) {
            logger.info("Can't send email to recipient : {}  due to error : {}", recipient, e.toString());
            return false;
        }
        return true;
    }
}