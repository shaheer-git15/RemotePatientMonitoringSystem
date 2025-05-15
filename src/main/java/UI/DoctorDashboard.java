package UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import DatabaseConnector.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.input.KeyCode;

public class DoctorDashboard {
    private final int doctorId;
    private final Stage stage;
    private final BorderPane root;
    private final VBox mainContent;
    private final String doctorName;

    public DoctorDashboard(Stage stage, int doctorId) {
        this.stage = stage;
        this.doctorId = doctorId;
        this.root = new BorderPane();
        this.mainContent = new VBox(20);
        this.mainContent.setPadding(new Insets(30, 10, 30, 30));
        this.mainContent.setStyle("-fx-background-color: #f7fafc;");
        this.doctorName = fetchDoctorNameFromDB(doctorId);
        setupUI();
    }

    private void setupUI() {
        // Sidebar with main options
        BorderPane sidebar = new BorderPane();
        VBox navButtons = new VBox(10);
        navButtons.setAlignment(Pos.TOP_LEFT);
        navButtons.setPadding(new Insets(20, 18, 20, 18));
        navButtons.setStyle("");
        sidebar.setStyle("-fx-background-color: #2c3e50; -fx-border-color: #e0e0e0; -fx-border-width: 0 2 0 0; -fx-effect: dropshadow(gaussian, #b2dfdb, 12, 0.2, 2, 0); -fx-background-radius: 18px; -fx-min-width: 270px;");

        // Welcome message at the top of the sidebar
        Label welcomeLabel = new Label("Welcome, Dr. " + doctorName + "!");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #fff; -fx-padding: 0 0 12 0;");
        welcomeLabel.setAlignment(Pos.CENTER);
        welcomeLabel.setMaxWidth(Double.MAX_VALUE);

        Button btnDashboard = createSidebarButton("Dashboard");
        Button btnViewVitals = createSidebarButton("View Vitals");
        Button btnAllAppointments = createSidebarButton("All Appointments");
        Button btnApprovedAppointments = createSidebarButton("Approved Appointments");
        Button btnPrescribe = createSidebarButton("Prescribe/Feedback");
        Button btnMedicalHistory = createSidebarButton("Medical History");
        Button btnConversation = createSidebarButton("Conversation");
        Button btnEmergencyAlerts = createSidebarButton("Emergency Alerts");

        navButtons.getChildren().setAll(
            welcomeLabel, btnDashboard, btnViewVitals, btnAllAppointments, btnApprovedAppointments, btnPrescribe, btnMedicalHistory, btnConversation, btnEmergencyAlerts, new Separator()
        );

        // Logout button (styled simply and red, like a sidebar button)
        Button btnLogout = new Button("Logout");
        btnLogout.setStyle("-fx-background-color: #e53935; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-font-size: 14px; -fx-padding: 8 18;");
        btnLogout.setOnMouseEntered(e -> btnLogout.setStyle("-fx-background-color: #b71c1c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-font-size: 14px; -fx-padding: 8 18;"));
        btnLogout.setOnMouseExited(e -> btnLogout.setStyle("-fx-background-color: #e53935; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-font-size: 14px; -fx-padding: 8 18;"));
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setMinWidth(140);
        btnLogout.setPrefHeight(44);
        btnLogout.setAlignment(Pos.CENTER);
        VBox.setMargin(btnLogout, new Insets(0, 0, 18, 0));

        Separator logoutSeparator = new Separator();
        logoutSeparator.setStyle("-fx-background-color: #e0e0e0;");
        VBox logoutBox = new VBox(10, logoutSeparator, btnLogout);
        logoutBox.setAlignment(Pos.CENTER);
        logoutBox.setPadding(new Insets(0, 0, 18, 0));

        sidebar.setCenter(navButtons);
        sidebar.setBottom(logoutBox);
        sidebar.setPrefHeight(Double.MAX_VALUE);

        root.setLeft(sidebar);
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 1100, 700);
        stage.setScene(scene);
        stage.setTitle("Doctor Dashboard");
        stage.setMaximized(true);
        stage.show();
        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            sidebar.setMinHeight((double) newVal);
            sidebar.setMaxHeight((double) newVal);
        });

        // Button actions
        btnDashboard.setOnAction(e -> showSection("Dashboard"));
        btnViewVitals.setOnAction(e -> showSection("View Vitals"));
        btnAllAppointments.setOnAction(e -> showSection("All Appointments"));
        btnApprovedAppointments.setOnAction(e -> showSection("Approved Appointments"));
        btnPrescribe.setOnAction(e -> showSection("Prescribe/Feedback"));
        btnMedicalHistory.setOnAction(e -> showSection("Medical History"));
        btnConversation.setOnAction(e -> showSection("Conversation"));
        btnEmergencyAlerts.setOnAction(e -> showSection("Emergency Alerts"));
        btnLogout.setOnAction(e -> new LoginPage().start(stage));

        showSection("Dashboard");
    }

    private Button createSidebarButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 18;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 18;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 18;"));
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    private String fetchDoctorNameFromDB(int doctorId) {
        String name = "Doctor " + doctorId;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT name FROM users WHERE user_id = ?")) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                name = rs.getString("name");
            }
        } catch (Exception e) {
            System.out.println("Error fetching doctor name: " + e.getMessage());
        }
        return name;
    }

    private void showSection(String section) {
        mainContent.getChildren().clear();
        if (section.equals("Dashboard")) {
            GridPane grid = new GridPane();
            grid.setHgap(32);
            grid.setVgap(32);
            grid.setAlignment(Pos.CENTER);
            grid.setPadding(new Insets(32, 32, 32, 0));

            // Fetch counts for dashboard
            int chatCount = getChatCount();
            int approvedAppointmentCount = getAppointmentCountByStatus("Approved");
            int completedAppointmentCount = getAppointmentCountByStatus("Completed");
            int activeEmergencyCount = getActiveEmergencyCount();

            VBox boxChats = createSummaryBox("Chats", String.valueOf(chatCount), "View", "#43a047", () -> showSection("Chat"), "\uD83D\uDCAC");
            VBox boxAppointments = createSummaryBox("All Appointments", String.valueOf(approvedAppointmentCount), "View", "#fbc02d", () -> showSection("All Appointments"), "\uD83D\uDCC5");
            VBox boxVitals = createSummaryBox("View Vitals", "", "View", "#8e24aa", () -> showSection("View Vitals"), "\uD83D\uDC89");
            VBox boxCompleted = createSummaryBox("Completed Appointments", String.valueOf(completedAppointmentCount), "View", "#00bcd4", () -> showSection("Approved Appointments"), "\u2714");
            VBox boxPrescribe = createSummaryBox("Prescribe/Feedback", "", "Go", "#ff7043", () -> showSection("Prescribe/Feedback"), "\uD83D\uDCDD");
            VBox boxEmergency = createSummaryBox("Emergency Alerts", String.valueOf(activeEmergencyCount), "View", "#e53935", () -> showSection("Emergency Alerts"), "⚠️");

            grid.add(boxChats, 0, 0);
            grid.add(boxAppointments, 1, 0);
            grid.add(boxVitals, 2, 0);
            grid.add(boxCompleted, 0, 1);
            grid.add(boxPrescribe, 1, 1);
            grid.add(boxEmergency, 2, 1);

            mainContent.getChildren().add(grid);
            VBox.setVgrow(grid, Priority.ALWAYS);
            return;
        }
        if (section.equals("All Appointments")) {
            showAllAppointments();
            return;
        }
        if (section.equals("View Vitals")) {
            showDoctorPatientVitals();
            return;
        }
        if (section.equals("Prescribe/Feedback")) {
            showPrescribeFeedback();
            return;
        }
        if (section.equals("Approved Appointments")) {
            showApprovedAppointments();
            return;
        }
        if (section.equals("Medical History")) {
            showMedicalHistory();
            return;
        }
        if (section.equals("Conversation")) {
            showConversation();
            return;
        }
        if (section.equals("Emergency Alerts")) {
            showEmergencyAlertsSection();
            return;
        }
        Label label = new Label();
        label.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1976d2; -fx-padding: 0 0 18 0;");
        switch (section) {
            case "Recent Chats" -> label.setText("Recent Chats (Placeholder)");
            default -> label.setText(section + " (Placeholder)");
        }
        mainContent.getChildren().add(label);
    }

    private void showAllAppointments() {
        mainContent.getChildren().clear();
        Label title = new Label("All Appointments");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #0097a7; -fx-padding: 0 0 18 0;");

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "Pending", "Approved", "Cancelled", "Completed");
        statusFilter.setValue("All");
        statusFilter.setPrefWidth(150);

        TableColumn<Appointment, String> patientCol = new TableColumn<>("Patient");
        patientCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPatient()));
        patientCol.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
        patientCol.setPrefWidth(120);

        TableColumn<Appointment, String> doctorCol = new TableColumn<>("Doctor");
        doctorCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDoctor()));
        doctorCol.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
        doctorCol.setPrefWidth(120);

        TableColumn<Appointment, String> dateCol = new TableColumn<>("Date & Time");
        dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDate()));
        dateCol.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
        dateCol.setPrefWidth(180);

        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    String textColor;
                    switch (status.toLowerCase()) {
                        case "cancelled" -> textColor = "#d32f2f";
                        case "approved" -> textColor = "#2e7d32";
                        case "pending" -> textColor = "#f57f17";
                        case "completed" -> textColor = "#1976d2";
                        default -> textColor = "#000000";
                    }
                    setStyle("-fx-text-fill: " + textColor + "; -fx-font-weight: bold; -fx-alignment: CENTER;");
                }
            }
        });
        statusCol.setStyle("-fx-font-weight: bold;");
        statusCol.setPrefWidth(90);

        TableColumn<Appointment, String> commentsCol = new TableColumn<>("Comments");
        commentsCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getComments()));
        commentsCol.setStyle("-fx-alignment: CENTER-LEFT; -fx-font-weight: bold;");
        commentsCol.setPrefWidth(200);

        TableView<Appointment> appointmentsTable = new TableView<>();
        appointmentsTable.getColumns().setAll(patientCol, doctorCol, dateCol, statusCol, commentsCol);

        // Color the entire row based on status
        appointmentsTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Appointment appt, boolean empty) {
                super.updateItem(appt, empty);
                if (appt == null || empty) {
                    setStyle("");
                } else {
                    String bgColor;
                    switch (appt.getStatus().toLowerCase()) {
                        case "cancelled" -> bgColor = "#ffcdd2";
                        case "approved" -> bgColor = "#c8e6c9";
                        case "pending" -> bgColor = "#fff9c4";
                        case "completed" -> bgColor = "#b3e5fc";
                        default -> bgColor = "#ffffff";
                    }
                    setStyle("-fx-background-color: " + bgColor + ";");
                }
            }
        });

        ObservableList<Appointment> appointments = fetchAppointmentsForDoctor(doctorId);
        appointmentsTable.setItems(appointments);

        statusFilter.setOnAction(e -> {
            String selectedStatus = statusFilter.getValue();
            ObservableList<Appointment> filtered;
            if ("All".equals(selectedStatus)) {
                filtered = fetchAppointmentsForDoctor(doctorId);
            } else {
                filtered = fetchAppointmentsForDoctorByStatus(doctorId, selectedStatus);
            }
            appointmentsTable.setItems(filtered);
            appointmentsTable.refresh();
        });

        ScrollPane tableScrollPane = new ScrollPane(appointmentsTable);
        tableScrollPane.setFitToWidth(true);
        tableScrollPane.setFitToHeight(true);
        tableScrollPane.setPannable(true);
        tableScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        tableScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        tableScrollPane.setPrefWidth(1180);
        tableScrollPane.setPrefHeight(580);
        tableScrollPane.setMaxWidth(Double.MAX_VALUE);
        tableScrollPane.setMaxHeight(Double.MAX_VALUE);

        VBox appointmentsBox = new VBox(10, statusFilter, tableScrollPane);
        appointmentsBox.setPadding(new Insets(0));
        appointmentsBox.setStyle("-fx-background-color: #e3f6fd; -fx-border-color: #00bcd4; -fx-border-width: 2px; -fx-border-style: solid; -fx-border-radius: 0;");
        appointmentsBox.setPrefWidth(1200);
        appointmentsBox.setPrefHeight(600);
        appointmentsBox.setMaxWidth(Double.MAX_VALUE);
        appointmentsBox.setMaxHeight(Double.MAX_VALUE);

        mainContent.getChildren().setAll(title, appointmentsBox);
    }

    private ObservableList<Appointment> fetchAppointmentsForDoctor(int doctorId) {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT a.appointment_id, a.date, a.status, a.comments, u.name as doctor_name, p.name as patient_name " +
                 "FROM appointments a " +
                 "JOIN users u ON a.doctor_id = u.user_id " +
                 "JOIN users p ON a.patient_id = p.user_id " +
                 "WHERE a.doctor_id = ? " +
                 "ORDER BY a.date DESC"
             )) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                try {
                    String date = rs.getString("date"); // DATETIME
                    LocalDateTime dateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    appointments.add(new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getString("doctor_name"),
                        dateTime,
                        rs.getString("status"),
                        rs.getString("comments"),
                        rs.getString("patient_name")
                    ));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("Error parsing appointment row: " + rs.getString("date"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appointments;
    }

    private ObservableList<Appointment> fetchAppointmentsForDoctorByStatus(int doctorId, String status) {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT a.appointment_id, a.date, a.status, a.comments, u.name as doctor_name, p.name as patient_name " +
                 "FROM appointments a " +
                 "JOIN users u ON a.doctor_id = u.user_id " +
                 "JOIN users p ON a.patient_id = p.user_id " +
                 "WHERE a.doctor_id = ? AND a.status = ? " +
                 "ORDER BY a.date DESC"
             )) {
            ps.setInt(1, doctorId);
            ps.setString(2, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                try {
                    String date = rs.getString("date"); // DATETIME
                    LocalDateTime dateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    appointments.add(new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getString("doctor_name"),
                        dateTime,
                        rs.getString("status"),
                        rs.getString("comments"),
                        rs.getString("patient_name")
                    ));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("Error parsing appointment row: " + rs.getString("date"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appointments;
    }

    private void showDoctorPatientVitals() {
        mainContent.getChildren().clear();
        Label title = new Label("Patient Vitals (Approved Appointments)");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #0097a7; -fx-padding: 0 0 18 0;");

        TableColumn<Appointment, String> patientCol = new TableColumn<>("Patient");
        patientCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPatient()));
        patientCol.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
        patientCol.setPrefWidth(160);

        TableColumn<Appointment, String> dateCol = new TableColumn<>("Date & Time");
        dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDate()));
        dateCol.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
        dateCol.setPrefWidth(180);

        TableView<Appointment> patientsTable = new TableView<>();
        patientsTable.getColumns().setAll(patientCol, dateCol);
        ObservableList<Appointment> approvedAppointments = fetchAppointmentsForDoctorByStatus(doctorId, "Approved");
        patientsTable.setItems(approvedAppointments);
        patientsTable.setPlaceholder(new Label("No approved appointments found."));

        VBox vitalsDetailBox = new VBox(18);
        vitalsDetailBox.setPadding(new Insets(24));
        vitalsDetailBox.setStyle("-fx-background-color: #f7fafc; -fx-background-radius: 18px; -fx-effect: dropshadow(gaussian, #b2dfdb, 8, 0.10, 0, 1);");
        vitalsDetailBox.setMinWidth(400);
        vitalsDetailBox.setAlignment(Pos.TOP_LEFT);
        Label selectPrompt = new Label("Select a patient to view/edit vitals.");
        selectPrompt.setStyle("-fx-font-size: 18px; -fx-text-fill: #1976d2; -fx-font-weight: bold;");

        // Vitals fields
        Label bpLabel = new Label("Blood Pressure (mmHg):");
        TextField bpField = new TextField();
        bpField.setPromptText("e.g., 120/80");
        bpField.setDisable(true);

        Label heartRateLabel = new Label("Heart Rate (bpm):");
        TextField heartRateField = new TextField();
        heartRateField.setPromptText("e.g., 75");
        heartRateField.setDisable(true);

        Label tempLabel = new Label("Temperature (°F):");
        TextField tempField = new TextField();
        tempField.setPromptText("e.g., 98.6");
        tempField.setDisable(true);

        Label spo2Label = new Label("SpO2 (%):");
        TextField spo2Field = new TextField();
        spo2Field.setPromptText("e.g., 98");
        spo2Field.setDisable(true);

        Label weightLabel = new Label("Weight (kg):");
        TextField weightField = new TextField();
        weightField.setPromptText("e.g., 70");
        weightField.setDisable(true);

        GridPane vitalsGrid = new GridPane();
        vitalsGrid.setHgap(10);
        vitalsGrid.setVgap(10);
        vitalsGrid.setPadding(new Insets(10));
        vitalsGrid.add(bpLabel, 0, 0);
        vitalsGrid.add(bpField, 1, 0);
        vitalsGrid.add(heartRateLabel, 0, 1);
        vitalsGrid.add(heartRateField, 1, 1);
        vitalsGrid.add(tempLabel, 0, 2);
        vitalsGrid.add(tempField, 1, 2);
        vitalsGrid.add(spo2Label, 0, 3);
        vitalsGrid.add(spo2Field, 1, 3);
        vitalsGrid.add(weightLabel, 0, 4);
        vitalsGrid.add(weightField, 1, 4);

        Button editButton = new Button("Edit Vitals");
        editButton.setStyle("-fx-background-color: #0097a7; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 16;");
        editButton.setDisable(true);

        Button saveButton = new Button("Save Changes");
        saveButton.setStyle("-fx-background-color: #43a047; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 16;");
        saveButton.setDisable(true);

        HBox buttonBox = new HBox(10, editButton, saveButton);
        buttonBox.setAlignment(Pos.CENTER);

        vitalsDetailBox.getChildren().setAll(selectPrompt, vitalsGrid, buttonBox);

        // Track selected patient
        final int[] selectedPatientId = { -1 };
        final boolean[] hasVitals = { false };

        patientsTable.setRowFactory(tv -> {
            TableRow<Appointment> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    Appointment appt = row.getItem();
                    selectPrompt.setText("Editing vitals for: " + appt.getPatient());
                    selectedPatientId[0] = getPatientIdByName(appt.getPatient());
                    // Try to load vitals
                    try (Connection conn = DBConnection.getConnection();
                         PreparedStatement ps = conn.prepareStatement(
                            "SELECT blood_pressure, heart_rate, temperature, oxygen_level, weight FROM vitals WHERE patient_id = ? ORDER BY timestamp DESC LIMIT 1")) {
                        ps.setInt(1, selectedPatientId[0]);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            bpField.setText(rs.getString("blood_pressure"));
                            heartRateField.setText(rs.getString("heart_rate"));
                            tempField.setText(rs.getString("temperature"));
                            spo2Field.setText(rs.getString("oxygen_level"));
                            weightField.setText(rs.getString("weight"));
                            hasVitals[0] = true;
                        } else {
                            bpField.clear();
                            heartRateField.clear();
                            tempField.clear();
                            spo2Field.clear();
                            weightField.clear();
                            hasVitals[0] = false;
                        }
                    } catch (Exception e) {
                        bpField.clear();
                        heartRateField.clear();
                        tempField.clear();
                        spo2Field.clear();
                        weightField.clear();
                        hasVitals[0] = false;
                    }
                    editButton.setDisable(false);
                    saveButton.setDisable(true);
                    bpField.setDisable(true);
                    heartRateField.setDisable(true);
                    tempField.setDisable(true);
                    spo2Field.setDisable(true);
                    weightField.setDisable(true);
                }
            });
            return row;
        });

        editButton.setOnAction(e -> {
            bpField.setDisable(false);
            heartRateField.setDisable(false);
            tempField.setDisable(false);
            spo2Field.setDisable(false);
            weightField.setDisable(false);
            saveButton.setDisable(false);
            editButton.setDisable(true);
        });

        saveButton.setOnAction(e -> {
            if (selectedPatientId[0] == -1) return;
            // Validate
            if (!validateVitals(bpField.getText(), heartRateField.getText(), tempField.getText(), spo2Field.getText(), weightField.getText())) return;
            try (Connection conn = DBConnection.getConnection()) {
                if (hasVitals[0]) {
                    // Update latest vitals (optional: you may want to always insert new row for history)
                    try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE vitals SET blood_pressure=?, heart_rate=?, temperature=?, oxygen_level=?, weight=?, timestamp=NOW() WHERE patient_id=? ORDER BY timestamp DESC LIMIT 1")) {
                        ps.setString(1, bpField.getText());
                        ps.setString(2, heartRateField.getText());
                        ps.setString(3, tempField.getText());
                        ps.setString(4, spo2Field.getText());
                        ps.setString(5, weightField.getText());
                        ps.setInt(6, selectedPatientId[0]);
                        ps.executeUpdate();
                    }
                } else {
                    // Insert new vitals
                    try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO vitals (patient_id, blood_pressure, heart_rate, temperature, oxygen_level, weight, timestamp) VALUES (?, ?, ?, ?, ?, ?, NOW())")) {
                        ps.setInt(1, selectedPatientId[0]);
                        ps.setString(2, bpField.getText());
                        ps.setString(3, heartRateField.getText());
                        ps.setString(4, tempField.getText());
                        ps.setString(5, spo2Field.getText());
                        ps.setString(6, weightField.getText());
                        ps.executeUpdate();
                    }
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Vitals saved successfully!");
                alert.showAndWait();
                saveButton.setDisable(true);
                editButton.setDisable(false);
                bpField.setDisable(true);
                heartRateField.setDisable(true);
                tempField.setDisable(true);
                spo2Field.setDisable(true);
                weightField.setDisable(true);
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to save vitals: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        HBox hbox = new HBox(32, patientsTable, vitalsDetailBox);
        hbox.setAlignment(Pos.TOP_LEFT);
        hbox.setPadding(new Insets(18, 0, 0, 0));
        mainContent.getChildren().setAll(title, hbox);
    }

    private boolean validateVitals(String bp, String heartRate, String temp, String spo2, String weight) {
        // Validate Blood Pressure
        if (!bp.matches("\\d+/\\d+")) {
            showError("Invalid blood pressure format. Use format: systolic/diastolic (e.g., 120/80)");
            return false;
        }

        // Validate Heart Rate
        if (!heartRate.matches("\\d+")) {
            showError("Heart rate must be a number");
            return false;
        }
        int hr = Integer.parseInt(heartRate);
        if (hr < 40 || hr > 200) {
            showError("Heart rate must be between 40 and 200 bpm");
            return false;
        }

        // Validate Temperature
        if (!temp.matches("\\d+\\.?\\d*")) {
            showError("Temperature must be a number");
            return false;
        }
        double tempValue = Double.parseDouble(temp);
        if (tempValue < 95 || tempValue > 105) {
            showError("Temperature must be between 95°F and 105°F");
            return false;
        }

        // Validate SpO2
        if (!spo2.matches("\\d+")) {
            showError("SpO2 must be a number");
            return false;
        }
        int spo2Value = Integer.parseInt(spo2);
        if (spo2Value < 70 || spo2Value > 100) {
            showError("SpO2 must be between 70% and 100%");
            return false;
        }

        // Validate Weight
        if (!weight.matches("\\d+\\.?\\d*")) {
            showError("Weight must be a number");
            return false;
        }
        double weightValue = Double.parseDouble(weight);
        if (weightValue < 20 || weightValue > 300) {
            showError("Weight must be between 20 and 300 kg");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadPatientVitals(int patientId, TextField bpField, TextField heartRateField, 
                                 TextField tempField, TextField spo2Field, TextField weightField) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT blood_pressure, heart_rate, temperature, oxygen_level, weight " +
                 "FROM vitals WHERE patient_id = ? " +
                 "ORDER BY timestamp DESC LIMIT 1")) {
            
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                bpField.setText(rs.getString("blood_pressure"));
                heartRateField.setText(rs.getString("heart_rate"));
                tempField.setText(rs.getString("temperature"));
                spo2Field.setText(rs.getString("oxygen_level"));
                weightField.setText(rs.getString("weight"));
            } else {
                // Clear fields if no vitals found
                bpField.clear();
                heartRateField.clear();
                tempField.clear();
                spo2Field.clear();
                weightField.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading patient vitals: " + e.getMessage());
        }
    }

    private void savePatientVitals(int patientId, String bp, String heartRate, 
                                 String temp, String spo2, String weight) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO vitals (patient_id, blood_pressure, heart_rate, " +
                 "temperature, oxygen_level, weight, timestamp) VALUES (?, ?, ?, ?, ?, ?, NOW())")) {
            
            ps.setInt(1, patientId);
            ps.setString(2, bp);
            ps.setString(3, heartRate);
            ps.setString(4, temp);
            ps.setString(5, spo2);
            ps.setString(6, weight);
            ps.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save vitals: " + e.getMessage());
        }
    }

    private void showPrescribeFeedback() {
        mainContent.getChildren().clear();
        Label title = new Label("Prescribe/Feedback (Approved Appointments)");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #0097a7; -fx-padding: 0 0 18 0;");

        TableColumn<Appointment, String> patientCol = new TableColumn<>("Patient");
        patientCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPatient()));
        patientCol.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
        patientCol.setPrefWidth(160);

        TableColumn<Appointment, String> dateCol = new TableColumn<>("Date & Time");
        dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDate()));
        dateCol.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
        dateCol.setPrefWidth(180);

        TableView<Appointment> patientsTable = new TableView<>();
        patientsTable.getColumns().setAll(patientCol, dateCol);
        ObservableList<Appointment> approvedAppointments = fetchAppointmentsForDoctorByStatus(doctorId, "Approved");
        patientsTable.setItems(approvedAppointments);
        patientsTable.setPlaceholder(new Label("No approved appointments found."));

        VBox prescriptionBox = new VBox(18);
        prescriptionBox.setPadding(new Insets(24));
        prescriptionBox.setStyle("-fx-background-color: #f7fafc; -fx-background-radius: 18px; -fx-effect: dropshadow(gaussian, #b2dfdb, 8, 0.10, 0, 1);");
        prescriptionBox.setMinWidth(400);
        prescriptionBox.setAlignment(Pos.TOP_LEFT);
        Label selectPrompt = new Label("Select a patient to write a prescription.");
        selectPrompt.setStyle("-fx-font-size: 18px; -fx-text-fill: #1976d2; -fx-font-weight: bold;");
        prescriptionBox.getChildren().add(selectPrompt);

        TextArea prescriptionTextArea = new TextArea();
        prescriptionTextArea.setPromptText("Enter prescription here...");
        prescriptionTextArea.setWrapText(true);
        prescriptionTextArea.setPrefRowCount(10);
        prescriptionTextArea.setStyle("-fx-font-size: 14px; -fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: #b2dfdb; -fx-border-radius: 8px; -fx-padding: 8px;");
        prescriptionTextArea.setDisable(true);

        Button sendButton = new Button("Send Prescription");
        sendButton.setStyle("-fx-background-color: #0097a7; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 16;");
        sendButton.setDisable(true);
        sendButton.setOnAction(e -> {
            String prescription = prescriptionTextArea.getText();
            if (prescription != null && !prescription.trim().isEmpty()) {
                // Placeholder: Save prescription to database
                savePrescription(patientsTable.getSelectionModel().getSelectedItem().getPatient(), prescription);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Prescription sent successfully!");
                alert.showAndWait();
                prescriptionTextArea.clear();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a prescription.");
                alert.showAndWait();
            }
        });

        patientsTable.setRowFactory(tv -> {
            TableRow<Appointment> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    Appointment appt = row.getItem();
                    prescriptionBox.getChildren().clear();
                    Label patientName = new Label("Patient: " + appt.getPatient());
                    patientName.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0097a7;");
                    prescriptionTextArea.setDisable(false);
                    sendButton.setDisable(false);
                    prescriptionBox.getChildren().addAll(patientName, prescriptionTextArea, sendButton);
                }
            });
            return row;
        });

        HBox hbox = new HBox(32, patientsTable, prescriptionBox);
        hbox.setAlignment(Pos.TOP_LEFT);
        hbox.setPadding(new Insets(18, 0, 0, 0));
        mainContent.getChildren().addAll(title, hbox);
    }

    private void showApprovedAppointments() {
        mainContent.getChildren().clear();
        Label title = new Label("Approved Appointments");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #0097a7; -fx-padding: 0 0 18 0;");

        TableColumn<Appointment, String> patientCol = new TableColumn<>("Patient");
        patientCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPatient()));
        patientCol.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
        patientCol.setPrefWidth(160);

        TableColumn<Appointment, String> dateCol = new TableColumn<>("Date & Time");
        dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDate()));
        dateCol.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
        dateCol.setPrefWidth(180);

        TableColumn<Appointment, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button completeButton = new Button("Mark as Completed");
            {
                completeButton.setStyle("-fx-background-color: #43a047; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 6 12;");
                completeButton.setOnAction(e -> {
                    Appointment appt = getTableView().getItems().get(getIndex());
                    updateAppointmentStatus(appt.getAppointmentId(), "Completed");
                    getTableView().refresh();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(completeButton);
                }
            }
        });
        actionCol.setPrefWidth(150);

        TableView<Appointment> approvedTable = new TableView<>();
        approvedTable.getColumns().setAll(patientCol, dateCol, actionCol);
        ObservableList<Appointment> approvedAppointments = fetchAppointmentsForDoctorByStatus(doctorId, "Approved");
        approvedTable.setItems(approvedAppointments);
        approvedTable.setPlaceholder(new Label("No approved appointments found."));

        VBox approvedBox = new VBox(18, approvedTable);
        approvedBox.setPadding(new Insets(24));
        approvedBox.setStyle("-fx-background-color: #f7fafc; -fx-background-radius: 18px; -fx-effect: dropshadow(gaussian, #b2dfdb, 8, 0.10, 0, 1);");
        approvedBox.setMinWidth(500);
        approvedBox.setAlignment(Pos.TOP_LEFT);

        mainContent.getChildren().addAll(title, approvedBox);
    }

    private void updateAppointmentStatus(int appointmentId, String status) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE appointments SET status = ? WHERE appointment_id = ?")) {
            ps.setString(1, status);
            ps.setInt(2, appointmentId);
            ps.executeUpdate();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Appointment marked as " + status + ".");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to update appointment status.");
            alert.showAndWait();
        }
    }

    private void savePrescription(String patientName, String prescription) {
        int patientId = getPatientIdByName(patientName);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO prescriptions (patient_id, doctor_id, prescription) VALUES (?, ?, ?)")) {
            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ps.setString(3, prescription);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            // Optionally show an error dialog
        }
    }

    private void showMedicalHistory() {
        mainContent.getChildren().clear();
        Label title = new Label("Medical History (Approved Appointments)");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #0097a7; -fx-padding: 0 0 18 0;");

        TableColumn<Appointment, String> patientCol = new TableColumn<>("Patient");
        patientCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPatient()));
        patientCol.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
        patientCol.setPrefWidth(160);

        TableColumn<Appointment, String> dateCol = new TableColumn<>("Date & Time");
        dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDate()));
        dateCol.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
        dateCol.setPrefWidth(180);

        TableView<Appointment> patientsTable = new TableView<>();
        patientsTable.getColumns().setAll(patientCol, dateCol);
        ObservableList<Appointment> approvedAppointments = fetchAppointmentsForDoctorByStatus(doctorId, "Approved");
        patientsTable.setItems(approvedAppointments);
        patientsTable.setPlaceholder(new Label("No approved appointments found."));

        VBox medicalRecordBox = new VBox(18);
        medicalRecordBox.setPadding(new Insets(24));
        medicalRecordBox.setStyle("-fx-background-color: #f7fafc; -fx-background-radius: 18px; -fx-effect: dropshadow(gaussian, #b2dfdb, 8, 0.10, 0, 1);");
        medicalRecordBox.setMinWidth(400);
        medicalRecordBox.setAlignment(Pos.TOP_LEFT);
        Label selectPrompt = new Label("Select a patient to view medical record.");
        selectPrompt.setStyle("-fx-font-size: 18px; -fx-text-fill: #1976d2; -fx-font-weight: bold;");
        medicalRecordBox.getChildren().add(selectPrompt);

        TextArea medicalRecordTextArea = new TextArea();
        medicalRecordTextArea.setPromptText("Medical record will appear here...");
        medicalRecordTextArea.setWrapText(true);
        medicalRecordTextArea.setPrefRowCount(10);
        medicalRecordTextArea.setStyle("-fx-font-size: 14px; -fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: #b2dfdb; -fx-border-radius: 8px; -fx-padding: 8px;");
        medicalRecordTextArea.setDisable(true);

        Button downloadButton = new Button("Download as Word");
        downloadButton.setStyle("-fx-background-color: #0097a7; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 16;");
        downloadButton.setDisable(true);
        downloadButton.setOnAction(e -> {
            String patientName = patientsTable.getSelectionModel().getSelectedItem().getPatient();
            downloadAsWord(patientName, medicalRecordTextArea.getText());
        });

        // Add export/save as file button
        Button exportButton = new Button("Save as File");
        exportButton.setStyle("-fx-background-color: #43a047; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 16;");
        exportButton.setDisable(true);
        exportButton.setOnAction(e -> {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Save Medical Record");
            fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Text Files", "*.txt"));
            java.io.File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                try (java.io.FileWriter fw = new java.io.FileWriter(file)) {
                    fw.write(medicalRecordTextArea.getText());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Medical record saved to: " + file.getAbsolutePath());
                    alert.showAndWait();
                } catch (Exception ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Failed to save file: " + ex.getMessage());
                    alert.showAndWait();
                }
            }
        });

        patientsTable.setRowFactory(tv -> {
            TableRow<Appointment> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    Appointment appt = row.getItem();
                    medicalRecordBox.getChildren().clear();
                    Label patientName = new Label("Patient: " + appt.getPatient());
                    patientName.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0097a7;");
                    medicalRecordTextArea.setDisable(false);
                    downloadButton.setDisable(false);
                    exportButton.setDisable(false);
                    // Fetch medical records from DB
                    StringBuilder recordText = new StringBuilder();
                    boolean hasRecord = false;
                    try (Connection conn = DatabaseConnector.DBConnection.getConnection();
                         PreparedStatement ps = conn.prepareStatement(
                             "SELECT record_date, comments, file_path FROM medical_records WHERE patient_id = ? ORDER BY record_date DESC")) {
                        ps.setInt(1, getPatientIdByName(appt.getPatient()));
                        ResultSet rs = ps.executeQuery();
                        while (rs.next()) {
                            hasRecord = true;
                            recordText.append("Date: ").append(rs.getString("record_date")).append("\n");
                            recordText.append("Comments: ").append(rs.getString("comments")).append("\n");
                            String filePath = rs.getString("file_path");
                            if (filePath != null && !filePath.isEmpty()) {
                                recordText.append("File: ").append(filePath).append("\n");
                            }
                            recordText.append("-----------------------------\n");
                        }
                    } catch (Exception ex) {
                        recordText.append("Error loading medical records: ").append(ex.getMessage());
                    }
                    if (!hasRecord) {
                        recordText.append("No medical history available for this patient.");
                    }
                    medicalRecordTextArea.setText(recordText.toString());
                    medicalRecordBox.getChildren().addAll(patientName, medicalRecordTextArea, downloadButton, exportButton);
                }
            });
            return row;
        });

        HBox hbox = new HBox(32, patientsTable, medicalRecordBox);
        hbox.setAlignment(Pos.TOP_LEFT);
        hbox.setPadding(new Insets(18, 0, 0, 0));
        mainContent.getChildren().setAll(title, hbox);
    }

    private void downloadAsWord(String patientName, String medicalRecord) {
        // Placeholder: Download medical record as Word document
        System.out.println("Downloading medical record for " + patientName + " as Word document.");
    }


    // Helper for summary boxes (like PatientDashboard)
    private VBox createSummaryBox(String title, String count, String buttonText, String color, Runnable onClick, String icon) {
        VBox box = new VBox(18);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 22px; -fx-effect: dropshadow(gaussian, #b2dfdb, 12, 0.20, 0, 2); -fx-pref-width: 290px; -fx-pref-height: 110px;");

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 36px; -fx-padding: 0 0 8 0; -fx-text-fill: " + color + ";");
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        Label countLabel = new Label(count);
        countLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #263238;");
        Button actionBtn = new Button(buttonText);
        actionBtn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10px; -fx-padding: 6 22;");
        actionBtn.setMaxWidth(Double.MAX_VALUE);
        actionBtn.setOnAction(e -> onClick.run());

        // Make the whole box clickable
        box.setOnMouseClicked(e -> onClick.run());
        box.setOnMouseEntered(e -> box.setStyle("-fx-background-color: #f0f4ff; -fx-background-radius: 22px; -fx-effect: dropshadow(gaussian, #b2dfdb, 12, 0.22, 0, 2); -fx-pref-width: 290px; -fx-pref-height: 110px;"));
        box.setOnMouseExited(e -> box.setStyle("-fx-background-color: white; -fx-background-radius: 22px; -fx-effect: dropshadow(gaussian, #b2dfdb, 12, 0.20, 0, 2); -fx-pref-width: 290px; -fx-pref-height: 110px;"));

        box.getChildren().addAll(iconLabel, titleLabel, countLabel, actionBtn);
        VBox.setVgrow(box, Priority.ALWAYS);
        return box;
    }

    private void showConversation() {
        mainContent.getChildren().clear();
        Label title = new Label("Conversation Options");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #0097a7; -fx-padding: 0 0 18 0;");

        // Patient Selection (moved to top)
        ComboBox<String> patientComboBox = new ComboBox<>();
        patientComboBox.setPromptText("Select a patient");
        patientComboBox.setStyle("-fx-font-size: 14px; -fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: #b2dfdb; -fx-border-radius: 8px; -fx-padding: 8px;");
        ObservableList<Appointment> approvedAppointments = fetchAppointmentsForDoctorByStatus(doctorId, "Approved");
        for (Appointment appt : approvedAppointments) {
            patientComboBox.getItems().add(appt.getPatient());
        }

        // Recent Chats Section
        VBox recentChatsBox = new VBox(10);
        recentChatsBox.setPadding(new Insets(24));
        recentChatsBox.setStyle("-fx-background-color: #f7fafc; -fx-background-radius: 18px; -fx-effect: dropshadow(gaussian, #b2dfdb, 8, 0.10, 0, 1);");
        recentChatsBox.setMinWidth(300);
        recentChatsBox.setAlignment(Pos.TOP_LEFT);

        Label recentChatsTitle = new Label("Recent Chats");
        recentChatsTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0097a7;");

        ListView<RecentChat> recentChatsList = new ListView<>();
        recentChatsList.setPrefHeight(400);
        recentChatsList.setStyle("-fx-background-color: transparent;");
        recentChatsList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(RecentChat chat, boolean empty) {
                super.updateItem(chat, empty);
                if (empty || chat == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox chatBox = new VBox(5);
                    Label patientName = new Label(chat.getPatientName());
                    patientName.setStyle("-fx-font-weight: bold; -fx-text-fill: #263238;");
                    Label lastMessage = new Label(chat.getLastMessage());
                    lastMessage.setStyle("-fx-text-fill: #666666;");
                    Label timestamp = new Label(chat.getTimestamp());
                    timestamp.setStyle("-fx-text-fill: #999999; -fx-font-size: 12px;");
                    chatBox.getChildren().addAll(patientName, lastMessage, timestamp);
                    setGraphic(chatBox);
                }
            }
        });

        // Load recent chats
        ObservableList<RecentChat> recentChats = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT DISTINCT p.name as patient_name, " +
                 "(SELECT message FROM chat_messages WHERE (doctor_id = ? AND patient_id = p.user_id) " +
                 "ORDER BY timestamp DESC LIMIT 1) as last_message, " +
                 "(SELECT timestamp FROM chat_messages WHERE (doctor_id = ? AND patient_id = p.user_id) " +
                 "ORDER BY timestamp DESC LIMIT 1) as last_timestamp " +
                 "FROM users p " +
                 "JOIN chat_messages cm ON p.user_id = cm.patient_id " +
                 "WHERE cm.doctor_id = ? " +
                 "ORDER BY last_timestamp DESC"
             )) {
            ps.setInt(1, doctorId);
            ps.setInt(2, doctorId);
            ps.setInt(3, doctorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                recentChats.add(new RecentChat(
                    rs.getString("patient_name"),
                    rs.getString("last_message"),
                    rs.getString("last_timestamp")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        recentChatsList.setItems(recentChats);

        // Handle recent chat selection
        recentChatsList.setOnMouseClicked(e -> {
            RecentChat selectedChat = recentChatsList.getSelectionModel().getSelectedItem();
            if (selectedChat != null) {
                patientComboBox.setValue(selectedChat.getPatientName());
                // This will trigger the patientComboBox action handler and load the chat
            }
        });

        recentChatsBox.getChildren().addAll(recentChatsTitle, recentChatsList);

        // Main Chat Section
        VBox chatBox = new VBox(18);
        chatBox.setPadding(new Insets(24));
        chatBox.setStyle("-fx-background-color: #f7fafc; -fx-background-radius: 18px; -fx-effect: dropshadow(gaussian, #b2dfdb, 8, 0.10, 0, 1);");
        chatBox.setMinWidth(500);
        chatBox.setAlignment(Pos.TOP_LEFT);

        Label chatTitle = new Label("Chat");
        chatTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0097a7;");

        // ListView for chat messages
        ListView<ChatMessage> chatListView = new ListView<>();
        ObservableList<ChatMessage> chatMessages = FXCollections.observableArrayList();
        chatListView.setItems(chatMessages);
        chatListView.setPrefHeight(300);
        chatListView.setStyle("-fx-background-color: transparent;");
        chatListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ChatMessage msg, boolean empty) {
                super.updateItem(msg, empty);
                if (empty || msg == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label bubble = new Label(msg.getContent());
                    bubble.setWrapText(true);
                    bubble.setMaxWidth(320);
                    bubble.setPadding(new Insets(10, 16, 10, 16));
                    bubble.setStyle("-fx-background-radius: 18px; -fx-font-size: 15px; -fx-font-weight: normal; -fx-effect: dropshadow(gaussian, #b2dfdb, 4, 0.10, 0, 1);");
                    HBox hbox = new HBox(bubble);
                    if (msg.getSender().equals("doctor")) {
                        hbox.setAlignment(Pos.CENTER_RIGHT);
                        bubble.setStyle(bubble.getStyle() + "-fx-background-color: #dcf8c6; -fx-text-fill: #263238;");
                    } else if (msg.getSender().equals("patient")) {
                        hbox.setAlignment(Pos.CENTER_LEFT);
                        bubble.setStyle(bubble.getStyle() + "-fx-background-color: #ffffff; -fx-text-fill: #263238; -fx-border-color: #b2dfdb; -fx-border-width: 1px;");
                    } else {
                        hbox.setAlignment(Pos.CENTER);
                        bubble.setStyle(bubble.getStyle() + "-fx-background-color: #e3f6fd; -fx-text-fill: #1976d2;");
                    }
                    setGraphic(hbox);
                    setText(null);
                }
            }
        });
        chatListView.setDisable(true);

        TextField messageInput = new TextField();
        messageInput.setPromptText("Type your message here...");
        messageInput.setStyle("-fx-font-size: 14px; -fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: #b2dfdb; -fx-border-radius: 8px; -fx-padding: 8px;");
        messageInput.setDisable(true);

        Button sendButton = new Button("Send");
        sendButton.setStyle("-fx-background-color: #0097a7; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 16;");
        sendButton.setDisable(true);

        // Map patient name to patientId
        java.util.Map<String, Integer> patientNameToId = new java.util.HashMap<>();
        for (Appointment appt : approvedAppointments) {
            patientNameToId.put(appt.getPatient(), getPatientIdByName(appt.getPatient()));
        }

        HBox inputBox = new HBox(8, messageInput, sendButton);
        inputBox.setAlignment(Pos.CENTER_LEFT);

        chatBox.getChildren().addAll(chatTitle, chatListView, inputBox);

        // Simple Video Call Button
        Button videoCallButton = new Button("Start Video Call");
        videoCallButton.setStyle("-fx-background-color: #43a047; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 16;");
        videoCallButton.setDisable(true);

        // On patient selection, load chat history
        patientComboBox.setOnAction(e -> {
            String selectedPatient = patientComboBox.getValue();
            if (selectedPatient != null) {
                chatListView.setDisable(false);
                messageInput.setDisable(false);
                sendButton.setDisable(false);
                videoCallButton.setDisable(false);
                chatMessages.clear();
                Integer patientId = patientNameToId.get(selectedPatient);
                if (patientId != null) {
                    ObservableList<DatabaseConnector.DBConnection.ChatMessage> dbMsgs = DatabaseConnector.DBConnection.fetchChatMessages(doctorId, patientId);
                    for (DatabaseConnector.DBConnection.ChatMessage dbMsg : dbMsgs) {
                        chatMessages.add(new ChatMessage(dbMsg.getSender(), dbMsg.getMessage(), dbMsg.getTimestamp()));
                    }
                }
                chatMessages.add(new ChatMessage("system", "Chat with " + selectedPatient + " started.", java.time.LocalTime.now().toString()));
                chatListView.scrollTo(chatMessages.size() - 1);
            } else {
                chatListView.setDisable(false);
                messageInput.setDisable(true);
                sendButton.setDisable(true);
                videoCallButton.setDisable(true);
            }
        });

        // Add send button functionality
        sendButton.setOnAction(e -> {
            String message = messageInput.getText().trim();
            if (!message.isEmpty() && patientComboBox.getValue() != null) {
                Integer patientId = patientNameToId.get(patientComboBox.getValue());
                if (patientId != null) {
                    // Save message to database
                    DatabaseConnector.DBConnection.insertChatMessage(
                        doctorId,
                        patientId,
                        "doctor",
                        message,
                        java.time.LocalDateTime.now().toString()
                    );
                    
                    // Add message to chat view
                    chatMessages.add(new ChatMessage("doctor", message, java.time.LocalDateTime.now().toString()));
                    messageInput.clear();
                    chatListView.scrollTo(chatMessages.size() - 1);
                }
            }
        });

        // Add enter key support for sending messages
        messageInput.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                sendButton.fire();
            }
        });

        // Update the layout to include recent chats
        HBox mainChatSection = new HBox(32, recentChatsBox, chatBox);
        VBox conversationBox = new VBox(32, patientComboBox, mainChatSection, videoCallButton);
        conversationBox.setPadding(new Insets(24));
        conversationBox.setStyle("-fx-background-color: #f7fafc; -fx-background-radius: 18px; -fx-effect: dropshadow(gaussian, #b2dfdb, 8, 0.10, 0, 1);");
        conversationBox.setMinWidth(900);
        conversationBox.setAlignment(Pos.TOP_LEFT);

        mainContent.getChildren().addAll(title, conversationBox);
    }

    // Helper to get patientId by name from users table (returns -1 if not found)
    private int getPatientIdByName(String patientName) {
        try (Connection conn = DatabaseConnector.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT user_id FROM users WHERE name = ? AND role = 'Patient' LIMIT 1")) {
            ps.setString(1, patientName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (Exception e) {
            System.err.println("Error fetching patient ID by name: " + e.getMessage());
        }
        return -1;
    }

    // WhatsApp-like chat message class
    private static class ChatMessage {
        private final String sender; // "doctor" or "patient" or "system"
        private final String content;
        private final String timestamp;
        public ChatMessage(String sender, String content, String timestamp) {
            this.sender = sender;
            this.content = content;
            this.timestamp = timestamp;
        }
        public String getSender() { return sender; }
        public String getContent() { return content; }
        public String getTimestamp() { return timestamp; }
    }

    // Add this class at the end of the file, before the last closing brace
    private static class RecentChat {
        private final String patientName;
        private final String lastMessage;
        private final String timestamp;

        public RecentChat(String patientName, String lastMessage, String timestamp) {
            this.patientName = patientName;
            this.lastMessage = lastMessage;
            this.timestamp = timestamp;
        }

        public String getPatientName() { return patientName; }
        public String getLastMessage() { return lastMessage; }
        public String getTimestamp() { return timestamp; }
    }

    private int getNotificationCount() {
        int count = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM notifications WHERE user_id = ?")) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    private int getChatCount() {
        int count = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(DISTINCT patient_id) FROM chat_messages WHERE doctor_id = ?")) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    private int getAppointmentCountByStatus(String status) {
        int count = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND status = ?")) {
            ps.setInt(1, doctorId);
            ps.setString(2, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    private int getActiveEmergencyCount() {
        int count = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT COUNT(*) FROM emergency_alerts e " +
                 "JOIN appointments a ON a.patient_id = e.patient_id " +
                 "WHERE a.doctor_id = ? AND e.status IN ('Active', 'Monitoring')")) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    private void updateEmergencyStatus(int alertId, String newStatus) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE emergency_alerts SET status = ? WHERE alert_id = ?")) {
            ps.setString(1, newStatus);
            ps.setInt(2, alertId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            // Show error dialog
        }
    }

    private void showEmergencyAlertsSection() {
        // Fetch emergency alerts (optionally filter to doctor's patients)
        ObservableList<EmergencyAlert> emergencies = fetchDoctorEmergencies();
        TableView<EmergencyAlert> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);
        table.setStyle("-fx-background-radius: 14px; -fx-border-radius: 14px; -fx-background-color: white; -fx-effect: dropshadow(gaussian, #b2dfdb, 8, 0.10, 0, 1);");

        TableColumn<EmergencyAlert, String> patientCol = new TableColumn<>("Patient");
        patientCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPatientName()));

        TableColumn<EmergencyAlert, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTimestamp()));

        TableColumn<EmergencyAlert, String> typeCol = new TableColumn<>("Alert Type");
        typeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAlertType()));

        TableColumn<EmergencyAlert, String> vitalsCol = new TableColumn<>("Vitals");
        vitalsCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getVitalsInfo()));

        TableColumn<EmergencyAlert, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status) {
                        case "Active" -> setStyle("-fx-text-fill: #e53935; -fx-font-weight: bold;");
                        case "Monitoring" -> setStyle("-fx-text-fill: #f57c00; -fx-font-weight: bold;");
                        case "Resolved" -> setStyle("-fx-text-fill: #43a047; -fx-font-weight: bold;");
                        default -> setStyle("");
                    }
                }
            }
        });
        // Add action column (Acknowledge/Resolve)
        TableColumn<EmergencyAlert, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button acknowledgeBtn = new Button("Acknowledge");
            private final Button resolveBtn = new Button("Resolve");
            {
                acknowledgeBtn.setStyle("-fx-background-color: #e53935; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px; -fx-padding: 4 12;");
                resolveBtn.setStyle("-fx-background-color: #43a047; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px; -fx-padding: 4 12;");
                acknowledgeBtn.setOnAction(e -> {
                    EmergencyAlert alert = getTableView().getItems().get(getIndex());
                    updateEmergencyStatus(alert.getAlertId(), "Monitoring");
                    showEmergencyAlertsSection();
                });
                resolveBtn.setOnAction(e -> {
                    EmergencyAlert alert = getTableView().getItems().get(getIndex());
                    updateEmergencyStatus(alert.getAlertId(), "Resolved");
                    showEmergencyAlertsSection();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    EmergencyAlert alert = getTableView().getItems().get(getIndex());
                    HBox buttons = new HBox(8);
                    if ("Active".equals(alert.getStatus())) {
                        buttons.getChildren().add(acknowledgeBtn);
                    } else if ("Monitoring".equals(alert.getStatus())) {
                        buttons.getChildren().add(resolveBtn);
                    }
                    setGraphic(buttons);
                }
            }
        });

        table.getColumns().addAll(patientCol, timeCol, typeCol, vitalsCol, statusCol, actionCol);
        table.setItems(emergencies);

        // Add patient details panel
        VBox detailsPanel = new VBox(15);
        detailsPanel.setPadding(new Insets(20));
        detailsPanel.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 14px; -fx-border-color: #e0e0e0; -fx-border-radius: 14px;");
        detailsPanel.setPrefWidth(300);

        Label detailsTitle = new Label("Alert Details");
        detailsTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1976d2;");

        Label selectPrompt = new Label("Select an alert to view details");
        selectPrompt.setStyle("-fx-text-fill: #666; -fx-font-size: 14px;");

        detailsPanel.getChildren().addAll(detailsTitle, selectPrompt);

        // Update details panel when a row is selected
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateAlertDetails(detailsPanel, newSelection);
            }
        });

        HBox content = new HBox(20, table, detailsPanel);
        content.setPadding(new Insets(20));

        VBox mainBox = new VBox(20, new Label("Emergency Alerts"), content);
        mainBox.setPadding(new Insets(20));
        mainContent.getChildren().setAll(mainBox);
    }

    private ObservableList<EmergencyAlert> fetchDoctorEmergencies() {
        ObservableList<EmergencyAlert> alerts = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT DISTINCT e.alert_id, u.name, e.alert_time, e.comments, e.vitals_info, e.status, " +
                 "CASE WHEN e.triggered_by = 'BUTTON' THEN 'Emergency Button' ELSE 'Critical Vitals' END as alert_type " +
                 "FROM emergency_alerts e " +
                 "JOIN users u ON e.patient_id = u.user_id " +
                 "JOIN appointments a ON a.patient_id = e.patient_id " +
                 "WHERE a.doctor_id = ? AND e.status IN ('Active', 'Monitoring', 'Resolved') " +
                 "ORDER BY e.alert_time DESC")) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();
            java.util.HashSet<Integer> seenAlertIds = new java.util.HashSet<>();
            while (rs.next()) {
                int alertId = rs.getInt("alert_id");
                if (seenAlertIds.contains(alertId)) continue;
                seenAlertIds.add(alertId);
                alerts.add(new EmergencyAlert(
                    alertId,
                    rs.getString("name"),
                    rs.getTimestamp("alert_time").toString(),
                    rs.getString("comments"),
                    rs.getString("vitals_info"),
                    rs.getString("status"),
                    rs.getString("alert_type")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alerts;
    }

    private void updateAlertDetails(VBox detailsPanel, EmergencyAlert alert) {
        detailsPanel.getChildren().clear();
        Label title = new Label("Alert Details");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1976d2;");
        Label patientName = new Label("Patient: " + alert.getPatientName());
        Label timestamp = new Label("Time: " + alert.getTimestamp());
        Label alertType = new Label("Type: " + alert.getAlertType());
        Label vitals = new Label("Vitals:\n" + alert.getVitalsInfo());
        Label comments = new Label("Comments:\n" + (alert.getComments() != null && !alert.getComments().isEmpty() ? alert.getComments() : "None"));
        Label status = new Label("Status: " + alert.getStatus());
        for (Label label : new Label[]{patientName, timestamp, alertType, vitals, comments, status}) {
            label.setStyle("-fx-font-size: 14px; -fx-text-fill: #333; -fx-padding: 5 0;");
            label.setWrapText(true);
        }
        detailsPanel.getChildren().addAll(title, patientName, timestamp, alertType, vitals, comments, status);
    }

    // EmergencyAlert class for emergency notifications (copied from AdminDashboard)
    private static class EmergencyAlert {
        private final int alertId;
        private final String patientName;
        private final String timestamp;
        private final String comments;
        private final String vitalsInfo;
        private final String status;
        private final String alertType;

        public EmergencyAlert(int alertId, String patientName, String timestamp,
                              String comments, String vitalsInfo, String status,
                              String alertType) {
            this.alertId = alertId;
            this.patientName = patientName;
            this.timestamp = timestamp;
            this.comments = comments;
            this.vitalsInfo = vitalsInfo;
            this.status = status;
            this.alertType = alertType;
        }

        public int getAlertId() { return alertId; }
        public String getPatientName() { return patientName; }
        public String getTimestamp() { return timestamp; }
        public String getComments() { return comments; }
        public String getVitalsInfo() { return vitalsInfo; }
        public String getStatus() { return status; }
        public String getAlertType() { return alertType; }
    }
} 