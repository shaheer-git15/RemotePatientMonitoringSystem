package Doctor_PatientInteraction;

import java.util.List;

public class MedicalHistory {

    // View full prescription history for a patient
    public static void viewHistory(int patientId) {
        List<String> history = PrescriptionDAO.getAllPrescriptionsForPatient(patientId);

        System.out.println("\n=== Medical History for Patient ID: " + patientId + " ===");

        if (history == null || history.isEmpty()) {
            System.out.println("No medical history found.");
            return;
        }

        for (String entry : history) {
            System.out.println(entry);
            System.out.println("----------------------------------");
        }
    }
}