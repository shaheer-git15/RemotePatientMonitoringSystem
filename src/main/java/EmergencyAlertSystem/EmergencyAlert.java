package EmergencyAlertSystem;

import NotificationAndReminders.EmailNotification;
import NotificationAndReminders.SMSNotification;
import NotificationAndReminders.Notifiable;
import UserManagement.Doctor;
import UserManagement.User;
import UserManagement.UserDAO;

import java.util.Scanner;

public class EmergencyAlert {
    private int patientId;
    private double heartRate, oxygenLevel, temperature;
    private String bloodPressure;

    public EmergencyAlert(int patientId, double heartRate, double oxygenLevel, String bloodPressure, double temperature) {
        this.patientId = patientId;
        this.heartRate = heartRate;
        this.oxygenLevel = oxygenLevel;
        this.bloodPressure = bloodPressure;
        this.temperature = temperature;
    }

    public void notifyDoctor() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Doctor ID to notify: ");
        int doctorId = sc.nextInt();

        User doctor = UserDAO.getUserById(doctorId);

        if (doctor == null || !(doctor instanceof Doctor)) {
            System.out.println("Doctor not found.");
            return;
        }

        String message = "[MANUAL EMERGENCY ALERT]\n" +
                "Patient ID     : " + patientId + "\n" +
                "Heart Rate     : " + heartRate + "\n" +
                "Oxygen Level   : " + oxygenLevel + "%\n" +
                "Blood Pressure : " + bloodPressure + "\n" +
                "Temperature    : " + temperature + "°F\n";

        Notifiable email = new EmailNotification();
        Notifiable sms = new SMSNotification();

        email.send("doctor@hospital.com", message);
        sms.send("+1234567890", message);

        System.out.println("Emergency alert sent to Doctor ID: " + doctorId);
    }
}