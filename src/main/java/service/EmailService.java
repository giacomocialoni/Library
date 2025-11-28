package service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final String username;
    private final String password;
    private final Session session;

    public EmailService() {
        Properties config = loadEmailConfig();
        this.username = config.getProperty("email.username");
        this.password = config.getProperty("email.password");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    private Properties loadEmailConfig() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("email.properties")) {
            if (input != null) {
                props.load(input);
            } else {
                logger.warn("File email.properties non trovato");
            }
        } catch (Exception e) {
            logger.error("Errore nel caricamento della configurazione email", e);
        }
        return props;
    }

    public boolean send(String to, String subject, String body) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username, "Bibliotech"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            logger.info("Email inviata con successo a: {}", to);
            return true;
            
        } catch (MessagingException e) {
            logger.error("Errore durante l'invio della mail a: {}. Errore: {}", to, e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Errore generico durante l'invio della mail a: {}", to, e);
            return false;
        }
    }
}