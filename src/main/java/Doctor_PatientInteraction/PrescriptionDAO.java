package Doctor_PatientInteraction;

import DatabaseConnector.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionDAO {

    // Store a new prescription
    public static void insertPrescription(int patientId, int doctorId, String medicines, String schedules, String feedback) {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO prescriptions (patient_id, doctor_id, medicines, schedules, feedback) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ps.setString(3, medicines);
            ps.setString(4, schedules);
            ps.setString(5, feedback);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Failed to insert prescription: " + e.getMessage());
        }
    }

    // Get the latest prescription for report
    public static String getPrescriptionText(int patientId) {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM prescriptions WHERE patient_id = ? ORDER BY timestamp DESC LIMIT 1";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return formatPrescription(rs);
            }

        } catch (Exception e) {
            System.out.println("Error fetching prescription: " + e.getMessage());
        }
        return "No prescriptions found.";
    }

    // Get latest prescription from a specific doctor
    public static String getPrescriptionByDoctor(int patientId, int doctorId) {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM prescriptions WHERE patient_id = ? AND doctor_id = ? ORDER BY timestamp DESC LIMIT 1";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return formatPrescription(rs);
            }

        } catch (Exception e) {
            System.out.println("Error fetching prescription by doctor: " + e.getMessage());
        }

        return "No prescriptions from this doctor for this patient.";
    }

    // Get full prescription history for a patient
    public static List<String> getAllPrescriptionsForPatient(int patientId) {
        List<String> history = new ArrayList<>();

        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM prescriptions WHERE patient_id = ? ORDER BY timestamp DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                history.add(formatPrescription(rs));
            }

        } catch (Exception e) {
            System.out.println("Error loading prescription history: " + e.getMessage());
        }

        return history;
    }

    // Helper to format any prescription
    private static String formatPrescription(ResultSet rs) throws Exception {
        return "\n--- Prescription ---" +
                "\nDoctor ID   : " + rs.getInt("doctor_id") +
                "\nPatient ID  : " + rs.getInt("patient_id") +
                "\nMedicines   : " + rs.getString("medicines") +
                "\nSchedules   : " + rs.getString("schedules") +
                "\nFeedback    : " + rs.getString("feedback") +
                "\nTimestamp   : " + rs.getTimestamp("timestamp");
    }
}