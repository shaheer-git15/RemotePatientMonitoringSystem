package NotificationAndReminders;

import UserManagement.User;
import UserManagement.UserDAO;

public class ReminderService {
    public static void sendReminder(int userID, String message) {
        try {
            boolean isDoctor = UserDAO.getRoleById(userID).equalsIgnoreCase("Doctor");
            boolean isPatient = UserDAO.getRoleById(userID).equalsIgnoreCase("patient");

            if(isDoctor){
                User doctor = UserDAO.getUserById(userID);
                Notifiable email = new EmailNotification();
                Notifiable sms = new SMSNotification();

                email.send(doctor.getEmail(), message);
                sms.send("+1234567890", message);

                NotificationDAO.logNotification(userID, "[Reminder] " + message);
                System.out.println("Reminder sent to doctorID: " + userID);
            }

            else if(isPatient) {
                User patient = UserDAO.getUserById(userID);

                Notifiable email = new EmailNotification();
                Notifiable sms = new SMSNotification();

                email.send(patient.getEmail(), message);
                sms.send("+1234567890", message);

                NotificationDAO.logNotification(userID, "[Reminder] " + message);
                System.out.println("Reminder sent to Patient ID: " + userID);
            }

            else
                throw new IllegalArgumentException("Patient with ID " + userID + " does not exist.");


        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error while sending reminder: " + e.getMessage());
        }
    }
}