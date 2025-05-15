package HealthDataHandling;

public class VitalSign {
    private int patientId;
    private double heartRate;
    private double oxygenLevel;
    private String bloodPressure;
    private double temperature;

    public VitalSign(int patientId) {
        this.patientId = patientId;
        this.heartRate = 75.0;
        this.oxygenLevel = 98.0;
        this.bloodPressure = "120/80";
        this.temperature = 98.6;
    }

    // Setters
    public void setHeartRate(double heartRate) {
        if (heartRate > 0) this.heartRate = heartRate;
    }

    public void setOxygenLevel(double oxygenLevel) {
        if (oxygenLevel >= 0 && oxygenLevel <= 100) this.oxygenLevel = oxygenLevel;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public void setTemperature(double temperature) {
        if (temperature > 0) this.temperature = temperature;
    }

    // Getters
    public int getPatientId() { return patientId; }
    public double getHeartRate() { return heartRate; }
    public double getOxygenLevel() { return oxygenLevel; }
    public String getBloodPressure() { return bloodPressure; }
    public double getTemperature() { return temperature; }

    @Override
    public String toString() {
        return "Heart Rate    : " + heartRate + " bpm\n" +
                "Oxygen Level  : " + oxygenLevel + " %\n" +
                "Blood Pressure: " + bloodPressure + "\n" +
                "Temperature   : " + temperature + " °F";
    }
}