package UI;

import HealthDataHandling.VitalsDAO;
import javafx.scene.image.Image;
import java.io.File;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import HealthDataHandling.VitalSign;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import UserManagement.Patient;
import DatabaseConnector.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.application.Platform;
import java.io.*;
import java.net.*;
import Notification.Notifiable;
import Notification.EmailNotification;
import Notification.SMSNotification;
import Notification.NotificationDAO;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import Doctor_PatientInteraction.PrescriptionDAO;
//import ChatAndVedioConsultation.ChatServer;
//import Chat.ChatServer;
import javafx.scene.input.KeyCode;


public class PatientDashboard {

    private final int patientId;
    private final Stage stage;
    private final BorderPane root;
    private final VBox mainContent;
    private final String patientName;
    private Tab myAppointmentsTab;
    private TableView<Appointment> appointmentsTable;

    public PatientDashboard(Stage stage, int patientId) {
        this.stage = stage;
        this.patientId = patientId;
        this.root = new BorderPane();
        this.mainContent = new VBox(20);
        this.mainContent.setPadding(new Insets(30, 10, 30, 30));
        this.mainContent.setStyle("-fx-background-color: #f7fafc;");
        this.patientName = fetchPatientNameFromDB(patientId);
        setupUI();
    }

    private void setupUI() {
        // Sidebar with main options
        BorderPane sidebar = new BorderPane();
        VBox navButtons = new VBox(10); // Reduced from 18 to 10
        navButtons.setAlignment(Pos.TOP_LEFT);
        navButtons.setPadding(new Insets(20, 18, 20, 18));
        navButtons.setStyle(""); // No background here, set on sidebar

        sidebar.setStyle("-fx-background-color: #2c3e50; -fx-border-color: #e0e0e0; -fx-border-width: 0 2 0 0; -fx-effect: dropshadow(gaussian, #b2dfdb, 12, 0.2, 2, 0); -fx-background-radius: 18px; -fx-min-width: 270px;");

        // Welcome message at the top of the sidebar
        Label welcomeLabel = new Label("Welcome, " + patientName + "!");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; -fx-padding: 0 0 12 0;"); // Changed text color to white
        welcomeLabel.setAlignment(Pos.CENTER);
        welcomeLabel.setMaxWidth(Double.MAX_VALUE);

        Button btnDashboard = createSidebarButton("Dashboard");
        Button btnProfile = createSidebarButton("Profile");
        Button btnAppointments = createSidebarButton("Appointments");
        Button btnVitals = createSidebarButton("Vitals");
        Button btnEmergency = createSidebarButton("Emergency");
        Button btnMedicalRecord = createSidebarButton("Medical Record");
        Button btnPrescription = createSidebarButton("Prescription");
        Button btnConversation = createSidebarButton("Conversation");
        Button btnUploadVitals = createSidebarButton("Upload Vitals");

        // Add sidebar buttons in the desired order
        navButtons.getChildren().setAll(
            welcomeLabel, btnDashboard, btnProfile, btnAppointments, btnConversation, btnVitals, btnUploadVitals, btnEmergency, btnMedicalRecord, btnPrescription, new Separator()
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
        VBox.setMargin(btnLogout, new Insets(0, 0, 18, 0)); // More bottom margin

        // Add a separator above the logout button
        Separator logoutSeparator = new Separator();
        logoutSeparator.setStyle("-fx-background-color: #e0e0e0;");
        VBox logoutBox = new VBox(10, logoutSeparator, btnLogout);
        logoutBox.setAlignment(Pos.CENTER);
        logoutBox.setPadding(new Insets(0, 0, 18, 0)); // Add bottom padding

        // Place navigation in center, logout at bottom
        sidebar.setCenter(navButtons);
        sidebar.setBottom(logoutBox);

        // Ensure sidebar fills the height of the window
        sidebar.setPrefHeight(Double.MAX_VALUE);

        // Main content area
        showWelcomeAndContent("Dashboard");

        // Button actions
        btnDashboard.setOnAction(e -> showWelcomeAndContent("Dashboard"));
        btnProfile.setOnAction(e -> showProfile());
        btnAppointments.setOnAction(e -> showWelcomeAndContent("Appointments"));
        btnVitals.setOnAction(e -> showWelcomeAndContent("Vitals"));
        btnEmergency.setOnAction(e -> showWelcomeAndContent("Emergency"));
        btnMedicalRecord.setOnAction(e -> showWelcomeAndContent("Medical Record"));
        btnPrescription.setOnAction(e -> showWelcomeAndContent("Prescription"));
        btnConversation.setOnAction(e -> showConversation());
        btnUploadVitals.setOnAction(e -> showUploadVitals());
        btnLogout.setOnAction(e -> new LoginPage().start(stage));

        root.setLeft(sidebar);
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 1100, 700);
        stage.setScene(scene);
        stage.setTitle("Patient Dashboard");
        stage.setMaximized(true);
        stage.show();

        // Ensure sidebar always fills the window height
        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            sidebar.setMinHeight((double) newVal);
            sidebar.setMaxHeight((double) newVal);
        });
    }

    private Button createSidebarButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 18;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 18;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 18;"));
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    private String fetchPatientNameFromDB(int patientId) {
        String name = "Patient " + patientId;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT name FROM users WHERE user_id = ?")) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                name = rs.getString("name");
            }
        } catch (Exception e) {
            System.out.println("Error fetching patient name: " + e.getMessage());
        }
        return name;
    }

    private void showWelcomeAndContent(String section) {
        mainContent.getChildren().clear();
        if (section == null || section.equals("Dashboard")) {
            GridPane grid = new GridPane();
            grid.setHgap(32);
            grid.setVgap(32);
            grid.setAlignment(Pos.CENTER);
            grid.setPadding(new Insets(32, 32, 32, 0));

            // Make 3 columns, 2 rows
            for (int i = 0; i < 3; i++) {
                ColumnConstraints col = new ColumnConstraints();
                col.setPercentWidth(33.33);
                col.setHgrow(Priority.ALWAYS);
                grid.getColumnConstraints().add(col);
            }
            for (int i = 0; i < 2; i++) {
                RowConstraints row = new RowConstraints();
                row.setPercentHeight(50);
                row.setVgrow(Priority.ALWAYS);
                grid.getRowConstraints().add(row);
            }

            int appointmentCount = fetchAppointmentsForPatient(patientId).size();
            int prescriptionCount = PrescriptionDAO.getAllPrescriptionsForPatient(patientId).size();

            VBox boxAppointments = createSummaryBox("Appointments", String.valueOf(appointmentCount), "View", "#3498db", () -> showWelcomeAndContent("Appointments"), null);
            VBox boxBook = createSummaryBox("Book Appointment", null, "Book", "#43a047", () -> showWelcomeAndContent("Appointments"), "\uD83D\uDCC5");
            VBox boxVitals = createSummaryBox("Vitals", null, "View", "#00bcd4", () -> showWelcomeAndContent("Vitals"), "\uD83D\uDC89");
            boxVitals.setStyle(boxVitals.getStyle() + " -fx-pref-width: 290px; -fx-pref-height: 220px;");
            VBox boxPrescriptions = createSummaryBox("Prescriptions", String.valueOf(prescriptionCount), "View", "#fbc02d", () -> showWelcomeAndContent("Prescription"), null);
            VBox boxRecords = createSummaryBox("Medical Records", null, "View", "#8e24aa", () -> showWelcomeAndContent("Medical Record"), "\uD83D\uDCC4");
            VBox boxEmergency = createEmergencyBox();
            boxEmergency.setStyle(boxEmergency.getStyle() + " -fx-pref-width: 290px; -fx-pref-height: 220px;");

            grid.add(boxAppointments, 0, 0);
            grid.add(boxBook, 1, 0);
            grid.add(boxVitals, 2, 0);
            grid.add(boxPrescriptions, 0, 1);
            grid.add(boxRecords, 1, 1);
            grid.add(boxEmergency, 2, 1);

            // Let boxes grow to fill cells
            GridPane.setHgrow(boxAppointments, Priority.ALWAYS);
            GridPane.setVgrow(boxAppointments, Priority.ALWAYS);
            GridPane.setHgrow(boxBook, Priority.ALWAYS);
            GridPane.setVgrow(boxBook, Priority.ALWAYS);
            GridPane.setHgrow(boxVitals, Priority.ALWAYS);
            GridPane.setVgrow(boxVitals, Priority.ALWAYS);
            GridPane.setHgrow(boxPrescriptions, Priority.ALWAYS);
            GridPane.setVgrow(boxPrescriptions, Priority.ALWAYS);
            GridPane.setHgrow(boxRecords, Priority.ALWAYS);
            GridPane.setVgrow(boxRecords, Priority.ALWAYS);
            GridPane.setHgrow(boxEmergency, Priority.ALWAYS);
            GridPane.setVgrow(boxEmergency, Priority.ALWAYS);

            mainContent.getChildren().add(grid);
            VBox.setVgrow(grid, Priority.ALWAYS);
            return;
        }
        switch (section) {
            case "Appointments" -> showAppointments();
            case "Vitals" -> showVitals();
            case "Emergency" -> showEmergency();
            case "Medical Record" -> showMedicalRecord();
            case "Prescription" -> showPrescription();
        }
    }

    private VBox createSummaryBox(String title, String count, String buttonText, String color, Runnable onClick, String icon) {
        VBox box = new VBox(12); // Reduced spacing from 18 to 12
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(24)); // Reduced padding from 38 to 24
        box.setStyle("-fx-background-color: white; -fx-background-radius: 22px; -fx-effect: dropshadow(gaussian, #b2dfdb, 12, 0.20, 0, 2); -fx-pref-width: 240px; -fx-pref-height: 180px;"); // Reduced width and height

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + color + ";"); // Reduced font size from 20px to 18px
        Node centerNode;
        if (icon != null) {
            Label iconLabel = new Label(icon);
            iconLabel.setStyle("-fx-font-size: 48px; -fx-padding: 0 0 6 0; -fx-text-fill: " + color + ";"); // Reduced font size from 60px to 48px
            centerNode = iconLabel;
        } else {
            Label countLabel = new Label(count);
            countLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #263238;"); // Reduced font size from 44px to 36px
            centerNode = countLabel;
        }
        Button actionBtn = new Button(buttonText);
        actionBtn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10px; -fx-padding: 8 24;"); // Reduced padding
        actionBtn.setMaxWidth(Double.MAX_VALUE);
        actionBtn.setOnAction(e -> onClick.run());

        box.getChildren().addAll(titleLabel, centerNode, actionBtn);
        VBox.setVgrow(box, Priority.ALWAYS);
        return box;
    }

    private VBox createEmergencyBox() {
        VBox box = new VBox(12); // Reduced spacing from 18 to 12
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(24)); // Reduced padding from 38 to 24
        box.setStyle("-fx-background-color: white; -fx-background-radius: 22px; -fx-effect: dropshadow(gaussian, #b2dfdb, 12, 0.20, 0, 2); -fx-pref-width: 240px; -fx-pref-height: 180px;"); // Reduced width and height

        Label titleLabel = new Label("Emergency");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #d32f2f;"); // Reduced font size from 20px to 18px
        Label iconLabel = new Label("⚠");
        iconLabel.setStyle("-fx-font-size: 48px; -fx-text-fill: #d32f2f; -fx-padding: 0 0 6 0;"); // Reduced font size from 60px to 48px
        Button panicBtn = new Button("Panic");
        panicBtn.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10px; -fx-padding: 8 24;"); // Reduced padding
        panicBtn.setMaxWidth(Double.MAX_VALUE);
        panicBtn.setOnAction(e -> showWelcomeAndContent("Emergency"));

        box.getChildren().addAll(titleLabel, iconLabel, panicBtn);
        VBox.setVgrow(box, Priority.ALWAYS);
        return box;
    }

    private void showAppointments() {
        mainContent.getChildren().clear();

        TabPane tabPane = new TabPane();
        tabPane.setStyle(
            "    -fx-tab-min-width: 160px;" +
            "    -fx-tab-max-width: 200px;" +
            "    -fx-background-color: #00bcd4;"
        );
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // --- Book Appointment Tab ---
        Tab bookTab = new Tab("Book Appointment");
        VBox bookForm = new VBox(24);
        bookForm.setPadding(new Insets(40, 48, 40, 48));
        bookForm.setAlignment(Pos.TOP_LEFT);
        bookForm.setStyle("-fx-background-color: #e3f6fd; -fx-effect: dropshadow(gaussian, #b2dfdb, 12, 0.15, 0, 2); -fx-border-color: #00bcd4; -fx-border-width: 2px; -fx-border-style: solid; -fx-border-radius: 0; ");

        // Labels styling
        Label doctorLabel = new Label("Doctor:");
        doctorLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #0097a7;");
        Label dateLabel = new Label("Date:");
        dateLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #0097a7;");
        Label timeLabel = new Label("Time:");
        timeLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #0097a7;");
        Label commentsLabel = new Label("Comments:");
        commentsLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #0097a7;");

        ComboBox<String> doctorCombo = new ComboBox<>();
        doctorCombo.setPromptText("Select Doctor");
        doctorCombo.setPrefWidth(320);
        doctorCombo.setItems(fetchDoctorNamesFromDB());

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Select Date");
        datePicker.setPrefWidth(320);

        ComboBox<String> timeCombo = new ComboBox<>();
        timeCombo.setPromptText("Select Time");
        timeCombo.setPrefWidth(320);
        timeCombo.getItems().addAll("09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30", "14:00");

        TextArea commentsArea = new TextArea();
        commentsArea.setPromptText("Add comments (optional)");
        commentsArea.setPrefRowCount(6);
        commentsArea.setPrefWidth(900);
        commentsArea.setPrefHeight(150);
        commentsArea.setStyle("-fx-background-radius: 8px; -fx-border-radius: 8px; -fx-border-color: #b2ebf2;");

        Button bookBtn = new Button("Book Appointment");
        bookBtn.setStyle("-fx-background-color: linear-gradient(to right, #43e97b 0%, #38f9d7 100%); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12px; -fx-padding: 12 36; -fx-font-size: 15px; -fx-cursor: hand;");
        bookBtn.setOnMouseEntered(e -> bookBtn.setStyle("-fx-background-color: linear-gradient(to right, #38f9d7 0%, #43e97b 100%); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12px; -fx-padding: 12 36; -fx-font-size: 15px; -fx-cursor: hand;"));
        bookBtn.setOnMouseExited(e -> bookBtn.setStyle("-fx-background-color: linear-gradient(to right, #43e97b 0%, #38f9d7 100%); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12px; -fx-padding: 12 36; -fx-font-size: 15px; -fx-cursor: hand;"));

        // Add booking logic
        bookBtn.setOnAction(e -> {
            String doctorName = doctorCombo.getValue();
            String date = datePicker.getValue() != null ? datePicker.getValue().toString() : null;
            String time = timeCombo.getValue();
            String comments = commentsArea.getText();
            if (doctorName == null || doctorName.isEmpty() || date == null || date.isEmpty() || time == null || time.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please select doctor, date, and time.");
                alert.showAndWait();
                return;
            }
            int doctorId = getDoctorIdByName(doctorName);
            if (doctorId == -1) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Selected doctor not found.");
                alert.showAndWait();
                return;
            }
            // Check if slot is already booked
            if (isTimeSlotBooked(doctorId, date, time)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "This time slot is already booked. Please choose another.");
                alert.showAndWait();
                return;
            }
            insertAppointmentToDB(patientId, doctorId, date, time, "Pending", comments);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Booking successful! Your appointment is pending approval.");
            alert.showAndWait();
            // Optionally clear form fields
            doctorCombo.setValue(null);
            datePicker.setValue(null);
            timeCombo.setValue(null);
            commentsArea.clear();
            // Update dashboard count
            showWelcomeAndContent("Dashboard");
        });

        Label bookStatus = new Label();
        bookStatus.setStyle("-fx-text-fill: #d32f2f;");

        // Book button right-aligned
        HBox bookBtnBox = new HBox(bookBtn);
        bookBtnBox.setAlignment(Pos.CENTER_RIGHT);
        VBox.setMargin(bookBtnBox, new Insets(18, 0, 0, 0));

        // Cyan separator below Book button
        Separator cyanSeparator = new Separator();
        cyanSeparator.setStyle("-fx-background-color: #00bcd4; -fx-border-color: #00bcd4; -fx-border-width: 2px;");
        cyanSeparator.setPrefHeight(2);
        VBox.setMargin(cyanSeparator, new Insets(18, 0, 18, 0));

        VBox.setMargin(bookStatus, new Insets(0, 0, 0, 0));

        bookForm.getChildren().setAll(
            doctorLabel, doctorCombo,
            dateLabel, datePicker,
            timeLabel, timeCombo,
            commentsLabel, commentsArea,
            bookBtnBox,
            cyanSeparator,
            bookStatus
        );
        bookTab.setContent(bookForm);

        // --- My Appointments Tab ---
        myAppointmentsTab = new Tab("My Appointments");

        VBox appointmentsBox = new VBox();
        appointmentsBox.setPadding(new Insets(0));
        appointmentsBox.setStyle(
            "-fx-background-color: #e3f6fd; " +
            "-fx-border-color: #00bcd4; " +
            "-fx-border-width: 2px; " +
            "-fx-border-style: solid; " +
            "-fx-border-radius: 0;"
        );
        appointmentsBox.setPrefWidth(1200);
        appointmentsBox.setPrefHeight(600);
        appointmentsBox.setMaxWidth(Double.MAX_VALUE);
        appointmentsBox.setMaxHeight(Double.MAX_VALUE);

        // --- Status Filter ComboBox ---
        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "Pending", "Approved", "Cancelled", "Completed");
        statusFilter.setValue("All");
        statusFilter.setPrefWidth(150);

        // --- Always add columns before setting items ---
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
        dateCol.setPrefWidth(120);

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
                    String color = switch (status.toLowerCase()) {
                        case "cancelled" -> "#d32f2f";
                        case "approved" -> "#2e7d32";
                        case "pending" -> "#f57f17";
                        case "completed" -> "#1976d2";
                        default -> "#000000";
                    };
                    setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold; -fx-alignment: CENTER;");
                }
            }
        });
        statusCol.setStyle("-fx-font-weight: bold;");
        statusCol.setPrefWidth(90);

        TableColumn<Appointment, String> commentsCol = new TableColumn<>("Comments");
        commentsCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getComments()));
        commentsCol.setStyle("-fx-alignment: CENTER-LEFT; -fx-font-weight: bold;");
        commentsCol.setPrefWidth(200);

        TableColumn<Appointment, Void> cancelCol = new TableColumn<>("Cancel");
        cancelCol.setCellFactory(col -> new TableCell<>() {
            private final Button cancelBtn = new Button("Cancel");
            {
                cancelBtn.setStyle("-fx-background-color: #e57373; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px;");
                cancelBtn.setOnAction(e -> {
                    Appointment appt = getTableView().getItems().get(getIndex());
                    if (!appt.getStatus().equalsIgnoreCase("cancelled")) {
                        appt.setStatus("Cancelled");
                        System.out.println("Cancelling appointment with ID: " + appt.getAppointmentId());
                        updateAppointmentStatusInDB(appt.getAppointmentId(), "Cancelled");
                        getTableView().refresh();
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Appointment appt = getTableView().getItems().get(getIndex());
                    if (appt.getStatus().equalsIgnoreCase("cancelled")) {
                        cancelBtn.setDisable(true);
                        cancelBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px;");
                    } else {
                        cancelBtn.setDisable(false);
                        cancelBtn.setStyle("-fx-background-color: #e57373; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px;");
                    }
                    setGraphic(cancelBtn);
                }
            }
        });
        appointmentsTable = new TableView<>();
        appointmentsTable.getColumns().setAll(patientCol, doctorCol, dateCol, statusCol, commentsCol, cancelCol);

        // --- Set items after columns are added ---
        ObservableList<Appointment> appointments = fetchAppointmentsForPatient(patientId);
        appointmentsTable.setItems(appointments);

        // --- Status filter logic ---
        statusFilter.setOnAction(e -> {
            String selectedStatus = statusFilter.getValue();
            ObservableList<Appointment> filtered;
            if ("All".equals(selectedStatus)) {
                filtered = fetchAppointmentsForPatient(patientId);
            } else {
                filtered = fetchAppointmentsForPatientByStatus(patientId, selectedStatus);
            }
            appointmentsTable.setItems(filtered);
            appointmentsTable.refresh();
        });

        // --- Add filter and table to layout ---
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
        appointmentsBox.getChildren().addAll(statusFilter, tableScrollPane);
        VBox.setVgrow(tableScrollPane, Priority.ALWAYS);

        myAppointmentsTab.setContent(appointmentsBox);
        tabPane.getTabs().addAll(bookTab, myAppointmentsTab);
        mainContent.getChildren().add(tabPane);
        tabPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getWindow().showingProperty().addListener((obs2, wasShowing, isNowShowing) -> {
                    if (isNowShowing) {
                        Node tabHeader = tabPane.lookup(".tab-header-background");
                        if (tabHeader != null) {
                            tabHeader.setStyle("-fx-background-color: #00bcd4;");
                        }
                    }
                });
            }
        });
    }

    private void showVitals() {
        mainContent.getChildren().clear();

        // Title
        Label title = new Label("My Vitals Dashboard");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #0097a7; -fx-padding: 0 0 18 0;");

        // Fetch latest vitals from DB
        double heartRate = 0, oxygen = 0, temperature = 0;
        String bloodPressure = "-";
        boolean hasVitals = false;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT heart_rate, oxygen_level, blood_pressure, temperature FROM vitals WHERE patient_id = ? ORDER BY timestamp DESC LIMIT 1")) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                heartRate = rs.getDouble("heart_rate");
                oxygen = rs.getDouble("oxygen_level");
                bloodPressure = rs.getString("blood_pressure");
                temperature = rs.getDouble("temperature");
                hasVitals = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        VBox chartBox = new VBox(18);
        chartBox.setAlignment(Pos.CENTER);
        chartBox.setPadding(new Insets(8, 24, 24, 24));
        chartBox.setStyle("-fx-background-color: #e3f6fd; -fx-background-radius: 18px; -fx-effect: dropshadow(gaussian, #b2dfdb, 12, 0.15, 0, 2);");

        if (!hasVitals) {
            Label noVitals = new Label("No vitals data found. Please add your vitals in the Profile section.");
            noVitals.setStyle("-fx-font-size: 16px; -fx-text-fill: #d32f2f; -fx-font-weight: bold;");
            chartBox.getChildren().add(noVitals);
        } else {
            // Color logic and status for each vital
            String hrColor, hrStatus, hrMsg, hrIcon = "❤️";
            if (heartRate >= 60 && heartRate <= 100) {
                hrColor = "#43a047"; hrStatus = "Normal"; hrMsg = "Your heart rate is in the healthy range.";
            } else if (heartRate < 60 || heartRate > 120) {
                hrColor = "#e53935"; hrStatus = "Abnormal"; hrMsg = "Warning: Your heart rate is outside the safe range!";
            } else {
                hrColor = "#fbc02d"; hrStatus = "Borderline"; hrMsg = "Caution: Your heart rate is slightly outside normal.";
            }
            String oxygenColor, oxygenStatus, oxygenMsg, oxygenIcon = "🫁";
            if (oxygen >= 95 && oxygen <= 100) {
                oxygenColor = "#43a047"; oxygenStatus = "Normal"; oxygenMsg = "Your oxygen level is healthy.";
            } else if (oxygen < 90) {
                oxygenColor = "#e53935"; oxygenStatus = "Abnormal"; oxygenMsg = "Warning: Your oxygen level is too low!";
            } else {
                oxygenColor = "#fbc02d"; oxygenStatus = "Borderline"; oxygenMsg = "Caution: Your oxygen is slightly low.";
            }
            String tempColor, tempStatus, tempMsg, tempIcon = "🌡️";
            if (temperature >= 97 && temperature <= 99) {
                tempColor = "#43a047"; tempStatus = "Normal"; tempMsg = "Your temperature is in the healthy range.";
            } else if (temperature < 96 || temperature > 102) {
                tempColor = "#e53935"; tempStatus = "Abnormal"; tempMsg = "Warning: Your temperature is outside the safe range!";
            } else {
                tempColor = "#fbc02d"; tempStatus = "Borderline"; tempMsg = "Caution: Your temperature is slightly outside normal.";
            }

            VBox hrBar = createHorizontalBar(hrIcon, "Heart Rate", heartRate, 40, 200, hrColor, "bpm", hrStatus, hrMsg, "Normal: 60-100 bpm");
            VBox oxygenBar = createHorizontalBar(oxygenIcon, "Oxygen", oxygen, 80, 100, oxygenColor, "%", oxygenStatus, oxygenMsg, "Normal: 95-100%");
            VBox tempBar = createHorizontalBar(tempIcon, "Temperature", temperature, 90, 110, tempColor, "°F", tempStatus, tempMsg, "Normal: 97-99°F");

            VBox bars = new VBox(18, hrBar, oxygenBar, tempBar);
            bars.setAlignment(Pos.CENTER);
            bars.setPadding(new Insets(10, 0, 0, 0));

            // Blood pressure color and status
            String bpColor = "#43a047", bpStatus = "Normal", bpMsg = "Your blood pressure is healthy.", bpIcon = "🩸";
            try {
                String[] bpParts = bloodPressure.split("/");
                int sys = Integer.parseInt(bpParts[0].replaceAll("[^0-9]", ""));
                int dia = Integer.parseInt(bpParts[1].replaceAll("[^0-9]", ""));
                if (sys < 90 || sys > 140 || dia < 60 || dia > 90) {
                    bpColor = (sys < 80 || sys > 160 || dia < 50 || dia > 100) ? "#e53935" : "#fbc02d";
                    bpStatus = (bpColor.equals("#e53935")) ? "Abnormal" : "Borderline";
                    bpMsg = (bpColor.equals("#e53935")) ? "Warning: Your blood pressure is outside the safe range!" : "Caution: Your blood pressure is slightly outside normal.";
                }
            } catch (Exception ignored) {}
            Label bpLabel = new Label(bpIcon + " Blood Pressure: " + bloodPressure + " mmHg");
            bpLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + bpColor + "; -fx-padding: 18 0 0 0;");
            Label bpStatusLabel = new Label(bpStatus + " - " + bpMsg);
            bpStatusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + bpColor + ";");
            VBox bpBox = new VBox(bpLabel, bpStatusLabel);
            bpBox.setAlignment(Pos.CENTER);
            bpBox.setPadding(new Insets(10, 0, 0, 0));

            // Legend for color codes
            HBox legendBox = new HBox(18);
            legendBox.setAlignment(Pos.CENTER);
            legendBox.setPadding(new Insets(10, 0, 0, 0));
            legendBox.getChildren().addAll(
                createLegendRect("#43a047", "Normal"),
                createLegendRect("#fbc02d", "Borderline"),
                createLegendRect("#e53935", "Abnormal")
            );

            // Normal ranges
            Label ranges = new Label(
                "Normal Ranges:  Heart Rate: 60-100 bpm   |   Oxygen: 95-100%   |   Temp: 97-99°F   |   BP: 120/80 mmHg"
            );
            ranges.setStyle("-fx-font-size: 14px; -fx-text-fill: #1976d2; -fx-font-weight: bold; -fx-padding: 12 0 0 0;");

            chartBox.getChildren().setAll(bars, bpBox, legendBox, ranges);
        }

        mainContent.getChildren().setAll(title, chartBox);
    }

    // Helper for horizontal bar meter
    private VBox createHorizontalBar(String icon, String label, double value, double min, double max, String color, String unit, String status, String explanation, String tooltipText) {
        double percent = (value - min) / (max - min);
        percent = Math.max(0, Math.min(1, percent));
        ProgressBar bar = new ProgressBar(percent);
        bar.setStyle("-fx-accent: " + color + "; -fx-background-radius: 8px; -fx-pref-height: 22px;");
        bar.setPrefWidth(320);
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 28px; -fx-padding: 0 12 0 0;");
        Label valueLabel = new Label(String.format("%.1f %s", value, unit));
        valueLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        Label nameLabel = new Label(label);
        nameLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #263238; -fx-padding: 0 0 0 8;");
        Label statusLabel = new Label(status);
        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + color + "; -fx-padding: 0 0 0 8;");
        Label explanationLabel = new Label(explanation);
        explanationLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #263238; -fx-padding: 2 0 0 8;");
        Tooltip tooltip = new Tooltip(tooltipText);
        Tooltip.install(bar, tooltip);
        HBox topRow = new HBox(iconLabel, nameLabel, valueLabel, statusLabel);
        topRow.setAlignment(Pos.CENTER_LEFT);
        topRow.setSpacing(8);
        VBox box = new VBox(topRow, bar, explanationLabel);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setSpacing(4);
        box.setPrefWidth(400);
        box.setStyle("-fx-background-color: #fff; -fx-background-radius: 10px; -fx-padding: 12 18 12 18; -fx-effect: dropshadow(gaussian, #b2dfdb, 6, 0.10, 0, 1);");
        return box;
    }

    // Helper for legend
    private HBox createLegendRect(String color, String label) {
        Region rect = new Region();
        rect.setPrefSize(24, 16);
        rect.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 4px;");
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #263238; -fx-padding: 0 0 0 6;");
        HBox box = new HBox(rect, lbl);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private void showEmergency() {
        mainContent.getChildren().clear();

        VBox emergencyContainer = new VBox(30);
        emergencyContainer.setAlignment(Pos.CENTER);
        emergencyContainer.setPadding(new Insets(40, 60, 40, 60));
        emergencyContainer.setStyle("-fx-background-color: #fff5f5; -fx-background-radius: 20px; -fx-effect: dropshadow(gaussian, #ffcdd2, 20, 0.2, 0, 2);");

        // Warning icon and title
        Label warningIcon = new Label("⚠️");
        warningIcon.setStyle("-fx-font-size: 64px;");
        Label title = new Label("Emergency Alert");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #d32f2f;");
        HBox titleBox = new HBox(20, warningIcon, title);
        titleBox.setAlignment(Pos.CENTER);

        // Comments section
        VBox commentsCard = new VBox(15);
        commentsCard.setPadding(new Insets(25));
        commentsCard.setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, #bdbdbd, 10, 0.1, 0, 1);");
        
        Label commentsTitle = new Label("Additional Emergency Details (Optional)");
        commentsTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #424242;");
        
        TextArea commentsArea = new TextArea();
        commentsArea.setPromptText("You can provide additional details about your emergency situation here (optional)...");
        commentsArea.setStyle("-fx-background-color: #f5f5f5; -fx-font-size: 14px; -fx-text-fill: #424242; -fx-background-radius: 8px;");
        commentsArea.setPrefRowCount(4);
        commentsArea.setPrefWidth(400);
        
        commentsCard.getChildren().addAll(commentsTitle, commentsArea);

        // Emergency button
        Button emergencyBtn = new Button("SEND EMERGENCY ALERT");
        emergencyBtn.setStyle(
            "-fx-background-color: #d32f2f; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 10px; " +
            "-fx-padding: 15 40; " +
            "-fx-cursor: hand;"
        );
        emergencyBtn.setOnMouseEntered(e -> 
            emergencyBtn.setStyle(
                "-fx-background-color: #b71c1c; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 18px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 10px; " +
                "-fx-padding: 15 40; " +
                "-fx-cursor: hand;"
            )
        );
        emergencyBtn.setOnMouseExited(e -> 
            emergencyBtn.setStyle(
                "-fx-background-color: #d32f2f; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 18px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 10px; " +
                "-fx-padding: 15 40; " +
                "-fx-cursor: hand;"
            )
        );

        // Warning message
        Label warningMsg = new Label("⚠️ This will immediately notify all doctors and administrators");
        warningMsg.setStyle("-fx-font-size: 14px; -fx-text-fill: #d32f2f; -fx-font-weight: bold;");

        emergencyContainer.getChildren().addAll(
            titleBox,
            commentsCard,
            emergencyBtn,
            warningMsg
        );

        // Emergency button action
        emergencyBtn.setOnAction(e -> {
            String comments = commentsArea.getText().trim();
            
            // Show confirmation dialog
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Emergency Alert");
            confirmAlert.setHeaderText("Send Emergency Alert?");
            confirmAlert.setContentText("This will immediately notify all doctors and administrators. Are you sure?");
            
            if (confirmAlert.showAndWait().get() == ButtonType.OK) {
                try (Connection conn = DBConnection.getConnection()) {
                    System.out.println("Starting emergency alert process...");
                    
                    // Get patient's current vitals
                    VitalSign vitals = VitalsDAO.getVitals(patientId);
                    String vitalsInfo = "Vitals not available";
                    
                    if (vitals != null) {
                        vitalsInfo = String.format("Current Vitals - Heart Rate: %.1f, Oxygen: %.1f%%, BP: %s, Temp: %.1f°F",
                            vitals.getHeartRate(), vitals.getOxygenLevel(), 
                            vitals.getBloodPressure(), vitals.getTemperature());
                        System.out.println("Retrieved vitals: " + vitalsInfo);
                    } else {
                        System.out.println("No vitals found for patient ID: " + patientId);
                    }

                    // Insert emergency alert with vitals info
                    PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO emergency_alerts (patient_id, alert_time, comments, status, vitals_info, triggered_by) " +
                        "VALUES (?, NOW(), ?, 'Active', ?, 'BUTTON')");
                    ps.setInt(1, patientId);
                    ps.setString(2, comments);
                    ps.setString(3, vitalsInfo);
                    ps.executeUpdate();
                    System.out.println("Emergency alert inserted into database");

                    // Get assigned doctor for this patient
                    PreparedStatement ps2 = conn.prepareStatement(
                        "SELECT d.user_id, d.email FROM users d " +
                        "JOIN patient_doctor pd ON d.user_id = pd.doctor_id " +
                        "WHERE pd.patient_id = ? AND d.role = 'Doctor'");
                    ps2.setInt(1, patientId);
                    ResultSet rs = ps2.executeQuery();

                    // Prepare emergency message
                    String emergencyMessage = String.format(
                        "🚨 EMERGENCY ALERT 🚨\n" +
                        "Patient: %s\n" +
                        "Time: %s\n" +
                        "Additional Comments: %s\n" +
                        "%s",
                        getPatientName(patientId),
                        new java.sql.Timestamp(System.currentTimeMillis()),
                        comments.isEmpty() ? "None provided" : comments,
                        vitalsInfo
                    );

                    boolean notificationSent = false;
                    int doctorCount = 0;

                    // Notify assigned doctor(s)
                    while (rs.next()) {
                        doctorCount++;
                        int doctorId = rs.getInt("user_id");
                        String doctorEmail = rs.getString("email");
                        System.out.println("Attempting to notify doctor ID: " + doctorId + " at email: " + doctorEmail);
                        
                        try {
                            // Send email notification
                            Notifiable email = new EmailNotification();
                            email.send(doctorEmail, emergencyMessage);
                            
                            // Send SMS notification
                            Notifiable sms = new SMSNotification();
                            sms.send("+1234567890", emergencyMessage); // Replace with actual doctor's phone
                            
                            // Log notification
                            NotificationDAO.logNotification(doctorId, "[EMERGENCY] " + emergencyMessage);
                            notificationSent = true;
                            System.out.println("Successfully notified doctor ID: " + doctorId);
                        } catch (Exception ex) {
                            System.err.println("Failed to send notification to doctor " + doctorId + ": " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }

                    System.out.println("Attempted to notify " + doctorCount + " doctors");

                    // Notify all admins if no doctors were notified
                    if (!notificationSent) {
                        System.out.println("No doctors were notified, attempting to notify admins...");
                        PreparedStatement ps3 = conn.prepareStatement(
                            "SELECT email FROM users WHERE role = 'Admin'");
                        ResultSet adminRs = ps3.executeQuery();
                        
                        int adminCount = 0;
                        while (adminRs.next()) {
                            adminCount++;
                            String adminEmail = adminRs.getString("email");
                            System.out.println("Attempting to notify admin at email: " + adminEmail);
                            try {
                                Notifiable email = new EmailNotification();
                                email.send(adminEmail, emergencyMessage);
                                
                                Notifiable sms = new SMSNotification();
                                sms.send("+1234567890", emergencyMessage);
                                notificationSent = true;
                                System.out.println("Successfully notified admin at: " + adminEmail);
                            } catch (Exception ex) {
                                System.err.println("Failed to send notification to admin: " + ex.getMessage());
                                ex.printStackTrace();
                            }
                        }
                        System.out.println("Attempted to notify " + adminCount + " admins");
                    }

                    if (notificationSent) {
                        // Show success message
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Success");
                        successAlert.setHeaderText("Emergency Alert Sent");
                        successAlert.setContentText("Doctors and administrators have been notified. Help is on the way!");
                        successAlert.showAndWait();
                        commentsArea.clear();
                    } else {
                        throw new Exception("Failed to send notifications to any doctors or administrators");
                    }
                } catch (Exception ex) {
                    System.err.println("Error in emergency alert process: " + ex.getMessage());
                    ex.printStackTrace();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("Failed to Send Alert");
                    errorAlert.setContentText("There was an error sending the emergency alert. Please try again or contact emergency services directly.\n\nError: " + ex.getMessage());
                    errorAlert.showAndWait();
                }
            }
        });

        mainContent.getChildren().add(emergencyContainer);
    }

    private String getPatientName(int patientId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT name FROM users WHERE user_id = ?")) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown Patient";
    }

    private void showMedicalRecord() {
        mainContent.getChildren().clear();

        VBox medicalRecordContainer = new VBox(24);
        medicalRecordContainer.setAlignment(Pos.CENTER);
        medicalRecordContainer.setPadding(new Insets(40, 60, 40, 60));
        medicalRecordContainer.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 20px; -fx-effect: dropshadow(gaussian, #e0e0e0, 20, 0.2, 0, 2);");

        // Title
        Label title = new Label("Medical Records");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #1976d2;");

        // File Upload Section (reduced height)
        VBox fileUploadBox = new VBox(10);
        fileUploadBox.setPadding(new Insets(18, 18, 18, 18)); // reduced padding
        fileUploadBox.setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, #bdbdbd, 10, 0.1, 0, 1);");
        fileUploadBox.setPrefWidth(800);
        fileUploadBox.setMaxWidth(900);

        Label fileUploadTitle = new Label("Upload Medical Report");
        fileUploadTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #424242;");

        Label fileUploadInfo = new Label("Upload your medical report or document (Word, PDF, or image files)");
        fileUploadInfo.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");

        Button uploadFileBtn = new Button("Choose File");
        uploadFileBtn.setStyle(
            "-fx-background-color: #1976d2; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8px; " +
            "-fx-padding: 10 20; " +
            "-fx-cursor: hand;"
        );
        uploadFileBtn.setOnMouseEntered(e -> 
            uploadFileBtn.setStyle(
                "-fx-background-color: #1565c0; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 8px; " +
                "-fx-padding: 10 20; " +
                "-fx-cursor: hand;"
            )
        );
        uploadFileBtn.setOnMouseExited(e -> 
            uploadFileBtn.setStyle(
                "-fx-background-color: #1976d2; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 8px; " +
                "-fx-padding: 10 20; " +
                "-fx-cursor: hand;"
            )
        );

        Label selectedFileLabel = new Label("No file selected");
        selectedFileLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");

        // Comments Section (smaller)
        VBox commentsBox = new VBox(10);
        commentsBox.setPadding(new Insets(18, 18, 18, 18));
        commentsBox.setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, #bdbdbd, 10, 0.1, 0, 1);");
        commentsBox.setPrefWidth(400);
        commentsBox.setMaxWidth(450);

        Label commentsTitle = new Label("Additional Comments");
        commentsTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #424242;");

        Label commentsInfo = new Label("Please provide additional details about your medical record");
        commentsInfo.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");

        TextArea commentsArea = new TextArea();
        commentsArea.setPromptText("Enter your comments here...");
        commentsArea.setStyle("-fx-background-color: #f5f5f5; -fx-font-size: 16px; -fx-text-fill: #424242; -fx-background-radius: 8px;");
        commentsArea.setPrefRowCount(5); // reduced height
        commentsArea.setPrefWidth(380);   // reduced width
        commentsArea.setMinHeight(80);    // ensure smaller height
        commentsArea.setMaxWidth(450);

        // Submit Button
        Button submitBtn = new Button("Submit Medical Record");
        submitBtn.setStyle(
            "-fx-background-color: #43a047; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 10px; " +
            "-fx-padding: 12 30; " +
            "-fx-cursor: hand;"
        );
        submitBtn.setOnMouseEntered(e -> 
            submitBtn.setStyle(
                "-fx-background-color: #388e3c; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 10px; " +
                "-fx-padding: 12 30; " +
                "-fx-cursor: hand;"
            )
        );
        submitBtn.setOnMouseExited(e -> 
            submitBtn.setStyle(
                "-fx-background-color: #43a047; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 10px; " +
                "-fx-padding: 12 30; " +
                "-fx-cursor: hand;"
            )
        );

        // Status Label
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Add components to their containers
        fileUploadBox.getChildren().setAll(fileUploadTitle, fileUploadInfo, uploadFileBtn, selectedFileLabel);
        commentsBox.getChildren().setAll(commentsTitle, commentsInfo, commentsArea);

        medicalRecordContainer.getChildren().setAll(
            title,
            fileUploadBox,
            commentsBox,
            submitBtn,
            statusLabel
        );

        // File Upload Handling
        uploadFileBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Medical Report");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Word Documents", "*.docx", "*.doc"),
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
            );
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                selectedFileLabel.setText("Selected: " + selectedFile.getName());
                selectedFileLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #1976d2;");
            }
        });

        // Submit Button Handling
        submitBtn.setOnAction(e -> {
            String comments = commentsArea.getText().trim();
            String selectedFile = selectedFileLabel.getText();
            if (selectedFile.equals("No file selected") && comments.isEmpty()) {
                statusLabel.setText("Error: Please either upload a file or provide comments");
                statusLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 14px; -fx-font-weight: bold;");
                return;
            }
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO medical_records (patient_id, record_date, comments, file_path) VALUES (?, NOW(), ?, ?)")) {
                ps.setInt(1, patientId);
                ps.setString(2, comments);
                ps.setString(3, selectedFile.equals("No file selected") ? null : selectedFile);
                ps.executeUpdate();
                statusLabel.setText("Medical record submitted successfully!");
                statusLabel.setStyle("-fx-text-fill: #43a047; -fx-font-size: 14px; -fx-font-weight: bold;");
                selectedFileLabel.setText("No file selected");
                selectedFileLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");
                commentsArea.clear();
            } catch (Exception ex) {
                ex.printStackTrace();
                statusLabel.setText("Error submitting medical record: " + ex.getMessage());
                statusLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 14px; -fx-font-weight: bold;");
            }
        });

        mainContent.getChildren().add(medicalRecordContainer);
    }

    private void showPrescription() {
        mainContent.getChildren().clear();

        VBox prescriptionContainer = new VBox(24);
        prescriptionContainer.setAlignment(Pos.TOP_CENTER);
        prescriptionContainer.setPadding(new Insets(40, 60, 40, 60));
        prescriptionContainer.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 20px; -fx-effect: dropshadow(gaussian, #e0e0e0, 20, 0.2, 0, 2);");

        Label title = new Label("My Prescriptions");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #1976d2; -fx-padding: 0 0 18 0;");
        prescriptionContainer.getChildren().add(title);

        // Create a horizontal container for prescription cards
        HBox cardsContainer = new HBox(20); // 20 pixels spacing between cards
        cardsContainer.setPadding(new Insets(10));
        cardsContainer.setAlignment(Pos.CENTER_LEFT);

        // Create a ScrollPane for horizontal scrolling
        ScrollPane scrollPane = new ScrollPane(cardsContainer);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPrefViewportWidth(800); // Set a fixed width for the viewport

        // Fetch prescriptions from DB
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT p.id, p.timestamp, d.name AS doctor_name, p.prescription " +
                "FROM prescriptions p JOIN users d ON p.doctor_id = d.user_id " +
                "WHERE p.patient_id = ? ORDER BY p.timestamp DESC")) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            boolean hasPrescription = false;
            while (rs.next()) {
                hasPrescription = true;
                VBox card = new VBox(12);
                card.setPadding(new Insets(24));
                card.setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, #bdbdbd, 10, 0.1, 0, 1);");
                card.setMinWidth(300); // Set minimum width for each card
                card.setMaxWidth(300); // Set maximum width for each card

                Label dateLabel = new Label("Date: " + rs.getString("timestamp"));
                dateLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #0097a7;");
                Label doctorLabel = new Label("Prescribed by: Dr. " + rs.getString("doctor_name"));
                doctorLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #0097a7;");

                String prescriptionText = rs.getString("prescription");
                Label prescriptionLabel = new Label("Prescription: " + (prescriptionText == null || prescriptionText.isEmpty() ? "-" : prescriptionText));
                prescriptionLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #263238; -fx-background-color: #fffde7; -fx-background-radius: 8px; -fx-padding: 8 12 8 12; -fx-margin: 8 0 0 0;");
                prescriptionLabel.setWrapText(true); // Enable text wrapping

                card.getChildren().addAll(dateLabel, doctorLabel, prescriptionLabel);
                cardsContainer.getChildren().add(card);
            }
            if (!hasPrescription) {
                Label noPresc = new Label("No prescriptions found. Your doctor will update this section when needed.");
                noPresc.setStyle("-fx-font-size: 16px; -fx-text-fill: #d32f2f; -fx-font-weight: bold;");
                cardsContainer.getChildren().add(noPresc);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Label error = new Label("Error loading prescriptions: " + e.getMessage());
            error.setStyle("-fx-font-size: 16px; -fx-text-fill: #d32f2f; -fx-font-weight: bold;");
            cardsContainer.getChildren().add(error);
        }

        prescriptionContainer.getChildren().add(scrollPane);
        mainContent.getChildren().add(prescriptionContainer);
    }

    private LineChart<String, Number> createVitalsChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Timestamp");
        yAxis.setLabel("Heart Rate (bpm)");
        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Heart Rate Trend");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Heart Rate");
        VitalSign v = VitalsDAO.getVitals(patientId);
        if (v != null) {
            series.getData().add(new XYChart.Data<>("Now", v.getHeartRate()));
        }
        chart.getData().add(series);
        return chart;
    }

    private ObservableList<String> fetchDoctorNamesFromDB() {
        ObservableList<String> doctorNames = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT name FROM users WHERE role = 'doctor'")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                doctorNames.add(rs.getString("name"));
            }
        } catch (Exception e) {
            System.out.println("Error fetching doctor names: " + e.getMessage());
        }
        return doctorNames;
    }

    private void insertAppointmentToDB(int patientId, int doctorId, String date, String time, String status, String comments) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO appointments (patient_id, doctor_id, date, status, comments) VALUES (?, ?, ?, ?, ?)"
             )) {
            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            // Combine date and time into a single DATETIME string
            String dateTime = date + " " + time;
            ps.setString(3, dateTime);
            ps.setString(4, status);
            ps.setString(5, comments);
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                // Refresh the appointments table
                ObservableList<Appointment> appointments = fetchAppointmentsForPatient(patientId);
                VBox appointmentsBox = (VBox) myAppointmentsTab.getContent();
                ScrollPane scrollPane = (ScrollPane) appointmentsBox.getChildren().get(0);
                TableView<Appointment> appointmentsTable = (TableView<Appointment>) scrollPane.getContent();
                appointmentsTable.setItems(appointments);
                appointmentsTable.refresh();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error inserting appointment: " + e.getMessage());
        }
    }

    // Add this method to fetch appointments for the current patient from the database
    private ObservableList<Appointment> fetchAppointmentsForPatient(int patientId) {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT a.appointment_id, a.date, a.status, a.comments, u.name as doctor_name, p.name as patient_name " +
                 "FROM appointments a " +
                 "JOIN users u ON a.doctor_id = u.user_id " +
                 "JOIN users p ON a.patient_id = p.user_id " +
                 "WHERE a.patient_id = ? " +
                 "ORDER BY a.date DESC"
             )) {
            ps.setInt(1, patientId);
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

    // Helper to get doctorId by name
    private int getDoctorIdByName(String doctorName) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT user_id FROM users WHERE name = ? AND role = 'doctor'")) {
            ps.setString(1, doctorName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Not found
    }

    private boolean isTimeSlotBooked(int doctorId, String date, String time) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND date = ? AND time = ? AND status != 'cancelled'"
             )) {
            ps.setInt(1, doctorId);
            ps.setString(2, date);
            ps.setString(3, time);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error checking time slot: " + e.getMessage());
        }
        return false;
    }

    private ObservableList<Appointment> fetchAppointmentsForPatientByStatus(int patientId, String status) {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT a.appointment_id, a.date, a.time, a.status, a.comments, u.name as doctor_name, p.name as patient_name " +
                 "FROM appointments a " +
                 "JOIN users u ON a.doctor_id = u.user_id " +
                 "JOIN users p ON a.patient_id = p.user_id " +
                 "WHERE a.patient_id = ? AND a.status = ? " +
                 "ORDER BY a.date DESC, a.time DESC"
             )) {
            ps.setInt(1, patientId);
            ps.setString(2, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String date = rs.getString("date");
                String time = rs.getString("time");
                LocalDateTime dateTime = LocalDateTime.parse(date + " " + time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                appointments.add(new Appointment(
                    rs.getInt("appointment_id"),
                    rs.getString("doctor_name"),
                    dateTime,
                    rs.getString("status"),
                    rs.getString("comments"),
                    rs.getString("patient_name")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appointments;
    }

    private void updateAppointmentStatusInDB(int appointmentId, String status) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "UPDATE appointments SET status = ? WHERE appointment_id = ?"
             )) {
            ps.setString(1, status);
            ps.setInt(2, appointmentId);
            int rows = ps.executeUpdate();
            System.out.println("Updated rows: " + rows + " for appointment_id: " + appointmentId + " to status: " + status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProfile() {
        mainContent.getChildren().clear();

        // Fetch user info from DB
        String name = "", gender = "", address = "", email = "", bloodGroup = "";
        String phone = "", emergencyContact = "", medicalHistory = "", allergies = "";
        int age = 0;
        boolean vitalsExist = false;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps1 = conn.prepareStatement("SELECT * FROM users WHERE user_id = ?");
             PreparedStatement ps2 = conn.prepareStatement("SELECT COUNT(*) FROM vitals WHERE patient_id = ?")) {

            ps1.setInt(1, patientId);
            ResultSet rs = ps1.executeQuery();
            if (rs.next()) {
                name = rs.getString("name");
                gender = rs.getString("gender");
                address = rs.getString("address");
                email = rs.getString("email");
                age = rs.getInt("age");
                bloodGroup = rs.getString("blood_group");
                phone = rs.getString("phone");
                emergencyContact = rs.getString("emergency_contact");
                medicalHistory = rs.getString("medical_history");
                allergies = rs.getString("allergies");
            }

            ps2.setInt(1, patientId);
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next() && rs2.getInt(1) > 0) {
                vitalsExist = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create TabPane
        TabPane tabPane = new TabPane();
        tabPane.setStyle(
            "-fx-tab-min-width: 160px;" +
            "-fx-tab-max-width: 200px;" +
            "-fx-background-color: #00bcd4;"
        );
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // --- Profile Tab ---
        Tab profileTab = new Tab("Personal Information");
        VBox profileBox = new VBox(20);
        profileBox.setPadding(new Insets(40, 48, 40, 48));
        profileBox.setStyle("-fx-background-color: #e3f6fd; -fx-effect: dropshadow(gaussian, #b2dfdb, 12, 0.15, 0, 2); -fx-border-color: #00bcd4; -fx-border-width: 2px; -fx-border-style: solid; -fx-border-radius: 0;");

        // UI fields with styling
        TextField nameField = new TextField(name);
        TextField ageField = new TextField(String.valueOf(age));
        ComboBox<String> genderBox = new ComboBox<>();
        genderBox.getItems().addAll("Male", "Female", "Other");
        genderBox.setValue(gender);
        TextField addressField = new TextField(address);
        TextField emailField = new TextField(email);
        TextField bloodGroupField = new TextField(bloodGroup);
        TextField phoneField = new TextField(phone);
        TextField emergencyContactField = new TextField(emergencyContact);
        TextArea medicalHistoryArea = new TextArea(medicalHistory);
        TextArea allergiesArea = new TextArea(allergies);

        // Style all text fields
        String fieldStyle = "-fx-background-color: white; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-border-color: #b2ebf2; -fx-padding: 8px;";
        nameField.setStyle(fieldStyle);
        ageField.setStyle(fieldStyle);
        genderBox.setStyle(fieldStyle);
        addressField.setStyle(fieldStyle);
        emailField.setStyle(fieldStyle);
        bloodGroupField.setStyle(fieldStyle);
        phoneField.setStyle(fieldStyle);
        emergencyContactField.setStyle(fieldStyle);

        // Style text areas
        String textAreaStyle = fieldStyle + " -fx-pref-height: 100px;";
        medicalHistoryArea.setStyle(textAreaStyle);
        allergiesArea.setStyle(textAreaStyle);

        // Style labels
        String labelStyle = "-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #0097a7;";

        // Save Button with styling
        Button saveBtn = new Button("Save Changes");
        saveBtn.setStyle("-fx-background-color: linear-gradient(to right, #43e97b 0%, #38f9d7 100%); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12px; -fx-padding: 12 36; -fx-font-size: 15px; -fx-cursor: hand;");
        saveBtn.setOnMouseEntered(e -> saveBtn.setStyle("-fx-background-color: linear-gradient(to right, #38f9d7 0%, #43e97b 100%); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12px; -fx-padding: 12 36; -fx-font-size: 15px; -fx-cursor: hand;"));
        saveBtn.setOnMouseExited(e -> saveBtn.setStyle("-fx-background-color: linear-gradient(to right, #43e97b 0%, #38f9d7 100%); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12px; -fx-padding: 12 36; -fx-font-size: 15px; -fx-cursor: hand;"));

        // Create content for profile box
        VBox profileContent = new VBox(20);
        profileContent.getChildren().addAll(
            new Label("Personal Information") {{
                setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0097a7; -fx-padding: 0 0 20 0;");
            }},
            new Label("Name:") {{
                setStyle(labelStyle);
            }}, nameField,
            new Label("Age:") {{
                setStyle(labelStyle);
            }}, ageField,
            new Label("Gender:") {{
                setStyle(labelStyle);
            }}, genderBox,
            new Label("Blood Group:") {{
                setStyle(labelStyle);
            }}, bloodGroupField,
            new Label("Phone Number:") {{
                setStyle(labelStyle);
            }}, phoneField,
            new Label("Email:") {{
                setStyle(labelStyle);
            }}, emailField,
            new Label("Address:") {{
                setStyle(labelStyle);
            }}, addressField,
            new Label("Emergency Contact:") {{
                setStyle(labelStyle);
            }}, emergencyContactField,
            new Label("Medical History:") {{
                setStyle(labelStyle);
            }}, medicalHistoryArea,
            new Label("Allergies:") {{
                setStyle(labelStyle);
            }}, allergiesArea,
            saveBtn
        );

        // Create ScrollPane for profile content
        ScrollPane scrollPane = new ScrollPane(profileContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        // Add scroll pane to profile box
        profileBox.getChildren().add(scrollPane);

        // --- Vitals Tab ---
        Tab vitalsTab = new Tab("Vital Signs");
        VBox vitalsBox = new VBox(20);
        vitalsBox.setPadding(new Insets(40, 48, 40, 48));
        vitalsBox.setStyle("-fx-background-color: #e3f6fd; -fx-effect: dropshadow(gaussian, #b2dfdb, 12, 0.15, 0, 2); -fx-border-color: #00bcd4; -fx-border-width: 2px; -fx-border-style: solid; -fx-border-radius: 0;");

        TextField heartRateField = new TextField();
        TextField bpField = new TextField();
        TextField oxygenField = new TextField();
        TextField tempField = new TextField();
        Button addVitalsBtn = new Button("Add Vitals");

        // Style vitals fields
        heartRateField.setStyle(fieldStyle);
        bpField.setStyle(fieldStyle);
        oxygenField.setStyle(fieldStyle);
        tempField.setStyle(fieldStyle);

        // Style add vitals button
        addVitalsBtn.setStyle("-fx-background-color: linear-gradient(to right, #43e97b 0%, #38f9d7 100%); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12px; -fx-padding: 12 36; -fx-font-size: 15px; -fx-cursor: hand;");
        addVitalsBtn.setOnMouseEntered(e -> addVitalsBtn.setStyle("-fx-background-color: linear-gradient(to right, #38f9d7 0%, #43e97b 100%); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12px; -fx-padding: 12 36; -fx-font-size: 15px; -fx-cursor: hand;"));
        addVitalsBtn.setOnMouseExited(e -> addVitalsBtn.setStyle("-fx-background-color: linear-gradient(to right, #43e97b 0%, #38f9d7 100%); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12px; -fx-padding: 12 36; -fx-font-size: 15px; -fx-cursor: hand;"));

        // Check if vitals already exist for this patient (enforce one-time entry)
        if (vitalsExist) {
            heartRateField.setDisable(true);
            bpField.setDisable(true);
            oxygenField.setDisable(true);
            tempField.setDisable(true);
            addVitalsBtn.setDisable(true);
            addVitalsBtn.setText("Vitals Already Added");
            addVitalsBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12px; -fx-padding: 12 36; -fx-font-size: 15px;");
        }

        // Add fields to vitals box with styled labels
        vitalsBox.getChildren().addAll(
            new Label("Vital Signs") {{
                setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0097a7; -fx-padding: 0 0 20 0;");
            }},
            new Label("Heart Rate (bpm):") {{
                setStyle(labelStyle);
            }}, heartRateField,
            new Label("Blood Pressure (mmHg):") {{
                setStyle(labelStyle);
            }}, bpField,
            new Label("Oxygen Level (%):") {{
                setStyle(labelStyle);
            }}, oxygenField,
            new Label("Temperature (°F):") {{
                setStyle(labelStyle);
            }}, tempField,
            addVitalsBtn
        );

        // Set tab contents
        profileTab.setContent(profileBox);
        vitalsTab.setContent(vitalsBox);
        tabPane.getTabs().addAll(profileTab, vitalsTab);

        // Add tab pane to main content
        mainContent.getChildren().add(tabPane);

        // Save changes to DB
        saveBtn.setOnAction(e -> {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "UPDATE users SET name=?, age=?, gender=?, address=?, email=?, blood_group=?, phone=?, emergency_contact=?, medical_history=?, allergies=? WHERE user_id=?"
                 )) {
                ps.setString(1, nameField.getText());
                ps.setInt(2, Integer.parseInt(ageField.getText()));
                ps.setString(3, genderBox.getValue());
                ps.setString(4, addressField.getText());
                ps.setString(5, emailField.getText());
                ps.setString(6, bloodGroupField.getText());
                ps.setString(7, phoneField.getText());
                ps.setString(8, emergencyContactField.getText());
                ps.setString(9, medicalHistoryArea.getText());
                ps.setString(10, allergiesArea.getText());
                ps.setInt(11, patientId);
                ps.executeUpdate();
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Profile updated successfully!");
                alert.showAndWait();
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error updating profile: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        // Add vitals to DB (INSERT ONLY, one-time entry enforced)
        addVitalsBtn.setOnAction(e -> {
            try {
                // Validate input
                if (heartRateField.getText().isEmpty() || bpField.getText().isEmpty() || 
                    oxygenField.getText().isEmpty() || tempField.getText().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill in all vitals fields");
                    alert.showAndWait();
                    return;
                }

                // Parse and validate numeric values
                double heartRate = Double.parseDouble(heartRateField.getText());
                double oxygenLevel = Double.parseDouble(oxygenField.getText());
                double temperature = Double.parseDouble(tempField.getText());
                String bloodPressure = bpField.getText();

                // Validate ranges
                if (heartRate < 40 || heartRate > 200) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Heart rate must be between 40 and 200 bpm");
                    alert.showAndWait();
                    return;
                }
                if (oxygenLevel < 0 || oxygenLevel > 100) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Oxygen level must be between 0 and 100%");
                    alert.showAndWait();
                    return;
                }
                if (temperature < 95 || temperature > 105) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Temperature must be between 95 and 105°F");
                    alert.showAndWait();
                    return;
                }

                // Check again in DB to prevent race condition (double safety)
                boolean alreadyExists = false;
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM vitals WHERE patient_id = ?")) {
                    ps.setInt(1, patientId);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        alreadyExists = true;
                    }
                }
                if (alreadyExists) {
                    heartRateField.setDisable(true);
                    bpField.setDisable(true);
                    oxygenField.setDisable(true);
                    tempField.setDisable(true);
                    addVitalsBtn.setDisable(true);
                    addVitalsBtn.setText("Vitals Already Added");
                    addVitalsBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12px; -fx-padding: 12 36; -fx-font-size: 15px;");
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Vitals already exist and cannot be changed.");
                    alert.showAndWait();
                    return;
                }

                // Insert into database (INSERT ONLY)
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO vitals (patient_id, heart_rate, oxygen_level, blood_pressure, temperature) VALUES (?, ?, ?, ?, ?)")) {
                    ps.setInt(1, patientId);
                    ps.setDouble(2, heartRate);
                    ps.setDouble(3, oxygenLevel);
                    ps.setString(4, bloodPressure);
                    ps.setDouble(5, temperature);
                    ps.executeUpdate();
                }

                // Disable fields after successful insertion
                heartRateField.setDisable(true);
                bpField.setDisable(true);
                oxygenField.setDisable(true);
                tempField.setDisable(true);
                addVitalsBtn.setDisable(true);
                addVitalsBtn.setText("Vitals Already Added");
                addVitalsBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12px; -fx-padding: 12 36; -fx-font-size: 15px;");

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Vitals added successfully! You cannot change them anymore.");
                alert.showAndWait();
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter valid numeric values for heart rate, oxygen level, and temperature");
                alert.showAndWait();
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error adding vitals: " + ex.getMessage());
                alert.showAndWait();
            }
        });
    }

    private void showConversation() {
        mainContent.getChildren().clear();
        VBox convoBox = new VBox(30);
        convoBox.setAlignment(Pos.CENTER);
        convoBox.setPadding(new Insets(60, 0, 0, 0));

        Label title = new Label("Start a Conversation");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #0097a7; -fx-padding: 0 0 18 0;");

        Button chatBtn = new Button("Chat");
        chatBtn.setStyle("-fx-background-color: #43a047; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 10px; -fx-padding: 16 48;");
        chatBtn.setOnMouseEntered(ev -> chatBtn.setStyle("-fx-background-color: #388e3c; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 10px; -fx-padding: 16 48;"));
        chatBtn.setOnMouseExited(ev -> chatBtn.setStyle("-fx-background-color: #43a047; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 10px; -fx-padding: 16 48;"));
        chatBtn.setOnAction(ev -> {
            showChatWindow();
        });

        Button videoBtn = new Button("Video Call");
        videoBtn.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 10px; -fx-padding: 16 48;");
        videoBtn.setOnMouseEntered(ev -> videoBtn.setStyle("-fx-background-color: #1565c0; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 10px; -fx-padding: 16 48;"));
        videoBtn.setOnMouseExited(ev -> videoBtn.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 10px; -fx-padding: 16 48;"));
        videoBtn.setOnAction(ev -> {
            ChatAndVedioConsultation.VideoCall.startCall(patientId);
        });

        HBox btnBox = new HBox(40, chatBtn, videoBtn);
        btnBox.setAlignment(Pos.CENTER);

        // Add a card-like frame around the buttons
        VBox card = new VBox(24, btnBox);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(36, 48, 36, 48));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 18px; -fx-border-radius: 18px; -fx-border-color: #00bcd4; -fx-border-width: 2px; -fx-effect: dropshadow(gaussian, #b2dfdb, 16, 0.18, 0, 2);");

        convoBox.getChildren().addAll(title, card);
        mainContent.getChildren().add(convoBox);
    }

    private void showChatWindow() {
        Stage chatStage = new Stage();
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        
        // Chat area
        ListView<ChatMessage> chatListView = new ListView<>();
        ObservableList<ChatMessage> chatMessages = FXCollections.observableArrayList();
        chatListView.setItems(chatMessages);
        chatListView.setPrefHeight(300);
        chatListView.setStyle("-fx-background-color: transparent;");
        
        // Custom cell factory for chat bubbles
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
                    
                    if (msg.getSender().equals("patient")) {
                        hbox.setAlignment(Pos.CENTER_RIGHT);
                        bubble.setStyle(bubble.getStyle() + "-fx-background-color: #dcf8c6; -fx-text-fill: #263238;");
                    } else if (msg.getSender().equals("doctor")) {
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

        // Input area
        TextField inputField = new TextField();
        inputField.setPromptText("Type your message...");
        inputField.setStyle("-fx-font-size: 14px; -fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: #b2dfdb; -fx-border-radius: 8px; -fx-padding: 8px;");
        
        Button sendBtn = new Button("Send");
        sendBtn.setStyle("-fx-background-color: #0097a7; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 16;");
        
        HBox inputBox = new HBox(10, inputField, sendBtn);
        inputBox.setAlignment(Pos.CENTER_LEFT);

        // Doctor selection
        ComboBox<String> doctorComboBox = new ComboBox<>();
        doctorComboBox.setPromptText("Select a doctor");
        doctorComboBox.setStyle("-fx-font-size: 14px; -fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: #b2dfdb; -fx-border-radius: 8px; -fx-padding: 8px;");
        
        // Load approved doctors
        try (Connection conn = DatabaseConnector.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT DISTINCT d.name FROM users d " +
                 "JOIN appointments a ON d.user_id = a.doctor_id " +
                 "WHERE a.patient_id = ? AND a.status = 'Approved'"
             )) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                doctorComboBox.getItems().add(rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Map doctor name to doctorId
        java.util.Map<String, Integer> doctorNameToId = new java.util.HashMap<>();
        try (Connection conn = DatabaseConnector.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT user_id, name FROM users WHERE role = 'Doctor'")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                doctorNameToId.put(rs.getString("name"), rs.getInt("user_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Load chat history when doctor is selected
        doctorComboBox.setOnAction(e -> {
            String selectedDoctor = doctorComboBox.getValue();
            if (selectedDoctor != null) {
                chatMessages.clear();
                Integer doctorId = doctorNameToId.get(selectedDoctor);
                if (doctorId != null) {
                    ObservableList<DatabaseConnector.DBConnection.ChatMessage> dbMsgs = 
                        DatabaseConnector.DBConnection.fetchChatMessages(doctorId, patientId);
                    for (DatabaseConnector.DBConnection.ChatMessage dbMsg : dbMsgs) {
                        chatMessages.add(new ChatMessage(dbMsg.getSender(), dbMsg.getMessage(), dbMsg.getTimestamp()));
                    }
                }
                chatMessages.add(new ChatMessage("system", "Chat with " + selectedDoctor + " started.", 
                    java.time.LocalDateTime.now().toString()));
                chatListView.scrollTo(chatMessages.size() - 1);
            }
        });

        // Send button functionality
        sendBtn.setOnAction(e -> {
            String message = inputField.getText().trim();
            if (!message.isEmpty() && doctorComboBox.getValue() != null) {
                Integer doctorId = doctorNameToId.get(doctorComboBox.getValue());
                if (doctorId != null) {
                    // Save message to database
                    DatabaseConnector.DBConnection.insertChatMessage(
                        doctorId,
                        patientId,
                        "patient",
                        message,
                        java.time.LocalDateTime.now().toString()
                    );
                    
                    // Add message to chat view
                    chatMessages.add(new ChatMessage("patient", message, java.time.LocalDateTime.now().toString()));
                    inputField.clear();
                    chatListView.scrollTo(chatMessages.size() - 1);
                }
            }
        });

        // Add enter key support for sending messages
        inputField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                sendBtn.fire();
            }
        });

        root.getChildren().addAll(doctorComboBox, chatListView, inputBox);
        chatStage.setScene(new Scene(root, 500, 500));
        chatStage.setTitle("Chat");
        chatStage.show();
    }

    // Chat message class
    private static class ChatMessage {
        private final String sender;
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

    private void showUploadVitals() {
        mainContent.getChildren().clear();

        VBox uploadContainer = new VBox(30);
        uploadContainer.setAlignment(Pos.CENTER);
        uploadContainer.setPadding(new Insets(40, 60, 40, 60));
        uploadContainer.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 20px; -fx-effect: dropshadow(gaussian, #e0e0e0, 20, 0.2, 0, 2);");

        // Title
        Label title = new Label("Vitals Data Management");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #1976d2;");

        // Instructions
        VBox instructionsBox = new VBox(15);
        instructionsBox.setPadding(new Insets(25));
        instructionsBox.setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, #bdbdbd, 10, 0.1, 0, 1);");
        
        Label instructionsTitle = new Label("Instructions");
        instructionsTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #424242;");
        
        TextArea instructionsText = new TextArea(
            "CSV File Format:\n\n" +
            "timestamp,heart_rate,blood_pressure,oxygen_level,temperature\n" +
            "2024-03-20 10:00:00,75,120/80,98,98.6\n\n" +
            "Notes:\n" +
            "• timestamp: Date and time of the reading (YYYY-MM-DD HH:MM:SS)\n" +
            "• heart_rate: Heart rate in beats per minute\n" +
            "• blood_pressure: Systolic/Diastolic in mmHg\n" +
            "• oxygen_level: Oxygen saturation percentage\n" +
            "• temperature: Body temperature in Fahrenheit"
        );
        instructionsText.setEditable(false);
        instructionsText.setStyle("-fx-background-color: #f5f5f5; -fx-font-size: 14px; -fx-text-fill: #424242; -fx-background-radius: 8px;");
        instructionsText.setPrefRowCount(10);
        instructionsText.setPrefWidth(600);
        
        instructionsBox.getChildren().addAll(instructionsTitle, instructionsText);

        // Buttons container
        HBox buttonsBox = new HBox(20);
        buttonsBox.setAlignment(Pos.CENTER);

        // Upload button
        Button uploadBtn = new Button("Upload CSV");
        uploadBtn.setStyle(
            "-fx-background-color: #1976d2; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 10px; " +
            "-fx-padding: 12 30; " +
            "-fx-cursor: hand;"
        );
        uploadBtn.setOnMouseEntered(e -> 
            uploadBtn.setStyle(
                "-fx-background-color: #1565c0; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 10px; " +
                "-fx-padding: 12 30; " +
                "-fx-cursor: hand;"
            )
        );
        uploadBtn.setOnMouseExited(e -> 
            uploadBtn.setStyle(
                "-fx-background-color: #1976d2; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 10px; " +
                "-fx-padding: 12 30; " +
                "-fx-cursor: hand;"
            )
        );

        // Export button
        Button exportBtn = new Button("Export CSV");
        exportBtn.setStyle(
            "-fx-background-color: #43a047; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 10px; " +
            "-fx-padding: 12 30; " +
            "-fx-cursor: hand;"
        );
        exportBtn.setOnMouseEntered(e -> 
            exportBtn.setStyle(
                "-fx-background-color: #388e3c; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 10px; " +
                "-fx-padding: 12 30; " +
                "-fx-cursor: hand;"
            )
        );
        exportBtn.setOnMouseExited(e -> 
            exportBtn.setStyle(
                "-fx-background-color: #43a047; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 10px; " +
                "-fx-padding: 12 30; " +
                "-fx-cursor: hand;"
            )
        );

        buttonsBox.getChildren().addAll(uploadBtn, exportBtn);

        // Status label
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        uploadContainer.getChildren().addAll(title, instructionsBox, buttonsBox, statusLabel);

        // File upload handling
        uploadBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Vitals CSV File");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
            );
            
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                try {
                    processCSVFile(selectedFile);
                    statusLabel.setText("File uploaded successfully!");
                    statusLabel.setStyle("-fx-text-fill: #43a047; -fx-font-size: 14px; -fx-font-weight: bold;");
                } catch (Exception ex) {
                    statusLabel.setText("Error processing file: " + ex.getMessage());
                    statusLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 14px; -fx-font-weight: bold;");
                }
            }
        });

        // Export handling
        exportBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Vitals CSV File");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
            );
            fileChooser.setInitialFileName("vitals_data.csv");
            
            File selectedFile = fileChooser.showSaveDialog(stage);
            if (selectedFile != null) {
                try {
                    exportVitalsToCSV(selectedFile);
                    statusLabel.setText("File exported successfully!");
                    statusLabel.setStyle("-fx-text-fill: #43a047; -fx-font-size: 14px; -fx-font-weight: bold;");
                } catch (Exception ex) {
                    statusLabel.setText("Error exporting file: " + ex.getMessage());
                    statusLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 14px; -fx-font-weight: bold;");
                }
            }
        });

        mainContent.getChildren().add(uploadContainer);
    }

    private void processCSVFile(File file) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;
            int rowCount = 0;
            int successCount = 0;
            
            // Start a transaction
            try (Connection conn = DBConnection.getConnection()) {
                conn.setAutoCommit(false);
                
                try {
                    while ((line = br.readLine()) != null) {
                        if (isFirstLine) {
                            isFirstLine = false;
                            continue; // Skip header
                        }
                        
                        rowCount++;
                        String[] values = line.split(",");
                        if (values.length != 5) {
                            throw new Exception("Invalid CSV format at row " + rowCount + ". Expected 5 columns.");
                        }

                        // Parse and validate values
                        String timestamp = values[0].trim();
                        if (!timestamp.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                            throw new Exception("Invalid timestamp format at row " + rowCount + ". Expected format: YYYY-MM-DD HH:MM:SS");
                        }

                        double heartRate = Double.parseDouble(values[1].trim());
                        if (heartRate < 40 || heartRate > 200) {
                            throw new Exception("Invalid heart rate at row " + rowCount + ". Must be between 40 and 200 bpm.");
                        }

                        String bloodPressure = values[2].trim();
                        if (!bloodPressure.matches("\\d{2,3}/\\d{2,3}")) {
                            throw new Exception("Invalid blood pressure format at row " + rowCount + ". Expected format: systolic/diastolic");
                        }

                        double oxygenLevel = Double.parseDouble(values[3].trim());
                        if (oxygenLevel < 0 || oxygenLevel > 100) {
                            throw new Exception("Invalid oxygen level at row " + rowCount + ". Must be between 0 and 100%.");
                        }

                        double temperature = Double.parseDouble(values[4].trim());
                        if (temperature < 95 || temperature > 105) {
                            throw new Exception("Invalid temperature at row " + rowCount + ". Must be between 95 and 105°F.");
                        }

                        // Insert into database
                        try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO vitals (patient_id, timestamp, heart_rate, blood_pressure, oxygen_level, temperature) " +
                            "VALUES (?, ?, ?, ?, ?, ?)")) {
                            
                            ps.setInt(1, patientId);
                            ps.setString(2, timestamp);
                            ps.setDouble(3, heartRate);
                            ps.setString(4, bloodPressure);
                            ps.setDouble(5, oxygenLevel);
                            ps.setDouble(6, temperature);
                            ps.executeUpdate();
                            successCount++;
                        }
                    }
                    
                    // Commit the transaction if all rows were processed successfully
                    conn.commit();
                    
                    // Log the results
                    System.out.println("Successfully processed " + successCount + " out of " + rowCount + " rows.");
                    
                } catch (Exception e) {
                    // Rollback the transaction if there's an error
                    conn.rollback();
                    throw e;
                } finally {
                    // Reset auto-commit
                    conn.setAutoCommit(true);
                }
            }
        } catch (Exception e) {
            throw new Exception("Error processing CSV file: " + e.getMessage());
        }
    }

    private void exportVitalsToCSV(File file) throws Exception {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT timestamp, heart_rate, blood_pressure, oxygen_level, temperature " +
                 "FROM vitals WHERE patient_id = ? ORDER BY timestamp DESC")) {
            
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();

            try (FileWriter fw = new FileWriter(file);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                
                // Write header
                out.println("timestamp,heart_rate,blood_pressure,oxygen_level,temperature");
                
                // Write data
                while (rs.next()) {
                    String timestamp = rs.getString("timestamp");
                    double heartRate = rs.getDouble("heart_rate");
                    String bloodPressure = rs.getString("blood_pressure");
                    double oxygenLevel = rs.getDouble("oxygen_level");
                    double temperature = rs.getDouble("temperature");
                    
                    out.printf("%s,%.1f,%s,%.1f,%.1f%n",
                        timestamp, heartRate, bloodPressure, oxygenLevel, temperature);
                }
            }
        }
    }

    private static class Appointment {
        private final int appointmentId;
        private final String patient;
        private final String doctor;
        private final LocalDateTime dateTime;
        private String status;  // Removed final modifier
        private final String comments;

        public Appointment(int appointmentId, String doctor, LocalDateTime dateTime, String status, String comments, String patient) {
            this.appointmentId = appointmentId;
            this.doctor = doctor;
            this.dateTime = dateTime;
            this.status = status;
            this.comments = comments;
            this.patient = patient;
        }

        public int getAppointmentId() { return appointmentId; }
        public String getPatient() { return patient; }
        public String getDoctor() { return doctor; }
        public String getDate() { return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")); }
        public String getStatus() { return status; }
        public String getComments() { return comments; }
        public void setStatus(String status) { this.status = status; }
    }

    private ObservableList<Appointment> fetchAllAppointments() {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT a.appointment_id, p.name as patient_name, d.name as doctor_name, " +
                 "a.date, a.status " +
                 "FROM appointments a " +
                 "JOIN users p ON a.patient_id = p.user_id " +
                 "JOIN users d ON a.doctor_id = d.user_id " +
                 "ORDER BY a.date DESC")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                appointments.add(new Appointment(
                    rs.getInt("appointment_id"),
                    rs.getString("doctor_name"),
                    LocalDateTime.parse(rs.getString("date"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    rs.getString("status"),
                    rs.getString("comments"),
                    rs.getString("patient_name")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appointments;
    }
} 