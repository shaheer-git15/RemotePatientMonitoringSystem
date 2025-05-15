package DatabaseConnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DBConnection {
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/rpms";
    private static final String USER = "root";
    private static final String PASS = "Shaheerkhan@2004";
    
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Attempt to establish connection
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Please add the driver to your classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed! Check your connection details.");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }


    public static class Notification {
        private final String patientName;
        private final String type;
        private final String timestamp;

        public Notification(String patientName, String type, String timestamp) {
            this.patientName = patientName;
            this.type = type;
            this.timestamp = timestamp;
        }

    }

    // Chat message class for chat persistence
    public static class ChatMessage {
        private final String sender; // "doctor" or "patient"
        private final String message;
        private final String timestamp;

        public ChatMessage(String sender, String message, String timestamp) {
            this.sender = sender;
            this.message = message;
            this.timestamp = timestamp;
        }

        public String getSender() { return sender; }
        public String getMessage() { return message; }
        public String getTimestamp() { return timestamp; }
    }

    // Fetch all chat messages between doctor and patient
    public static ObservableList<ChatMessage> fetchChatMessages(int doctorId, int patientId) {
        ObservableList<ChatMessage> messages = FXCollections.observableArrayList();
        String sql = "SELECT sender, message, timestamp FROM chat_messages WHERE doctor_id = ? AND patient_id = ? ORDER BY timestamp ASC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setInt(2, patientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                messages.add(new ChatMessage(
                    rs.getString("sender"),
                    rs.getString("message"),
                    rs.getString("timestamp")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching chat messages: " + e.getMessage());
            e.printStackTrace();
        }
        return messages;
    }

    // Insert a new chat message
    public static void insertChatMessage(int doctorId, int patientId, String sender, String message, String timestamp) {
        String sql = "INSERT INTO chat_messages (doctor_id, patient_id, sender, message, timestamp) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setInt(2, patientId);
            ps.setString(3, sender);
            ps.setString(4, message);
            ps.setString(5, timestamp);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error inserting chat message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}