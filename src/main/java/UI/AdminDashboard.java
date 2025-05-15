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

public class AdminDashboard {
    private final Stage stage;
    private final BorderPane root;
    private final VBox mainContent;
    private final String adminName;
    private Label title;

    public AdminDashboard(Stage stage, int adminId) {
        this.stage = stage;
        this.root = new BorderPane();
        this.mainContent = new VBox(20);
        this.mainContent.setPadding(new Insets(30, 10, 30, 30));
        this.mainContent.setStyle("-fx-background-color: #f7fafc;");
        this.adminName = fetchAdminNameFromDB(adminId);
        setupUI();
    }

    private void setupUI() {
        // Sidebar with main options
        BorderPane sidebar = new BorderPane();
        VBox navButtons = new VBox(10);
        navButtons.setAlignment(Pos.TOP_LEFT);
        navButtons.setPadding(new Insets(20, 18, 20, 18));
        sidebar.setStyle("-fx-background-color: #2c3e50; -fx-border-color: #e0e0e0; -fx-border-width: 0 2 0 0; -fx-effect: dropshadow(gaussian, #b2dfdb, 12, 0.2, 2, 0); -fx-background-radius: 18px; -fx-min-width: 270px;");

        // Welcome message at the top of the sidebar
        Label welcomeLabel = new Label("Welcome, " + adminName + "!");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #fff; -fx-padding: 0 0 12 0;");
        welcomeLabel.setAlignment(Pos.CENTER);
        welcomeLabel.setMaxWidth(Double.MAX_VALUE);

        Button btnDashboard = createSidebarButton("Dashboard");
        Button btnManageUsers = createSidebarButton("Manage Users");
        Button btnSystemStats = createSidebarButton("System Statistics");
        Button btnAppointments = createSidebarButton("All Appointments");
        Button btnReports = createSidebarButton("Reports");
        Button btnEmergencyAlerts = createSidebarButton("Emergency Alerts");
        Button btnSettings = createSidebarButton("Settings");

        navButtons.getChildren().setAll(
            welcomeLabel, btnDashboard, btnManageUsers, btnSystemStats, btnAppointments, btnReports, btnEmergencyAlerts, btnSettings, new Separator()
        );

        // Logout button
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
        stage.setTitle("Admin Dashboard");
        stage.setMaximized(true);
        stage.show();

        // Button actions
        btnDashboard.setOnAction(e -> showSection("Dashboard"));
        btnManageUsers.setOnAction(e -> showSection("Manage Users"));
        btnSystemStats.setOnAction(e -> showSection("System Statistics"));
        btnAppointments.setOnAction(e -> showSection("All Appointments"));
        btnReports.setOnAction(e -> showSection("Reports"));
        btnEmergencyAlerts.setOnAction(e -> showSection("Emergency Alerts"));
        btnSettings.setOnAction(e -> showSection("Settings"));
        btnLogout.setOnAction(e -> new LoginPage().start(stage));

        showSection("Dashboard");

        // Add CSS for emergency alert animation
        String css = "@keyframes pulse {\n" +
                    "    0% { -fx-effect: dropshadow(gaussian, #e74c3c, 10, 0.5, 0, 0); }\n" +
                    "    50% { -fx-effect: dropshadow(gaussian, #e74c3c, 20, 0.8, 0, 0); }\n" +
                    "    100% { -fx-effect: dropshadow(gaussian, #e74c3c, 10, 0.5, 0, 0); }\n" +
                    "}";
        root.getStylesheets().add("data:text/css," + css);
    }

    private Button createSidebarButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 18;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 18;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 18;"));
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    private String fetchAdminNameFromDB(int adminId) {
        String name = "Admin " + adminId;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT name FROM users WHERE user_id = ?")) {
            ps.setInt(1, adminId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                name = rs.getString("name");
            }
        } catch (Exception e) {
            System.out.println("Error fetching admin name: " + e.getMessage());
        }
        return name;
    }

    private void showSection(String section) {
        mainContent.getChildren().clear();
        title = new Label(section);
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-padding: 0 0 18 0;");

        switch (section) {
            case "Dashboard" -> showDashboard();
            case "Manage Users" -> showManageUsers();
            case "System Statistics" -> showSystemStats();
            case "All Appointments" -> showAllAppointments();
            case "Reports" -> showReports();
            case "Emergency Alerts" -> showEmergencyAlertsSection();
            case "Settings" -> showSettings();
        }
    }

    private void showDashboard() {
        showEmergencyBanner(mainContent);
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(15, 15, 15, 0));

        // Create summary boxes
        VBox totalUsersBox = createSummaryBox("Total Users", String.valueOf(getTotalUsers()), "View", "#3498db", () -> showSection("Manage Users"), "👥");
        VBox activeDoctorsBox = createSummaryBox("Active Doctors", String.valueOf(getActiveDoctors()), "View", "#2ecc71", () -> showSection("Manage Users"), "👨‍⚕️");
        VBox totalPatientsBox = createSummaryBox("Total Patients", String.valueOf(getTotalPatients()), "View", "#e74c3c", () -> showSection("Manage Users"), "👤");
        VBox todayAppointmentsBox = createSummaryBox("Today's Appointments", String.valueOf(getTodayAppointments()), "View", "#f1c40f", () -> showSection("All Appointments"), "📅");
        VBox systemHealthBox = createSummaryBox("System Health", "Good", "View", "#9b59b6", () -> showSection("System Statistics"), "💻");
        
        // Replace Recent Activity with Emergency Alerts
        int activeEmergencies = getActiveEmergencyCount();
        String emergencyStatus = activeEmergencies > 0 ? activeEmergencies + " Active" : "No Active";
        VBox emergencyAlertsBox = createSummaryBox("Emergency Alerts", emergencyStatus, "View", "#e74c3c", () -> showSection("Emergency Alerts"), "🚨");
        // Add pulsing effect if there are active emergencies
        if (activeEmergencies > 0) {
            emergencyAlertsBox.setStyle(emergencyAlertsBox.getStyle() + 
                " -fx-effect: dropshadow(gaussian, #e74c3c, 10, 0.5, 0, 0);" +
                " -fx-animation: pulse 2s infinite;");
        }

        grid.add(totalUsersBox, 0, 0);
        grid.add(activeDoctorsBox, 1, 0);
        grid.add(totalPatientsBox, 2, 0);
        grid.add(todayAppointmentsBox, 0, 1);
        grid.add(systemHealthBox, 1, 1);
        grid.add(emergencyAlertsBox, 2, 1);

        mainContent.getChildren().setAll(title, grid);
    }

    private void showEmergencyBanner(VBox parent) {
        ObservableList<EmergencyAlert> emergencies = fetchRecentEmergencies();
        if (!emergencies.isEmpty()) {
            EmergencyAlert alert = emergencies.get(0); // Show the most recent
            HBox banner = new HBox();
            banner.setStyle("-fx-background-color: #e53935; -fx-background-radius: 8px; -fx-padding: 16;");
            Label label = new Label("🚨 Emergency Alert: Patient " + alert.getPatientName() +
                " triggered an emergency at " + alert.getTimestamp());
            label.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
            banner.getChildren().add(label);
            parent.getChildren().add(0, banner); // Add at the top
        }
    }

    private void showManageUsers() {
        // Create table for users
        TableView<User> usersTable = new TableView<>();
        usersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        usersTable.setPrefHeight(400);
        usersTable.setStyle("-fx-background-radius: 14px; -fx-border-radius: 14px; -fx-background-color: white; -fx-effect: dropshadow(gaussian, #b2dfdb, 8, 0.10, 0, 1);");

        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getRole()));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));

        TableColumn<User, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));

        TableColumn<User, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            {
                editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px; -fx-padding: 4 8;");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px; -fx-padding: 4 8;");
                editBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    showEditUserDialog(user);
                });
                deleteBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    showDeleteUserConfirmation(user);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(8, editBtn, deleteBtn);
                    setGraphic(buttons);
                }
            }
        });

        usersTable.getColumns().addAll(nameCol, roleCol, emailCol, statusCol, actionCol);
        usersTable.setItems(fetchUsers());

        // Zebra striping and selection color for rows
        usersTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else if (isSelected()) {
                    setStyle("-fx-background-color: #b3e5fc; -fx-border-color: #039be5; -fx-border-width: 0 0 0 5px;");
                } else if (getIndex() % 2 == 0) {
                    setStyle("-fx-background-color: #f4faff;");
                } else {
                    setStyle("-fx-background-color: #e1f5fe;");
                }
            }
        });

        // Restore previous header styling
        usersTable.widthProperty().addListener((obs, oldVal, newVal) -> {
            usersTable.lookupAll(".column-header-background").forEach(header -> {
                header.setStyle("-fx-background-color: #1976d2; -fx-background-radius: 14px 14px 0 0;");
            });
            usersTable.lookupAll(".label").forEach(label -> {
                label.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-background-color: transparent;");
            });
        });

        // Add user button
        Button addUserBtn = new Button("Add New User");
        addUserBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 16;");
        addUserBtn.setOnAction(e -> showAddUserDialog());

        VBox content = new VBox(20, addUserBtn, usersTable);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 18px; -fx-effect: dropshadow(gaussian, #b2dfdb, 8, 0.10, 0, 1);");

        mainContent.getChildren().setAll(title, content);
    }

    private void showSystemStats() {
        // Create statistics panels
        VBox statsBox = new VBox(24);
        statsBox.setPadding(new Insets(30));
        statsBox.setStyle("-fx-background-color: white; -fx-background-radius: 18px; -fx-effect: dropshadow(gaussian, #b2dfdb, 8, 0.10, 0, 1);");

        // System Overview
        Label systemOverviewTitle = new Label("System Overview  📊");
        systemOverviewTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1976d2; -fx-padding: 0 0 18 0;");
        
        GridPane overviewGrid = new GridPane();
        overviewGrid.setHgap(32);
        overviewGrid.setVgap(18);
        overviewGrid.setPadding(new Insets(18));
        overviewGrid.setStyle("-fx-background-color: #f7fafc; -fx-background-radius: 14px; -fx-effect: dropshadow(gaussian, #b2dfdb, 4, 0.08, 0, 1);");

        // Add system metrics with icons and colored labels
        addMetricToGridStyled(overviewGrid, "👥", "Total Users", String.valueOf(getTotalUsers()), "#3498db", 0);
        addMetricToGridStyled(overviewGrid, "🟢", "Active Sessions", String.valueOf(getActiveSessions()), "#43a047", 1);
        addMetricToGridStyled(overviewGrid, "💾", "Database Size", getDatabaseSize(), "#8e24aa", 2);
        addMetricToGridStyled(overviewGrid, "⏱️", "System Uptime", getSystemUptime(), "#fbc02d", 3);
        addMetricToGridStyled(overviewGrid, "🗄️", "Last Backup", getLastBackupTime(), "#039be5", 4);
        addMetricToGridStyled(overviewGrid, "💻", "Server Status", "Online", "#00bfae", 5);

        statsBox.getChildren().setAll(systemOverviewTitle, overviewGrid);
        mainContent.getChildren().setAll(title, statsBox);
    }

    // Helper for styled metrics
    private void addMetricToGridStyled(GridPane grid, String icon, String label, String value, String color, int row) {
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 22px; -fx-padding: 0 10 0 0;");
        Label metricLabel = new Label(label + ":");
        metricLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + color + "; -fx-font-size: 16px;");
        Label metricValue = new Label(value);
        metricValue.setStyle("-fx-text-fill: #263238; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #e3f2fd; -fx-background-radius: 8px; -fx-padding: 4 12;");
        HBox rowBox = new HBox(10, iconLabel, metricLabel, metricValue);
        rowBox.setAlignment(Pos.CENTER_LEFT);
        grid.add(rowBox, 0, row);
    }

    private void showAllAppointments() {
        TableView<Appointment> appointmentsTable = new TableView<>();
        appointmentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        appointmentsTable.setPrefHeight(400);
        appointmentsTable.setStyle("-fx-background-radius: 14px; -fx-border-radius: 14px; -fx-background-color: white; -fx-effect: dropshadow(gaussian, #b2dfdb, 8, 0.10, 0, 1);");

        TableColumn<Appointment, String> patientCol = new TableColumn<>("Patient");
        patientCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPatient()));

        TableColumn<Appointment, String> doctorCol = new TableColumn<>("Doctor");
        doctorCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDoctor()));

        TableColumn<Appointment, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDate()));

        TableColumn<Appointment, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTime()));

        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));

        TableColumn<Appointment, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button approveBtn = new Button("Approve");
            private final Button cancelBtn = new Button("Cancel");
            {
                approveBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px; -fx-padding: 4 12; -fx-cursor: hand;");
                cancelBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px; -fx-padding: 4 12; -fx-cursor: hand;");
                approveBtn.setOnAction(e -> {
                    Appointment appt = getTableView().getItems().get(getIndex());
                    updateAppointmentStatus(appt.getAppointmentId(), "Approved");
                });
                cancelBtn.setOnAction(e -> {
                    Appointment appt = getTableView().getItems().get(getIndex());
                    updateAppointmentStatus(appt.getAppointmentId(), "Cancelled");
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Appointment appt = getTableView().getItems().get(getIndex());
                    HBox buttons = new HBox(8);
                    if ("Pending".equalsIgnoreCase(appt.getStatus())) {
                        buttons.getChildren().addAll(approveBtn, cancelBtn);
                    } else if ("Approved".equalsIgnoreCase(appt.getStatus())) {
                        buttons.getChildren().add(cancelBtn);
                    } else {
                        // Cancelled or other status: no actions
                    }
                    setGraphic(buttons);
                }
            }
        });

        appointmentsTable.getColumns().addAll(patientCol, doctorCol, dateCol, timeCol, statusCol, actionCol);
        appointmentsTable.setItems(fetchAllAppointments());

        // Zebra striping and selection color for rows
        appointmentsTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Appointment item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else if (isSelected()) {
                    setStyle("-fx-background-color: #b3e5fc; -fx-border-color: #039be5; -fx-border-width: 0 0 0 5px;");
                } else if (getIndex() % 2 == 0) {
                    setStyle("-fx-background-color: #f4faff;");
                } else {
                    setStyle("-fx-background-color: #e1f5fe;");
                }
            }
        });

        // Style the table header
        appointmentsTable.widthProperty().addListener((obs, oldVal, newVal) -> {
            appointmentsTable.lookupAll(".column-header-background").forEach(header -> {
                header.setStyle("-fx-background-color: #1976d2; -fx-background-radius: 14px 14px 0 0;");
            });
            appointmentsTable.lookupAll(".label").forEach(label -> {
                label.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-background-color: transparent;");
            });
        });

        VBox content = new VBox(20, appointmentsTable);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 18px; -fx-effect: dropshadow(gaussian, #b2dfdb, 8, 0.10, 0, 1);");

        mainContent.getChildren().setAll(title, content);
    }

    private void showReports() {
        VBox reportsBox = new VBox(24);
        reportsBox.setPadding(new Insets(30));
        reportsBox.setStyle("-fx-background-color: white; -fx-background-radius: 18px; -fx-effect: dropshadow(gaussian, #b2dfdb, 8, 0.10, 0, 1);");

        Label reportsTitle = new Label("Generate & Save Report");
        reportsTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1976d2; -fx-padding: 0 0 10 0;");

        // Report type
        ComboBox<String> reportType = new ComboBox<>();
        reportType.getItems().addAll(
            "User Activity Report",
            "Appointment Statistics",
            "System Usage Report",
            "Error Log Report"
        );
        reportType.setPromptText("Select Report Type");
        reportType.setStyle("-fx-font-size: 15px; -fx-background-radius: 8px; -fx-padding: 6 12; -fx-background-color: #f7fafc; -fx-border-color: black; -fx-border-width: 1.2;");

        // Date range
        HBox dateRange = new HBox(10);
        dateRange.setAlignment(Pos.CENTER_LEFT);
        DatePicker startDate = new DatePicker();
        startDate.setPromptText("Start Date");
        startDate.setStyle("-fx-font-size: 15px; -fx-background-radius: 8px; -fx-padding: 6 12; -fx-background-color: #f7fafc; -fx-border-color: black; -fx-border-width: 1.2;");
        DatePicker endDate = new DatePicker();
        endDate.setPromptText("End Date");
        endDate.setStyle("-fx-font-size: 15px; -fx-background-radius: 8px; -fx-padding: 6 12; -fx-background-color: #f7fafc; -fx-border-color: black; -fx-border-width: 1.2;");
        dateRange.getChildren().addAll(new Label("From:"), startDate, new Label("To:"), endDate);

        // File type selection
        ComboBox<String> fileTypeCombo = new ComboBox<>();
        fileTypeCombo.getItems().addAll("CSV (Excel)", "TXT (Notepad)");
        fileTypeCombo.setPromptText("Select File Type");
        fileTypeCombo.setStyle("-fx-font-size: 15px; -fx-background-radius: 8px; -fx-padding: 6 12; -fx-background-color: #f7fafc; -fx-border-color: black; -fx-border-width: 1.2;");

        // Generate & Save button
        Button generateBtn = new Button("Generate Report");
        generateBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 10 28; -fx-font-size: 16px; -fx-cursor: hand;");
        generateBtn.setDisable(true);

        // Enable button only when all fields are filled
        Runnable updateButtonState = () -> {
            boolean ready = reportType.getValue() != null && startDate.getValue() != null && endDate.getValue() != null && fileTypeCombo.getValue() != null;
            generateBtn.setDisable(!ready);
        };
        reportType.setOnAction(e -> updateButtonState.run());
        startDate.setOnAction(e -> updateButtonState.run());
        endDate.setOnAction(e -> updateButtonState.run());
        fileTypeCombo.setOnAction(e -> updateButtonState.run());

        generateBtn.setOnAction(e -> {
            String type = reportType.getValue();
            String fileType = fileTypeCombo.getValue();
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Save Report As");
            if (fileType != null && fileType.contains("CSV")) {
                fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            } else {
                fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Text Files", "*.txt"));
            }
            java.io.File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                try {
                    String content = generateSampleReportContent(type, startDate.getValue(), endDate.getValue(), fileType);
                    java.nio.file.Files.write(java.nio.file.Paths.get(file.getAbsolutePath()), content.getBytes());
                    showInfo("Report generated and saved to: " + file.getAbsolutePath());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Failed to save report: " + ex.getMessage());
                }
            }
        });

        VBox card = new VBox(18, reportType, dateRange, fileTypeCombo, generateBtn);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(28));
        card.setStyle("-fx-background-color: #f7fafc; -fx-background-radius: 14px; -fx-effect: dropshadow(gaussian, #b2dfdb, 4, 0.08, 0, 1);");

        reportsBox.getChildren().setAll(reportsTitle, card);
        mainContent.getChildren().setAll(title, reportsBox);
    }

    // Helper to generate sample report content
    private String generateSampleReportContent(String type, java.time.LocalDate start, java.time.LocalDate end, String fileType) {
        StringBuilder sb = new StringBuilder();
        if (fileType != null && fileType.contains("CSV")) {
            if (type != null && type.contains("User Activity")) {
                sb.append("User,Action,Timestamp\n");
                sb.append("Alice,Login,2024-06-01 09:00\nBob,Logout,2024-06-01 09:30\n");
            } else if (type != null && type.contains("Appointment")) {
                sb.append("Patient,Doctor,Date,Status\n");
                sb.append("John Doe,Dr. Smith,2024-06-01,Approved\nJane Roe,Dr. Adams,2024-06-02,Pending\n");
            } else if (type != null && type.contains("System Usage")) {
                sb.append("Date,Active Users,Sessions\n");
                sb.append("2024-06-01,12,20\n2024-06-02,15,25\n");
            } else if (type != null && type.contains("Error Log")) {
                sb.append("Timestamp,Error,User\n");
                sb.append("2024-06-01 10:00,NullPointerException,Alice\n2024-06-01 11:00,SQLException,Bob\n");
            } else {
                sb.append("Sample Report\n");
            }
        } else { // TXT
            sb.append("Report Type: ").append(type).append("\n");
            sb.append("Date Range: ").append(start).append(" to ").append(end).append("\n\n");
            if (type != null && type.contains("User Activity")) {
                sb.append("User Activity:\n- Alice: Login at 2024-06-01 09:00\n- Bob: Logout at 2024-06-01 09:30\n");
            } else if (type != null && type.contains("Appointment")) {
                sb.append("Appointments:\n- John Doe with Dr. Smith on 2024-06-01 (Approved)\n- Jane Roe with Dr. Adams on 2024-06-02 (Pending)\n");
            } else if (type != null && type.contains("System Usage")) {
                sb.append("System Usage:\n- 2024-06-01: 12 active users, 20 sessions\n- 2024-06-02: 15 active users, 25 sessions\n");
            } else if (type != null && type.contains("Error Log")) {
                sb.append("Error Log:\n- 2024-06-01 10:00: NullPointerException (Alice)\n- 2024-06-01 11:00: SQLException (Bob)\n");
            } else {
                sb.append("Sample Report\n");
            }
        }
        return sb.toString();
    }

    // Helper to show info dialog
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSettings() {
        VBox settingsBox = new VBox(28);
        settingsBox.setPadding(new Insets(30));
        settingsBox.setStyle("-fx-background-color: white; -fx-background-radius: 18px; -fx-effect: dropshadow(gaussian, #b2dfdb, 8, 0.10, 0, 1);");

        // Main title
        Label systemSettingsTitle = new Label("System Settings ⚙️");
        systemSettingsTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1976d2; -fx-padding: 0 0 18 0;");

        // Backup settings
        VBox backupBox = new VBox(12);
        backupBox.setPadding(new Insets(18));
        backupBox.setStyle("-fx-background-color: #f7fafc; -fx-background-radius: 12px; -fx-border-color: #b2dfdb; -fx-border-width: 1.2; -fx-border-radius: 12px;");
        Label backupTitle = new Label("🔄 Backup Settings");
        backupTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1976d2;");
        CheckBox autoBackup = new CheckBox("Enable Automatic Backups");
        autoBackup.setStyle("-fx-font-size: 15px; -fx-text-fill: #263238;");
        ComboBox<String> backupFrequency = new ComboBox<>();
        backupFrequency.getItems().addAll("Daily", "Weekly", "Monthly");
        backupFrequency.setPromptText("Select Backup Frequency");
        backupFrequency.setStyle("-fx-font-size: 15px; -fx-background-radius: 8px; -fx-padding: 6 12; -fx-background-color: #fff; -fx-border-color: #1976d2; -fx-border-width: 1.2;");
        Button backupNow = new Button("Backup Now");
        backupNow.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 18; -fx-font-size: 14px;");
        backupBox.getChildren().addAll(backupTitle, autoBackup, backupFrequency, backupNow);

        // Security settings
        VBox securityBox = new VBox(12);
        securityBox.setPadding(new Insets(18));
        securityBox.setStyle("-fx-background-color: #f7fafc; -fx-background-radius: 12px; -fx-border-color: #b2dfdb; -fx-border-width: 1.2; -fx-border-radius: 12px;");
        Label securityTitle = new Label("🔒 Security Settings");
        securityTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");
        CheckBox twoFactorAuth = new CheckBox("Enable Two-Factor Authentication");
        twoFactorAuth.setStyle("-fx-font-size: 15px; -fx-text-fill: #263238;");
        CheckBox passwordExpiry = new CheckBox("Enable Password Expiry");
        passwordExpiry.setStyle("-fx-font-size: 15px; -fx-text-fill: #263238;");
        TextField expiryDays = new TextField();
        expiryDays.setPromptText("Days until password expires");
        expiryDays.setStyle("-fx-font-size: 15px; -fx-background-radius: 8px; -fx-padding: 6 12; -fx-background-color: #fff; -fx-border-color: #e74c3c; -fx-border-width: 1.2;");
        securityBox.getChildren().addAll(securityTitle, twoFactorAuth, passwordExpiry, expiryDays);

        // Notification settings
        VBox notificationBox = new VBox(12);
        notificationBox.setPadding(new Insets(18));
        notificationBox.setStyle("-fx-background-color: #f7fafc; -fx-background-radius: 12px; -fx-border-color: #b2dfdb; -fx-border-width: 1.2; -fx-border-radius: 12px;");
        Label notificationTitle = new Label("🔔 Notification Settings");
        notificationTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #43a047;");
        CheckBox emailNotifications = new CheckBox("Enable Email Notifications");
        emailNotifications.setStyle("-fx-font-size: 15px; -fx-text-fill: #263238;");
        CheckBox systemNotifications = new CheckBox("Enable System Notifications");
        systemNotifications.setStyle("-fx-font-size: 15px; -fx-text-fill: #263238;");
        TextField adminEmail = new TextField();
        adminEmail.setPromptText("Admin Email for Notifications");
        adminEmail.setStyle("-fx-font-size: 15px; -fx-background-radius: 8px; -fx-padding: 6 12; -fx-background-color: #fff; -fx-border-color: #43a047; -fx-border-width: 1.2;");
        notificationBox.getChildren().addAll(notificationTitle, emailNotifications, systemNotifications, adminEmail);

        // Save Changes button
        Button saveBtn = new Button("Save Changes");
        saveBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 12 32; -fx-font-size: 16px; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, #b2dfdb, 4, 0.10, 0, 1);");
        saveBtn.setOnAction(e -> showInfo("Settings saved successfully!"));

        settingsBox.getChildren().setAll(systemSettingsTitle, backupBox, securityBox, notificationBox, saveBtn);

        ScrollPane scrollPane = new ScrollPane(settingsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        mainContent.getChildren().setAll(title, scrollPane);
    }

    // Helper methods for dashboard statistics
    private int getTotalUsers() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM users")) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getActiveDoctors() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE role = 'Doctor' AND status = 'Active'")) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getTotalPatients() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE role = 'Patient'")) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getTodayAppointments() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM appointments WHERE DATE(date) = CURDATE()")) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getActiveEmergencyCount() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT COUNT(*) FROM emergency_alerts WHERE status IN ('Active', 'Monitoring')")) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private VBox createSummaryBox(String title, String count, String buttonText, String color, Runnable onClick, String icon) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, #b2dfdb, 12, 0.20, 0, 2); " +
                    "-fx-pref-width: 260px; -fx-pref-height: 140px;");

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 32px; -fx-padding: 0 0 6 0; -fx-text-fill: " + color + ";");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        Label countLabel = new Label(count);
        countLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #263238;");
        
        Button actionBtn = new Button(buttonText);
        actionBtn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; " +
                          "-fx-background-radius: 8px; -fx-padding: 6 16; -fx-font-size: 12px;");
        actionBtn.setMaxWidth(Double.MAX_VALUE);
        actionBtn.setOnAction(e -> onClick.run());

        box.getChildren().addAll(iconLabel, titleLabel, countLabel, actionBtn);
        return box;
    }

    // Helper methods for user management
    private ObservableList<User> fetchUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM users ORDER BY name")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(new User(
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("role"),
                    rs.getString("status")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    private void showAddUserDialog() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        dialog.setHeaderText("Enter user details");

        // Create the custom dialog content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("Doctor", "Patient", "Admin");
        roleCombo.setPromptText("Select Role");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(new Label("Role:"), 0, 3);
        grid.add(roleCombo, 1, 3);

        dialog.getDialogPane().setContent(grid);

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new User(0, nameField.getText(), emailField.getText(), roleCombo.getValue(), "Active");
            }
            return null;
        });

        dialog.showAndWait().ifPresent(user -> {
            // Add user to database
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)")) {
                ps.setString(1, user.getName());
                ps.setString(2, user.getEmail());
                ps.setString(3, "default_password");
                ps.setString(4, user.getRole());
                ps.executeUpdate();
                
                // Refresh the users table
                showSection("Manage Users");
            } catch (Exception e) {
                e.printStackTrace();
                showError("Failed to add user: " + e.getMessage());
            }
        });
    }

    private void showEditUserDialog(User user) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Edit user details");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(user.getName());
        TextField emailField = new TextField(user.getEmail());
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("Doctor", "Patient", "Admin");
        roleCombo.setValue(user.getRole());
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Active", "Inactive", "Suspended");
        statusCombo.setValue(user.getStatus());

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Role:"), 0, 2);
        grid.add(roleCombo, 1, 2);
        grid.add(new Label("Status:"), 0, 3);
        grid.add(statusCombo, 1, 3);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new User(user.getUserId(), nameField.getText(), emailField.getText(), 
                              roleCombo.getValue(), statusCombo.getValue());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedUser -> {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "UPDATE users SET name = ?, email = ?, role = ?, status = ? WHERE user_id = ?")) {
                ps.setString(1, updatedUser.getName());
                ps.setString(2, updatedUser.getEmail());
                ps.setString(3, updatedUser.getRole());
                ps.setString(4, updatedUser.getStatus());
                ps.setInt(5, updatedUser.getUserId());
                ps.executeUpdate();
                
                // Refresh the users table
                showSection("Manage Users");
            } catch (Exception e) {
                e.printStackTrace();
                showError("Failed to update user: " + e.getMessage());
            }
        });
    }

    private void showDeleteUserConfirmation(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete User");
        alert.setHeaderText("Delete User: " + user.getName());
        alert.setContentText("Are you sure you want to delete this user? This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE user_id = ?")) {
                    ps.setInt(1, user.getUserId());
                    ps.executeUpdate();
                    
                    // Refresh the users table
                    showSection("Manage Users");
                } catch (Exception e) {
                    e.printStackTrace();
                    showError("Failed to delete user: " + e.getMessage());
                }
            }
        });
    }

    // Helper methods for system statistics
    private String getDatabaseSize() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) as size " +
                 "FROM information_schema.tables " +
                 "WHERE table_schema = DATABASE()")) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("size") + " MB";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    private String getSystemUptime() {
        // In a real application, this would get the actual system uptime
        return "24 days, 6 hours";
    }

    private String getLastBackupTime() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT MAX(backup_time) as last_backup FROM system_backups")) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("last_backup");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Never";
    }

    private int getActiveSessions() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT COUNT(DISTINCT user_id) FROM active_sessions")) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Helper methods for settings
    private VBox createBackupSettings() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        CheckBox autoBackup = new CheckBox("Enable Automatic Backups");
        ComboBox<String> backupFrequency = new ComboBox<>();
        backupFrequency.getItems().addAll("Daily", "Weekly", "Monthly");
        backupFrequency.setPromptText("Select Backup Frequency");
        
        Button backupNow = new Button("Backup Now");
        backupNow.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        
        box.getChildren().addAll(autoBackup, backupFrequency, backupNow);
        return box;
    }

    private VBox createSecuritySettings() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        CheckBox twoFactorAuth = new CheckBox("Enable Two-Factor Authentication");
        CheckBox passwordExpiry = new CheckBox("Enable Password Expiry");
        TextField expiryDays = new TextField();
        expiryDays.setPromptText("Days until password expires");
        
        box.getChildren().addAll(twoFactorAuth, passwordExpiry, expiryDays);
        return box;
    }

    private VBox createNotificationSettings() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        CheckBox emailNotifications = new CheckBox("Enable Email Notifications");
        CheckBox systemNotifications = new CheckBox("Enable System Notifications");
        TextField adminEmail = new TextField();
        adminEmail.setPromptText("Admin Email for Notifications");
        
        box.getChildren().addAll(emailNotifications, systemNotifications, adminEmail);
        return box;
    }

    private void generateReport(String reportType, java.time.LocalDate startDate, java.time.LocalDate endDate) {
        if (reportType == null || startDate == null || endDate == null) {
            showError("Please select report type and date range");
            return;
        }

        // In a real application, this would generate the actual report
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report Generation");
        alert.setHeaderText(null);
        alert.setContentText("Generating " + reportType + " for period: " + startDate + " to " + endDate);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // User class for user management
    private static class User {
        private final int userId;
        private final String name;
        private final String email;
        private final String role;
        private final String status;

        public User(int userId, String name, String email, String role, String status) {
            this.userId = userId;
            this.name = name;
            this.email = email;
            this.role = role;
            this.status = status;
        }

        public int getUserId() { return userId; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
        public String getStatus() { return status; }
    }

    // Appointment class for appointment management
    private static class Appointment {
        private final int appointmentId;
        private final String patient;
        private final String doctor;
        private final String date;
        private final String time;
        private final String status;

        public Appointment(int appointmentId, String patient, String doctor, String date, String time, String status) {
            this.appointmentId = appointmentId;
            this.patient = patient;
            this.doctor = doctor;
            this.date = date;
            this.time = time;
            this.status = status;
        }

        public int getAppointmentId() { return appointmentId; }
        public String getPatient() { return patient; }
        public String getDoctor() { return doctor; }
        public String getDate() { return date; }
        public String getTime() { return time; }
        public String getStatus() { return status; }
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
                    rs.getString("patient_name"),
                    rs.getString("doctor_name"),
                    rs.getString("date"),
                    "", // No time column, pass empty string
                    rs.getString("status")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appointments;
    }

    // Helper method to update appointment status in the database
    private void updateAppointmentStatus(int appointmentId, String newStatus) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE appointments SET status = ? WHERE appointment_id = ?")) {
            ps.setString(1, newStatus);
            ps.setInt(2, appointmentId);
            ps.executeUpdate();
            // Refresh the appointments table
            showSection("All Appointments");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to update appointment: " + e.getMessage());
        }
    }

    private ObservableList<EmergencyAlert> fetchRecentEmergencies() {
        ObservableList<EmergencyAlert> alerts = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT e.alert_id, u.name, e.alert_time, e.comments, e.vitals_info, e.status, " +
                 "CASE WHEN e.triggered_by = 'BUTTON' THEN 'Emergency Button' ELSE 'Critical Vitals' END as alert_type " +
                 "FROM emergency_alerts e " +
                 "JOIN users u ON e.patient_id = u.user_id " +
                 "WHERE e.status IN ('Active', 'Monitoring') " +
                 "ORDER BY e.alert_time DESC")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                alerts.add(new EmergencyAlert(
                    rs.getInt("alert_id"),
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

    // EmergencyAlert class for emergency notifications
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

    // Emergency Alerts Section
    private void showEmergencyAlertsSection() {
        ObservableList<EmergencyAlert> emergencies = fetchRecentEmergencies();
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
                });
                
                resolveBtn.setOnAction(e -> {
                    EmergencyAlert alert = getTableView().getItems().get(getIndex());
                    updateEmergencyStatus(alert.getAlertId(), "Resolved");
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

        VBox mainBox = new VBox(20, title, content);
        mainBox.setPadding(new Insets(20));
        mainContent.getChildren().setAll(mainBox);
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
        
        // Style the labels
        for (Label label : new Label[]{patientName, timestamp, alertType, vitals, comments, status}) {
            label.setStyle("-fx-font-size: 14px; -fx-text-fill: #333; -fx-padding: 5 0;");
            label.setWrapText(true);
        }
        
        Button viewHistoryBtn = new Button("View Patient History");
        viewHistoryBtn.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px; -fx-padding: 8 16;");
        viewHistoryBtn.setOnAction(e -> showPatientMedicalHistory(alert.getPatientName()));
        
        detailsPanel.getChildren().addAll(title, patientName, timestamp, alertType, vitals, comments, status, viewHistoryBtn);
    }

    private void updateEmergencyStatus(int alertId, String newStatus) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE emergency_alerts SET status = ? WHERE alert_id = ?")) {
            ps.setString(1, newStatus);
            ps.setInt(2, alertId);
            ps.executeUpdate();
            showSection("Emergency Alerts"); // Refresh the view
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to update emergency status: " + e.getMessage());
        }
    }

    private void showPatientMedicalHistory(String patientName) {
        // Implementation for showing patient's medical history
        // This would typically open a new window or dialog with the patient's medical records
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Medical History");
        alert.setHeaderText("Medical History for " + patientName);
        alert.setContentText("This feature will show the patient's complete medical history, including past emergencies, prescriptions, and treatments.");
        alert.showAndWait();
    }
} 