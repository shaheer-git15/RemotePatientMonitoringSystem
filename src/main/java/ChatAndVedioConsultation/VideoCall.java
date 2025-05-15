package ChatAndVedioConsultation;

import UserManagement.UserDAO;

import java.util.Scanner;

public class VideoCall {
    static Scanner sc = new Scanner(System.in);

    public static void startCall(int userID) {
        try {
            String role = UserDAO.getRoleById(userID);

            if (role == null || role.isEmpty()) {
                System.out.println("Invalid user.");
                return;
            }

            if (role.equalsIgnoreCase("Doctor")) {
                System.out.print("Enter Patient ID to start video call: ");
                int pid = sc.nextInt();
                if (!UserDAO.getRoleById(pid).equalsIgnoreCase("Patient")) {
                    System.out.println("Patient not found.");
                    return;
                }
                simulateCall(userID, pid);
            }

            else if (role.equalsIgnoreCase("Patient")) {
                System.out.print("Enter Doctor ID to start video call: ");
                int did = sc.nextInt();
                if (!UserDAO.getRoleById(did).equalsIgnoreCase("Doctor")) {
                    System.out.println("Doctor not found.");
                    return;
                }
                simulateCall(userID, did);
            }

            else {
                System.out.println("Only patients and doctors can use this feature.");
            }
        }

        catch (Exception e) {
            System.out.println("Error during call setup: " + e.getMessage());
        }
    }

    private static void simulateCall(int callerID, int receiverID) {
        System.out.println("Connecting video call between " + callerID + " and " + receiverID + "...");
        System.out.println("[Simulation] Google Meet / Zoom link launched");
    }
}