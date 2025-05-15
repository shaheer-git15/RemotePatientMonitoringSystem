package NotificationAndReminders;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailNotification implements Notifiable {
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    
    // Get credentials from environment variables or use defaults
    private final String senderEmail = System.getenv("HEALTHCARE_EMAIL") != null ? 
        System.getenv("HEALTHCARE_EMAIL") : "shaheermuzaffarkhan@gmail.com";
    private final String senderPassword = System.getenv("HEALTHCARE_EMAIL_PASSWORD") != null ? 
        System.getenv("HEALTHCARE_EMAIL_PASSWORD") : "ocxj ghku ubhy dclx";

    @Override
    public void send(String recipient, String content) {
        if (recipient == null || recipient.trim().isEmpty()) {
            System.err.println("Error: Recipient email address is empty");
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(recipient)
            );
            message.setSubject("Healthcare Emergency Alert");
            message.setText(content);

            Transport.send(message);
            System.out.println("[EMAIL SENT] To: " + recipient);

        } catch (MessagingException e) {
            System.err.println("Failed to send email to " + recipient + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send email notification", e);
        }
    }
}
