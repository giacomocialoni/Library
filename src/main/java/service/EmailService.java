package service;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailService {

    private final String username = "giacomo.cialoni@gmail.com"; 
    private final String password = "dupuxetiertbjady"; 
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final Session session;

    public EmailService() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public void send(String to, String subject, String body) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);

        } catch (MessagingException e) {
            logger.error("Errore durante l'invio della mail destinata a: " + to, e);
        }
    }
}