package HealthDataHandling;

import DatabaseConnector.DBConnection;
import EmergencyAlertSystem.EmergencyMonitor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class VitalsDAO {

    // Insert or update vitals and check for emergency
    public static void insertVitals(VitalSign vitals) {
        try (Connection con = DBConnection.getConnection()) {
            // First check if vitals exist for this patient
            String checkVitals = "SELECT COUNT(*) FROM vitals WHERE patient_id = ?";
            PreparedStatement checkPs = con.prepareStatement(checkVitals);
            checkPs.setInt(1, vitals.getPatientId());
            ResultSet rs = checkPs.executeQuery();
            
            boolean vitalsExist = false;
            if (rs.next()) {
                vitalsExist = rs.getInt(1) > 0;
            }

            if (vitalsExist) {
                // Update existing vitals
                String updateVitals = "UPDATE vitals SET heart_rate = ?, oxygen_level = ?, " +
                        "blood_pressure = ?, temperature = ?, timestamp = NOW() WHERE patient_id = ?";
                PreparedStatement ps = con.prepareStatement(updateVitals);
                ps.setDouble(1, vitals.getHeartRate());
                ps.setDouble(2, vitals.getOxygenLevel());
                ps.setString(3, vitals.getBloodPressure());
                ps.setDouble(4, vitals.getTemperature());
                ps.setInt(5, vitals.getPatientId());
                ps.executeUpdate();
            } else {
                // Insert new vitals
                String insertVitals = "INSERT INTO vitals (patient_id, heart_rate, oxygen_level, " +
                        "blood_pressure, temperature, timestamp) VALUES (?, ?, ?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(insertVitals);
                ps.setInt(1, vitals.getPatientId());
                ps.setDouble(2, vitals.getHeartRate());
                ps.setDouble(3, vitals.getOxygenLevel());
                ps.setString(4, vitals.getBloodPressure());
                ps.setDouble(5, vitals.getTemperature());
                ps.executeUpdate();
            }

            // Always insert into history
            String insertHistory = "INSERT INTO vitals_history (patient_id, heart_rate, oxygen_level, " +
                    "blood_pressure, temperature, timestamp) VALUES (?, ?, ?, ?, ?, NOW())";
            PreparedStatement ps2 = con.prepareStatement(insertHistory);
            ps2.setInt(1, vitals.getPatientId());
            ps2.setDouble(2, vitals.getHeartRate());
            ps2.setDouble(3, vitals.getOxygenLevel());
            ps2.setString(4, vitals.getBloodPressure());
            ps2.setDouble(5, vitals.getTemperature());
            ps2.executeUpdate();

            EmergencyMonitor.checkVitals(vitals.getPatientId()); // trigger optimized check

        } catch (Exception e) {
            System.out.println("Error inserting/updating vitals: " + e.getMessage());
        }
    }

    // Get latest vitals as object
    public static VitalSign getVitals(int patientId) {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM vitals WHERE patient_id = ? ORDER BY timestamp DESC LIMIT 1";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                VitalSign v = new VitalSign(patientId);
                v.setHeartRate(rs.getDouble("heart_rate"));
                v.setOxygenLevel(rs.getDouble("oxygen_level"));
                v.setBloodPressure(rs.getString("blood_pressure"));
                v.setTemperature(rs.getDouble("temperature"));
                return v;
            }
        } catch (Exception e) {
            System.out.println("Error retrieving vitals: " + e.getMessage());
        }
        return null;
    }

    // Get vitals as formatted string
    public static String getVitalsAsString(int patientId) {
        VitalSign v = getVitals(patientId);
        return (v == null) ?
                "No vitals found." : v.toString();
    }

    // Import from CSV call this in VitalsCSVReader
    public static void importVitalsFromCSV(String filePath) {
        VitalsCSVReader.importVitalsFromCSV(filePath); // make sure that reader calls insertVitals()
    }
}