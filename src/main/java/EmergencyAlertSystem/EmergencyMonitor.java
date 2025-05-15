package EmergencyAlertSystem;

import HealthDataHandling.VitalSign;
import HealthDataHandling.VitalsDAO;
import NotificationAndReminders.EmailNotification;
import NotificationAndReminders.SMSNotification;
import NotificationAndReminders.Notifiable;
import UserManagement.User;
import UserManagement.UserDAO;

import java.util.List;

public class EmergencyMonitor {

    public static void checkVitals(int patientId) {
        VitalSign vitals = VitalsDAO.getVitals(patientId);
        if (vitals == null) {
            System.out.println("No vitals found for Patient ID: " + patientId);
            return;
        }

        StringBuilder alertMessage = new StringBuilder("[AUTO EMERGENCY ALERT]\nPatient ID: ").append(patientId).append("\n");

        boolean isCritical = false;

        if (vitals.getHeartRate() < 40 || vitals.getHeartRate() > 130) {
            alertMessage.append("Abnormal Heart Rate: ").append(vitals.getHeartRate()).append("\n");
            isCritical = true;
        }
        if (vitals.getOxygenLevel() < 85) {
            alertMessage.append("Low Oxygen Level: ").append(vitals.getOxygenLevel()).append("%\n");
            isCritical = true;
        }
        if (!"120/80".equals(vitals.getBloodPressure())) {
            alertMessage.append("Abnormal Blood Pressure: ").append(vitals.getBloodPressure()).append("\n");
            isCritical = true;
        }
        if (vitals.getTemperature() < 95 || vitals.getTemperature() > 103) {
            alertMessage.append("Abnormal Temperature: ").append(vitals.getTemperature()).append("°F\n");
            isCritical = true;
        }

        if (!isCritical) return;

        List<User> doctors = UserDAO.getUsersByRole("Doctor");

        if (doctors.isEmpty()) {
            System.out.println("No doctors available to notify.");
            return;
        }

        User assignedDoctor = doctors.get(0);

        Notifiable email = new EmailNotification();
        Notifiable sms = new SMSNotification();

        email.send(assignedDoctor.getEmail(), alertMessage.toString());
        sms.send("+1234567890", alertMessage.toString());

        System.out.println("Auto emergency alert sent to Doctor ID: " + assignedDoctor.getUserID());
    }
}