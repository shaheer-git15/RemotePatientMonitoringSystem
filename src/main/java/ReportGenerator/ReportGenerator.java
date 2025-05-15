package ReportGenerator;

import Doctor_PatientInteraction.PrescriptionDAO;
import HealthDataHandling.VitalsDAO;
import HealthDataHandling.VitalSign;
import UserManagement.User;
import UserManagement.UserDAO;

public class ReportGenerator {

    public static String generatePatientReport(int patientId) {
        User patient = UserDAO.getUserById(patientId);

        if (patient == null) {
            return "Patient not found.";
        }

        StringBuilder report = new StringBuilder();
        report.append("===== Remote Patient Report =====\n");
        report.append("Patient ID: ").append(patient.getUserID()).append("\n");
        report.append("Name      : ").append(patient.getName()).append("\n");
        report.append("Gender    : ").append(patient.getGender()).append("\n");
        report.append("Age       : ").append(patient.getAge()).append("\n");
        report.append("Email     : ").append(patient.getEmail()).append("\n\n");

        // Add vitals
        report.append(">>> Latest Vitals <<<\n");
        VitalSign vitals = VitalsDAO.getVitals(patientId);
        if (vitals == null) {
            report.append("No vitals found.\n\n");
        } else {
            report.append(vitals).append("\n\n");
        }

        // Add feedback/prescription
        report.append(">>> Doctor Feedback & Prescriptions <<<\n");
        String feedback = PrescriptionDAO.getPrescriptionText(patientId);
        if (feedback == null) {
            report.append("No feedback or prescriptions found.\n");
        } else {
            report.append(feedback).append("\n");
        }

        report.append("==================================");

        return report.toString();
    }
}