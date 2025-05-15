package ChatAndVedioConsultation;

import DatabaseConnector.DBConnection;
import UserManagement.UserDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class ChatServer {

    // Save a message to the chat_messages table
    public static void logMessage(int senderID, int receiverID, String message) {
        try (Connection con = DBConnection.getConnection()) {
            // Determine if sender is doctor or patient
            String senderRole = UserDAO.getRoleById(senderID);
            String receiverRole = UserDAO.getRoleById(receiverID);
            
            if (senderRole == null || receiverRole == null) {
                System.out.println("Invalid user roles.");
                return;
            }

            int doctorId, patientId;
            String sender;

            if (senderRole.equalsIgnoreCase("Doctor")) {
                doctorId = senderID;
                patientId = receiverID;
                sender = "doctor";
            } else {
                doctorId = receiverID;
                patientId = senderID;
                sender = "patient";
            }

            String sql = "INSERT INTO chat_messages (doctor_id, patient_id, sender, message) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, doctorId);
            ps.setInt(2, patientId);
            ps.setString(3, sender);
            ps.setString(4, message);
            ps.executeUpdate();

            // Also log to chat_logs for backward compatibility
            String sql2 = "INSERT INTO chat_logs (sender_id, receiver_id, message) VALUES (?, ?, ?)";
            PreparedStatement ps2 = con.prepareStatement(sql2);
            ps2.setInt(1, senderID);
            ps2.setInt(2, receiverID);
            ps2.setString(3, message);
            ps2.executeUpdate();

        } catch (Exception e) {
            System.out.println("Failed to log chat message: " + e.getMessage());
        }
    }

    // View complete chat history between two users using chat_messages table
    public static void viewChat(int userA, int userB) {
        try (Connection con = DBConnection.getConnection()) {
            String roleA = UserDAO.getRoleById(userA);
            String roleB = UserDAO.getRoleById(userB);

            if (roleA == null || roleB == null) {
                System.out.println("Invalid user roles.");
                return;
            }

            int doctorId, patientId;
            if (roleA.equalsIgnoreCase("Doctor")) {
                doctorId = userA;
                patientId = userB;
            } else {
                doctorId = userB;
                patientId = userA;
            }

            String sql = "SELECT sender, message, timestamp FROM chat_messages " +
                    "WHERE doctor_id = ? AND patient_id = ? " +
                    "ORDER BY timestamp ASC";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, doctorId);
            ps.setInt(2, patientId);
            ResultSet rs = ps.executeQuery();

            System.out.println("--- Chat between " + userA + " and " + userB + " ---");
            boolean empty = true;

            while (rs.next()) {
                empty = false;
                String sender = rs.getString("sender");
                String msg = rs.getString("message");
                String time = rs.getString("timestamp");
                System.out.println("[" + time + "] " + sender.toUpperCase() + ": " + msg);
            }

            if (empty) {
                System.out.println("No chat history between these users.");
            }

        } catch (Exception e) {
            System.out.println("Failed to fetch chat history: " + e.getMessage());
        }
    }

    // Get recent chats for a user
    public static void getRecentChats(int userId) {
        try (Connection con = DBConnection.getConnection()) {
            String role = UserDAO.getRoleById(userId);
            
            if (role == null) {
                System.out.println("Invalid user role.");
                return;
            }

            String sql;
            if (role.equalsIgnoreCase("Doctor")) {
                sql = "SELECT DISTINCT p.name as patient_name, " +
                      "(SELECT message FROM chat_messages WHERE doctor_id = ? AND patient_id = p.user_id " +
                      "ORDER BY timestamp DESC LIMIT 1) as last_message, " +
                      "(SELECT timestamp FROM chat_messages WHERE doctor_id = ? AND patient_id = p.user_id " +
                      "ORDER BY timestamp DESC LIMIT 1) as last_timestamp " +
                      "FROM users p " +
                      "JOIN chat_messages cm ON p.user_id = cm.patient_id " +
                      "WHERE cm.doctor_id = ? " +
                      "ORDER BY last_timestamp DESC";
            } else {
                sql = "SELECT DISTINCT d.name as doctor_name, " +
                      "(SELECT message FROM chat_messages WHERE doctor_id = d.user_id AND patient_id = ? " +
                      "ORDER BY timestamp DESC LIMIT 1) as last_message, " +
                      "(SELECT timestamp FROM chat_messages WHERE doctor_id = d.user_id AND patient_id = ? " +
                      "ORDER BY timestamp DESC LIMIT 1) as last_timestamp " +
                      "FROM users d " +
                      "JOIN chat_messages cm ON d.user_id = cm.doctor_id " +
                      "WHERE cm.patient_id = ? " +
                      "ORDER BY last_timestamp DESC";
            }

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setInt(3, userId);
            ResultSet rs = ps.executeQuery();

            System.out.println("--- Recent Chats ---");
            boolean empty = true;

            while (rs.next()) {
                empty = false;
                String name = rs.getString(role.equalsIgnoreCase("Doctor") ? "patient_name" : "doctor_name");
                String lastMessage = rs.getString("last_message");
                String timestamp = rs.getString("last_timestamp");
                System.out.println("[" + timestamp + "] " + name + ": " + lastMessage);
            }

            if (empty) {
                System.out.println("No recent chats found.");
            }

        } catch (Exception e) {
            System.out.println("Failed to fetch recent chats: " + e.getMessage());
        }
    }
}