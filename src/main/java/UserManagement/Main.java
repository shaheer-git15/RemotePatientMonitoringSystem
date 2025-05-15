package UserManagement;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Welcome to RPMS ---");
            System.out.println("1. Login");
            System.out.println("2. Signup as New Patient");
            System.out.println("0. Exit");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 0 -> {
                    System.out.println("Goodbye!");
                    return;
                }

                case 1 -> {
                    int id;
                    while (true) {
                        try {
                            System.out.print("Enter your User ID: ");
                            id = sc.nextInt();
                            sc.nextLine();
                            break;
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input. Please enter a numeric User ID.");
                            sc.nextLine(); // clear the invalid input from the scanner
                        }
                    }
                    System.out.print("Enter your password: ");
                    String pwd = sc.nextLine();
                    System.out.print("Enter your role (Doctor / Patient / Administrator): ");
                    String role = sc.nextLine();
                    User.login(id, pwd, role); // this will now check DB via UserDAO
                }

                case 2 -> {
                    System.out.print("Enter new Patient ID: ");
                    int id = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter your name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter age: ");
                    int age = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter gender: ");
                    String gender = sc.nextLine();
                    System.out.print("Enter address: ");
                    String address = sc.nextLine();
                    System.out.print("Enter email: ");
                    String email = sc.nextLine();
                    System.out.print("Create password: ");
                    String password = sc.nextLine();

                    Patient newPatient = new Patient(id, name, age, gender, address, email, password);

                    // Store to DB instead of file
                    UserDAO.insertUser(newPatient, "Patient");

                    System.out.println("Patient registered successfully! You can now log in.");
                }

                default -> System.out.println("Invalid input. Try again.");
            }
        }
    }
}