package UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import DatabaseConnector.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignUpPage {
    private Stage stage;

    public SignUpPage() {
        stage = new Stage();
        createUI();
    }

    private void createUI() {
        // Main container
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setStyle("-fx-background-color: #f5f5f5;");

        // Title
        Label titleLabel = new Label("Patient Registration");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#2c3e50"));

        // Form container
        VBox formContainer = new VBox(15);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setMaxWidth(500);
        formContainer.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-background-radius: 10;");

        // Form fields
        TextField userIdField = new TextField();
        userIdField.setPromptText("User ID");
        userIdField.setStyle("-fx-padding: 10; -fx-font-size: 14;");

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        nameField.setStyle("-fx-padding: 10; -fx-font-size: 14;");

        TextField ageField = new TextField();
        ageField.setPromptText("Age");
        ageField.setStyle("-fx-padding: 10; -fx-font-size: 14;");

        ComboBox<String> genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("Male", "Female", "Other");
        genderComboBox.setPromptText("Select Gender");
        genderComboBox.setStyle("-fx-padding: 10; -fx-font-size: 14;");

        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        addressField.setStyle("-fx-padding: 10; -fx-font-size: 14;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setStyle("-fx-padding: 10; -fx-font-size: 14;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-padding: 10; -fx-font-size: 14;");

        // Register button
        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 10 20; -fx-background-radius: 5;");
        registerButton.setMaxWidth(Double.MAX_VALUE);

        // Add components to form
        formContainer.getChildren().addAll(
            new Label("Create New Account"),
            userIdField,
            nameField,
            ageField,
            genderComboBox,
            addressField,
            emailField,
            passwordField,
            registerButton
        );

        // Add components to main container
        mainContainer.getChildren().addAll(titleLabel, formContainer);

        // Register button action
        registerButton.setOnAction(e -> {
            if (validateAndRegister(
                userIdField.getText(),
                nameField.getText(),
                ageField.getText(),
                genderComboBox.getValue(),
                addressField.getText(),
                emailField.getText(),
                passwordField.getText()
            )) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Registration successful! You can now login.");
                stage.close();
            }
        });

        // Create scene
        Scene scene = new Scene(mainContainer, 800, 700);
        stage.setTitle("RPMS - Patient Registration");
        stage.setScene(scene);
    }

    private boolean validateAndRegister(String userId, String name, String age, String gender, 
                                      String address, String email, String password) {
        // Validate input
        if (userId.isEmpty() || name.isEmpty() || age.isEmpty() || gender == null || 
            address.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields");
            return false;
        }

        // Validate age
        try {
            int ageNum = Integer.parseInt(age);
            if (ageNum <= 0 || ageNum > 120) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid age");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Age must be a number");
            return false;
        }

        // Insert into database
        String query = "INSERT INTO users (user_id, name, age, gender, address, email, password, role) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, 'Patient')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, userId);
            pstmt.setString(2, name);
            pstmt.setInt(3, Integer.parseInt(age));
            pstmt.setString(4, gender);
            pstmt.setString(5, address);
            pstmt.setString(6, email);
            pstmt.setString(7, password);

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                showAlert(Alert.AlertType.ERROR, "Error", "User ID or Email already exists");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Registration failed: " + e.getMessage());
            }
            e.printStackTrace();
            return false;
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void show() {
        stage.show();
    }
} 