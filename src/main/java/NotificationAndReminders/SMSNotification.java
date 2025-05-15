package NotificationAndReminders;

public class SMSNotification implements Notifiable {
    public void send(String recipient, String content) {
        System.out.println("[SMS SENT] To: " + recipient);
        System.out.println("Message: " + content);
    }
}