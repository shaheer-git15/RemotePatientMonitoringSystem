package NotificationAndReminders;

import DatabaseConnector.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class NotificationDAO {

    // Log a new notification in the database
    public static void logNotification(int userId, String message) {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO notifications (user_id, message) VALUES (?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setString(2, message);
            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("Failed to log notification: " + e.getMessage());
        }
    }

    // View all notifications for a user
    public static void viewNotifications(int userId) {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY timestamp DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            System.out.println("=== Notifications for User ID: " + userId + " ===");
            boolean hasNotifications = false;

            while (rs.next()) {
                hasNotifications = true;
                String message = rs.getString("message");
                String timestamp = rs.getString("timestamp");
                System.out.println("[" + timestamp + "] " + message);
            }

            if (!hasNotifications) {
                System.out.println("No notifications found.");
            }

        } catch (Exception e) {
            System.out.println("Failed to fetch notifications: " + e.getMessage());
        }
    }
}