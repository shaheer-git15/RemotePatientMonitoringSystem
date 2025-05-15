package UserManagement;

import DatabaseConnector.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public static void insertUser(User user, String role) {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO users (user_id, name, age, gender, address, email, password, role) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, user.getUserID());
            ps.setString(2, user.getName());
            ps.setInt(3, user.getAge());
            ps.setString(4, user.getGender());
            ps.setString(5, user.getAddress());
            ps.setString(6, user.getEmail());
            ps.setString(7, user.getPassword());
            ps.setString(8, role);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Failed to insert user: " + e.getMessage());
        }
    }

    public static User getUser(int userId, String password, String role) {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE user_id = ? AND password = ? AND role = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setString(2, password);
            ps.setString(3, role);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return extractUserFromResult(rs);

        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
        }
        return null;
    }

    public static String getRoleById(int userId) {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT role FROM users WHERE user_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return rs.getString("role");

        } catch (Exception e) {
            System.out.println("Failed to fetch role: " + e.getMessage());
        }
        return "";
    }

    public static User getUserById(int userId) {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE user_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return extractUserFromResult(rs);

        } catch (Exception e) {
            System.out.println("Failed to fetch user: " + e.getMessage());
        }
        return null;
    }

    public static List<User> getUsersByRole(String role) {
        List<User> users = new ArrayList<>();
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE role = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, role);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                users.add(extractUserFromResult(rs));
            }

        } catch (Exception e) {
            System.out.println("Error fetching users by role: " + e.getMessage());
        }
        return users;
    }

    private static User extractUserFromResult(ResultSet rs) throws Exception {
        int id = rs.getInt("user_id");
        String name = rs.getString("name");
        int age = rs.getInt("age");
        String gender = rs.getString("gender");
        String address = rs.getString("address");
        String email = rs.getString("email");
        String password = rs.getString("password");

        return new User(id, name, age, gender, address, email, password);
    }
}