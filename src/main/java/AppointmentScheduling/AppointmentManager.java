package AppointmentScheduling;

public class AppointmentManager {

    // Book appointment (Patient)
    public static void bookAppointment(int patientId, int doctorId) {
        AppointmentDAO.bookAppointment(patientId, doctorId);
    }

    // Cancel appointment (Patient)
    public static void cancelAppointment(int appointmentId) {
        AppointmentDAO.cancelAppointment(appointmentId);
    }

    public static void patientAppointments(int patientID){
        AppointmentDAO.viewpatientAppointments(patientID);
    }

    // Approve appointment (Doctor)
    public static void approveAppointment(int appointmentId, int doctorId) {
        AppointmentDAO.approveAppointment(appointmentId, doctorId);
    }

    // Reject appointment (Doctor)
    public static void rejectAppointment(int appointmentId, int doctorId) {
        AppointmentDAO.rejectAppointment(appointmentId, doctorId);
    }

    // View all appointments/Approved appointments for a (Doctor)
    public static void viewDoctorAppointments(int doctorId, String status) {
        AppointmentDAO.viewDoctorAppointments(doctorId, status);
    }

}