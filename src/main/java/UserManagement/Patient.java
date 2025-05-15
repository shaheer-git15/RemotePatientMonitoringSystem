package UserManagement;

import AppointmentScheduling.AppointmentManager;
import ChatAndVedioConsultation.ChatClient;
import ChatAndVedioConsultation.ChatServer;
import ChatAndVedioConsultation.VideoCall;
import Doctor_PatientInteraction.MedicalHistory;
import Doctor_PatientInteraction.Prescription;
import EmergencyAlertSystem.PanicButton;
import HealthDataHandling.VitalSign;
import HealthDataHandling.VitalsDAO;
import NotificationAndReminders.NotificationDAO;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Patient extends User {
    static Scanner sc = new Scanner(System.in);
    VitalSign vitals;

    public Patient(int userID, String name, int age, String gender, String address, String email, String password) {
        super(userID, name, age, gender, address, email, password);
        vitals = new VitalSign(userID);
        VitalsDAO.insertVitals(vitals);
    }

    public void loginPatient() {
        while (true) {
            System.out.println("\n--- Patient Dashboard ---");
            System.out.println("1. Book appointment");
            System.out.println("2. Cancel appointment");
            System.out.println("3. see all appointments");
            System.out.println("4. Update vitals manually");
            System.out.println("5. Upload vitals from CSV");
            System.out.println("6. See your vitals");
            System.out.println("7. View your medical history");
            System.out.println("8. Press Panic Button");
            System.out.println("9. View notifications");
            System.out.println("10. Start chat");
            System.out.println("11. Start video call");
            System.out.println("12. View recent Chats");
            System.out.println("0. Logout");
            System.out.print("Choose option: ");
            int option = sc.nextInt();

            switch (option) {
                case 0 -> { return; }
                case 1 -> bookAppointment();
                case 2 -> cancelAppointment();
                case 3 -> seeAllAppointments();
                case 4 -> manualVitals();
                case 5 -> uploadCSVVitals();
                case 6 -> viewVitals();
                case 7 -> medicalHistory();
                case 8 -> new PanicButton(getUserID());
                case 9 -> NotificationDAO.viewNotifications(getUserID());
                case 10 -> ChatClient.startChat(getUserID());
                case 11 -> VideoCall.startCall(getUserID());
                case 12 -> viewRecentChats();
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void bookAppointment() {
        System.out.print("Enter Doctor ID with which you want to book an appointment: ");
        int doctorId = sc.nextInt();
        AppointmentManager.bookAppointment(getUserID(), doctorId);
    }

    private void cancelAppointment() {
        System.out.print("Enter Appointment ID to cancel: ");
        int apptId = sc.nextInt();
        AppointmentManager.cancelAppointment(apptId);
    }

    private void seeAllAppointments() {
        AppointmentManager.patientAppointments(getUserID());
        while (true) {
            System.out.println("Enter 1 to cancel an appointment");
            System.out.println("Enter 0 to go back");
            System.out.print("Enter here: ");
            int choice = sc.nextInt();
            switch (choice) {
                case 0 -> { return; }
                case 1 -> cancelAppointment();
                default -> System.out.println("Incorrect input! Enter a valid value");
            }
        }
    }

    private void manualVitals() {
        double hr;
        double oxygen;
        double temp;

        System.out.println("\n--- Manual Vitals Entry ---");
        while (true) {
            try {
                System.out.print("Enter heart rate: ");
                hr = sc.nextDouble();
                sc.nextLine(); // consume newline
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a numeric value.");
                sc.nextLine(); // clear the invalid input
            }
        }

        while (true) {
            try {
                System.out.print("Enter oxygen level: ");
                oxygen = sc.nextDouble();
                sc.nextLine(); // consume newline
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a numeric value.");
                sc.nextLine();
            }
        }

        while (true) {
            try {
                System.out.print("Enter temperature: ");
                temp = sc.nextDouble();
                sc.nextLine();
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a numeric value.");
                sc.nextLine();
            }
        }

        System.out.print("Enter blood pressure: ");
        String bp = sc.next();

        vitals.setOxygenLevel(oxygen);
        vitals.setTemperature(temp);
        vitals.setBloodPressure(bp);
        vitals.setHeartRate(hr);

        VitalsDAO.insertVitals(vitals);
    }

    private void uploadCSVVitals() {
        System.out.print("Enter CSV file path: ");
        sc.nextLine();
        String path = sc.nextLine();
        VitalsDAO.importVitalsFromCSV(path);
    }

    private void viewVitals() {
        System.out.println(VitalsDAO.getVitalsAsString(getUserID()));
    }

    private void medicalHistory() {
        while (true) {
            System.out.println("\n--- Medical History ---");
            System.out.println("1. View prescription by doctor");
            System.out.println("2. View full history");
            System.out.println("0. Back");
            int choice = sc.nextInt();

            switch (choice) {
                case 0 -> { return; }
                case 1 -> Prescription.viewByDoctor(getUserID());
                case 2 -> MedicalHistory.viewHistory(getUserID());
                default -> System.out.println("Invalid input.");
            }
        }
    }

    private void viewRecentChats(){
        System.out.print("Enter the doctor ID whose recent chats you want to see: ");
        int doctorID = sc.nextInt();
        ChatServer.viewChat(this.getUserID(), doctorID);
    }

    @Override
    public String toString() {
        return "Patient ID: " + getUserID() + ", Name: " + getName();
    }
}