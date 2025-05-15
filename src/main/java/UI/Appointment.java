package UI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import DatabaseConnector.DBConnection;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.control.TableView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import java.io.File;

public class Appointment {
    private int appointmentId;
    private String doctor;
    private LocalDateTime dateTime;
    private String status;
    private String comments;
    private String patient;
    private LocalDateTime createdAt;
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Appointment(int appointmentId, String doctor, LocalDateTime dateTime, String status, String comments, String patient) {
        this.appointmentId = appointmentId;
        this.doctor = doctor;
        this.dateTime = dateTime;
        this.status = status;
        this.comments = comments;
        this.patient = patient;
    }

    public Appointment(int appointmentId, String date, String time, String status, String comments, String doctor, String patient, LocalDateTime createdAt) {
        this.appointmentId = appointmentId;
        this.doctor = doctor;
        this.dateTime = LocalDateTime.parse(date + " " + time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.status = status;
        this.comments = comments;
        this.patient = patient;
        this.createdAt = createdAt;
    }

    public int getAppointmentId() { return appointmentId; }

    public String getDoctor() {
        return doctor;
    }

    public String getDate() {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public String getTime() {
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getStatus() {
        return status;
    }

    public String getComments() {
        return comments;
    }

    public String getPatient() {
        return patient;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private void insertAppointmentToDB(int patientId, int doctorId, LocalDateTime dateTime, String status, String comments) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO appointments (patient_id, doctor_id, date, status, comments) VALUES (?, ?, ?, ?, ?)"
             )) {
            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ps.setObject(3, dateTime);
            ps.setString(4, status);
            ps.setString(5, comments);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshAppointments(ObservableList<Appointment> appointments, TableView<Appointment> appointmentsTable) {
        appointmentsTable.setItems(appointments);
        appointmentsTable.refresh();
    }

    private void updateAppointmentStatusInDB(int appointmentId, String status) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "UPDATE appointments SET status = ? WHERE appointment_id = ?"
             )) {
            ps.setString(1, status);
            ps.setInt(2, appointmentId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 