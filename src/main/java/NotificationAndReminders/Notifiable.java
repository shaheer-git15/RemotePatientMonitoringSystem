package NotificationAndReminders;

public interface Notifiable {
    void send(String recipient, String content);
}
