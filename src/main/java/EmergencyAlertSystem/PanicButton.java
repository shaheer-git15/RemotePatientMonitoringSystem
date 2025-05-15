package EmergencyAlertSystem;

import HealthDataHandling.VitalSign;
import HealthDataHandling.VitalsDAO;

public class PanicButton {

    public PanicButton(int patientId) {
        VitalSign vitals = VitalsDAO.getVitals(patientId);

        if (vitals == null) {
            System.out.println(" No vitals found for this patient.");
            return;
        }

        emergency(patientId, vitals.getHeartRate(), vitals.getOxygenLevel(), vitals.getBloodPressure(), vitals.getTemperature());
    }

    private void emergency(int patientId, double hr, double oxy, String bp, double temp) {
        EmergencyAlert alert = new EmergencyAlert(patientId, hr, oxy, bp, temp);
        alert.notifyDoctor();
    }
}