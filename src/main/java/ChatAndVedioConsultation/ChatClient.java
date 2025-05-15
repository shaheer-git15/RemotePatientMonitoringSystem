package ChatAndVedioConsultation;

import UserManagement.User;
import UserManagement.UserDAO;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ChatClient {
    static Scanner sc = new Scanner(System.in);

    public static void startChat(int userID) {
        try {
            boolean isDoctor = UserDAO.getRoleById(userID).equalsIgnoreCase("Doctor");

            System.out.print("Enter message: ");
            sc.nextLine(); // clear buffer
            String message = sc.nextLine();

            if (isDoctor) {
                System.out.print("Enter Patient ID to send message to: ");
                int pid = sc.nextInt();
                User patient = UserDAO.getUserById(pid);

                if (patient == null || !UserDAO.getRoleById(pid).equalsIgnoreCase("Patient")) {
                    throw new IllegalArgumentException("Patient not found.");
                }
                ChatServer.logMessage(userID, pid, message); // Save message to chat log
            }

            // If the user is a patient
            else {
                System.out.print("Enter Doctor ID to send message to: ");
                int did = sc.nextInt();
                User doctor = UserDAO.getUserById(did);

                if (doctor == null || !UserDAO.getRoleById(did).equalsIgnoreCase("Doctor")) {
                    throw new IllegalArgumentException("Doctor not found.");
                }
                ChatServer.logMessage(userID, did, message); // Save message to chat log
            }

            System.out.println("Message sent.");

        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid ID.");
            sc.nextLine();
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }
}