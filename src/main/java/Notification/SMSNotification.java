package Notification;

public class SMSNotification implements Notifiable {

    @Override
    public void send(String recipient, String message) {
        // In a real implementation, this would make an API call to an SMS service
        System.out.println("SMS Notification (Simulated):");
        System.out.println("To: " + recipient);
        System.out.println("Message: " + message);

    }
}