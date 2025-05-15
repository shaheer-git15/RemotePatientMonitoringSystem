package AppointmentScheduling;

import DatabaseConnector.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AppointmentDAO {

    public static void bookAppointment(int patientID, int doctorID) {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO appointments (patient_id, doctor_id, status) VALUES (?, ?, 'Pending')";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, patientID);
            ps.setInt(2, doctorID);
            ps.executeUpdate();
            System.out.println("Appointment booked successfully.");
        } catch (Exception e) {
            System.out.println("Failed to book appointment: " + e.getMessage());
        }
    }

    public static void cancelAppointment(int appointmentID) {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "DELETE FROM appointments WHERE appointment_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, appointmentID);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Appointment cancelled.");
            else System.out.println("Appointment not found.");
        } catch (Exception e) {
            System.out.println("Failed to cancel appointment: " + e.getMessage());
        }
    }

    public static void viewpatientAppointments(int patientID) {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM appointments WHERE patient_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, patientID);
            ResultSet rs = ps.executeQuery();

            System.out.println("=== Appointments for Patient ID: " + patientID + " ===");

            while (rs.next()) {
                int apptID = rs.getInt("appointment_id");
                int doctorID = rs.getInt("doctor_id");
                String date = rs.getString("date");
                System.out.println("Appointment ID: " + apptID + ", Patient ID: " + patientID +
                        ", doctor ID: " + doctorID + ", Date: " + date);
            }
        } catch (Exception e) {
            System.out.println("Failed to retrieve appointments: " + e.getMessage());
        }
    }


    public static void approveAppointment(int appointmentID, int doctorID) {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "UPDATE appointments SET status = 'Approved' WHERE appointment_id = ? AND doctor_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, appointmentID);
            ps.setInt(2, doctorID);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Appointment approved.");
            else System.out.println("Invalid appointment or doctor ID.");
        } catch (Exception e) {
            System.out.println("Failed to approve appointment: " + e.getMessage());
        }
    }

    public static void rejectAppointment(int appointmentID, int doctorID) {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "DELETE FROM appointments WHERE appointment_id = ? AND doctor_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, appointmentID);
            ps.setInt(2, doctorID);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Appointment rejected and removed.");
            else System.out.println("Appointment not found or invalid.");
        } catch (Exception e) {
            System.out.println("Failed to reject appointment: " + e.getMessage());
        }
    }

    public static void viewDoctorAppointments(int doctorID,String status) {
        try (Connection con = DBConnection.getConnection()) {

            if(status == "approved"){
                String sql = "SELECT * FROM appointments WHERE doctor_id = ? AND status = 'Approved'";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, doctorID);
                ResultSet rs = ps.executeQuery();

                System.out.println("=== Approved Appointments for Doctor ID: " + doctorID + " ===");
                boolean found = false;

                while (rs.next()) {
                    found = true;
                    int apptID = rs.getInt("appointment_id");
                    int patientID = rs.getInt("patient_id");
                    String date = rs.getString("date");
                    System.out.println("Appointment ID: " + apptID + ", Patient ID: " + patientID + ", Date: " + date);
                }

                if (!found)
                    System.out.println("No approved appointments.");
            }

            if (status == "all"){
                String sql = "SELECT * FROM appointments WHERE doctor_id = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, doctorID);
                ResultSet rs = ps.executeQuery();

                System.out.println("=== All Appointments for Doctor ID: " + doctorID + " ===");

                while (rs.next()) {
                    int apptID = rs.getInt("appointment_id");
                    int patientID = rs.getInt("patient_id");
                    String date = rs.getString("date");
                    System.out.println("Appointment ID: " + apptID + ", Patient ID: " + patientID + ", Date: " + date);
                }
            }

        } catch (Exception e) {
            System.out.println("Failed to retrieve appointments: " + e.getMessage());
        }
    }
}