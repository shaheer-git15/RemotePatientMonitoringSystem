package Notification;

import DatabaseConnector.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class NotificationDAO {
    public static void logNotification(int userId, String content) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO notifications (user_id, content) VALUES (?, ?)")) {

            ps.setInt(1, userId);
            ps.setString(2, content);
            ps.executeUpdate();

        } catch (Exception e) {
            System.err.println("Failed to log notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
}