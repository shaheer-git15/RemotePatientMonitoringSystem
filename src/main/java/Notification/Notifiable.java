package Notification;

public interface Notifiable {
    void send(String recipient, String message);
}