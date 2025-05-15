package UserManagement;

import NotificationAndReminders.ReminderService;
import ReportGenerator.ReportGenerator;

import java.util.List;
import java.util.Scanner;

public class Administrator extends User {
    static Scanner sc = new Scanner(System.in);

    public Administrator(int userID, String name, int age, String gender, String address, String email, String password) {
        super(userID, name, age, gender, address, email, password);
    }

    public void loginAdministrator() {
        while (true) {
            System.out.println("\n--- Admin Dashboard ---");
            System.out.println("1. View all doctors");
            System.out.println("2. View all patients");
            System.out.println("3. Send reminder to patient");
            System.out.println("4. Send reminder to doctor");
            System.out.println("5. Generate patient report");
            System.out.println("0. Logout");
            System.out.print("Select option: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 0 -> { return; }
                case 1 -> showDoctors();
                case 2 -> showPatients();
                case 3 -> sendReminderPatient();
                case 4 -> sendReminderDoctor();
                case 5 -> generateReport();
                default -> System.out.println("Invalid input.");
            }
        }
    }

    private void showDoctors() {
        List<User> doctors = UserDAO.getUsersByRole("Doctor");
        if (doctors.isEmpty()) {
            System.out.println("No doctors available.");
        } else {
            System.out.println("--- Doctors ---");
            doctors.forEach(doc -> System.out.println("ID: " + doc.getUserID() + ", Name: " + doc.getName()));
        }
    }

    private void showPatients() {
        List<User> patients = UserDAO.getUsersByRole("Patient");
        if (patients.isEmpty()) {
            System.out.println("No patients available.");
        } else {
            System.out.println("--- Patients ---");
            patients.forEach(pat -> System.out.println("ID: " + pat.getUserID() + ", Name: " + pat.getName()));
        }
    }

    private void sendReminderPatient() {
        System.out.print("Enter patient ID to send reminder: ");
        int pid = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter message: ");
        String msg = sc.nextLine();
        ReminderService.sendReminder(pid, msg);
    }

    private void sendReminderDoctor(){
        System.out.print("Enter doctor ID to send reminder: ");
        int did = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter message: ");
        String msg = sc.nextLine();
        ReminderService.sendReminder(did, msg);
    }

    private void generateReport() {
        System.out.print("Enter patient ID for report: ");
        int pid = sc.nextInt();
        String report = ReportGenerator.generatePatientReport(pid);
        System.out.println("--- Report ---\n" + report);
    }

    @Override
    public String toString() {
        return "Admin ID: " + this.getUserID() + ", Name: " + this.getName();
    }
}