package UserManagement;

import AppointmentScheduling.AppointmentManager;
import ChatAndVedioConsultation.ChatClient;
import ChatAndVedioConsultation.ChatServer;
import ChatAndVedioConsultation.VideoCall;
import Doctor_PatientInteraction.Feedback;
import Doctor_PatientInteraction.MedicalHistory;
import HealthDataHandling.VitalsDAO;
import NotificationAndReminders.NotificationDAO;

import java.util.Scanner;

public class Doctor extends User {
    static Scanner sc = new Scanner(System.in);

    public Doctor(int userID, String name, int age, String gender, String address, String email, String password) {
        super(userID, name, age, gender, address, email, password);
    }

    public void loginDoctor() {
        while (true) {
            System.out.println("\n--- Doctor Dashboard ---");
            System.out.println("1. View vitals of patient");
            System.out.println("2. See all appointments");
            System.out.println("3. See all Approved appointments");
            System.out.println("4. Prescribe and give feedback");
            System.out.println("5. View full medical history of a patient");
            System.out.println("6. View notifications");
            System.out.println("7. Start chat");
            System.out.println("8. Start video call");
            System.out.println("9. View recent Chats");
            System.out.println("0. Logout");
            System.out.print("Choose: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 0 -> { return; }
                case 1 -> viewVitals();
                case 2 -> viewAppointments();
                case 3 -> seeApprovedAppointments();
                case 4 -> prescribeFeedback();
                case 5 -> fullHistory();
                case 6 -> NotificationDAO.viewNotifications(getUserID());
                case 7 -> ChatClient.startChat(getUserID());
                case 8 -> VideoCall.startCall(getUserID());
                case 9 -> viewRecentChats();
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void viewVitals() {
        System.out.print("Enter patient ID: ");
        int pid = sc.nextInt();
        System.out.println(VitalsDAO.getVitalsAsString(pid));
        sc.nextLine();

        while (true) {
            System.out.println("Enter 1 to precribe medicine to this patient");
            System.out.println("Enter 0 to go back");
            System.out.print("Enter here: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 0 -> {return;}
                case 1 -> fullHistory();
                default -> System.out.println("Invalid option.");
            }
        }
    }


    private void viewAppointments() {
        AppointmentManager.viewDoctorAppointments(getUserID(), "all");
        while (true) {
            System.out.println("Enter 1 manage appointments");
            System.out.println("Enter 3 to see all approved appointments");
            System.out.println("Enter 0 to go back");
            System.out.print("Enter here: ");
            int answer = sc.nextInt();

            switch (answer) {
                case 0 -> { return; }
                case 1 -> manageAppointments();
                case 3 -> seeApprovedAppointments();
                default -> System.out.println("Incorrect input! Enter a valid value");
            }
        }
    }

    private void manageAppointments() {
        System.out.print("Enter appointment ID to process: ");
        int aid = sc.nextInt();
        System.out.println("1. Approve");
        System.out.println("2. Reject");
        int choice = sc.nextInt();

        if (choice == 1) {
            AppointmentManager.approveAppointment(aid, getUserID());
        } else if (choice == 2) {
            AppointmentManager.rejectAppointment(aid, getUserID());
        } else {
            System.out.println("Invalid input.");
        }
    }

    private void seeApprovedAppointments() {
        AppointmentManager.viewDoctorAppointments(getUserID(), "approved");
    }

    private void prescribeFeedback() {
        System.out.print("Enter patient ID: ");
        int pid = sc.nextInt();
        Feedback fb = new Feedback(pid, getUserID());
        fb.prescribe();
    }

    private void fullHistory() {
        System.out.print("Enter patient ID: ");
        int pid = sc.nextInt();
        MedicalHistory.viewHistory(pid);
    }

    private void viewRecentChats(){
        System.out.print("Enter the patient ID whose recent chats you want to see: ");
        int patientID = sc.nextInt();
        ChatServer.viewChat(this.getUserID(), patientID);
    }

    @Override
    public String toString() {
        return "Doctor ID: " + getUserID() + ", Name: " + getName();
    }
}