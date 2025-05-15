package HealthDataHandling;

import java.io.File;
import java.util.Scanner;

public class VitalsCSVReader {

    public static void importVitalsFromCSV(String filePath) {
        try (Scanner sc = new Scanner(new File(filePath))) {
            if (sc.hasNextLine())
                sc.nextLine();

            while (sc.hasNextLine()) {
                String[] data = sc.nextLine().split(",");
                if (data.length != 5)
                    continue;

                int patientId = Integer.parseInt(data[0]);
                double heartRate = Double.parseDouble(data[1]);
                double oxygenLevel = Double.parseDouble(data[2]);
                String bloodPressure = data[3];
                double temperature = Double.parseDouble(data[4]);

                VitalSign vitals = new VitalSign(patientId);
                vitals.setHeartRate(heartRate);
                vitals.setOxygenLevel(oxygenLevel);
                vitals.setBloodPressure(bloodPressure);
                vitals.setTemperature(temperature);

                VitalsDAO.insertVitals(vitals);
                System.out.println("Vitals uploaded for Patient ID: " + patientId);
            }

        } catch (Exception e) {
            System.out.println("Error reading vitals CSV: " + e.getMessage());
        }
    }
}