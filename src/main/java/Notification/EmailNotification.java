package Notification;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailNotification implements Notifiable {
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_USERNAME = "your-email@gmail.com"; // Replace with your email
    private static final String EMAIL_PASSWORD = "your-app-password"; // Replace with your app password

    @Override
    public void send(String recipient, String message) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(EMAIL_USERNAME));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            msg.setSubject("Emergency Alert - Remote Patient Monitoring System");
            msg.setText(message);

            Transport.send(msg);
            System.out.println("Email notification sent successfully to: " + recipient);
        } catch (MessagingException e) {
            System.err.println("Failed to send email notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
}