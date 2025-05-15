package Doctor_PatientInteraction;

import java.util.Scanner;

public class Prescription {
    private static final Scanner sc = new Scanner(System.in);

    // View the latest prescription from a specific doctor
    public static void viewByDoctor(int patientId) {
        System.out.print("Enter the Doctor ID whose prescription you want to see: ");
        int doctorId = getValidInt();

        String result = PrescriptionDAO.getPrescriptionByDoctor(patientId, doctorId);
        System.out.println(result);
    }

    private static int getValidInt() {
        while (!sc.hasNextInt()) {
            System.out.print("Invalid input. Please enter a number: ");
            sc.next();
        }
        int value = sc.nextInt();
        sc.nextLine(); // clear buffer
        return value;
    }
}