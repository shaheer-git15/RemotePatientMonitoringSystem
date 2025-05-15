package Doctor_PatientInteraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Feedback {
    private final int patientId;
    private final int doctorId;
    private final List<String> medicines = new ArrayList<>();
    private final List<String> schedules = new ArrayList<>();
    private String feedback = "No feedback provided.";

    private static final Scanner sc = new Scanner(System.in);

    public Feedback(int patientId, int doctorId) {
        this.patientId = patientId;
        this.doctorId = doctorId;
    }

    public void prescribe() {
        inputMedicines();
        inputSchedules();
        inputFeedback();
        saveToDatabase();
    }

    private void inputMedicines() {
        System.out.print("How many medicines to prescribe? ");
        int count = safeIntInput();

        for (int i = 1; i <= count; i++) {
            System.out.print("Medicine " + i + ": ");
            medicines.add(sc.nextLine().trim());
        }
    }

    private void inputSchedules() {
        for (String med : medicines) {
            System.out.print("Schedule for " + med + ": ");
            schedules.add(sc.nextLine().trim());
        }
    }

    private void inputFeedback() {
        System.out.print("Leave feedback for this patient? (Y/N): ");
        String choice = sc.nextLine().trim();
        if (choice.equalsIgnoreCase("Y")) {
            System.out.print("Enter feedback: ");
            feedback = sc.nextLine().trim();
        }
    }

    private void saveToDatabase() {
        String meds = String.join(", ", medicines);
        String scheds = String.join(", ", schedules);
        PrescriptionDAO.insertPrescription(patientId, doctorId, meds, scheds, feedback);
        System.out.println("Prescription saved.");
    }

    private int safeIntInput() {
        while (!sc.hasNextInt()) {
            System.out.print("Please enter a valid number: ");
            sc.next();
        }
        int val = sc.nextInt();
        sc.nextLine();
        return val;
    }

    @Override
    public String toString() {
        return "\n--- Prescription ---" +
                "\nDoctor ID   : " + doctorId +
                "\nPatient ID  : " + patientId +
                "\nMedicines   : " + medicines +
                "\nSchedules   : " + schedules +
                "\nFeedback    : " + feedback;
    }
}