package src.CatBoost;

import ai.catboost.CatBoostError;
import ai.catboost.CatBoostModel;
import ai.catboost.CatBoostPredictions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CatBoostClassifier {

    public static void CatBoost() {
        try {
            // Load the pre-trained CatBoost model
            CatBoostModel model = CatBoostModel.loadModel("data/model.cbm");

            // Open the dataset file
            BufferedReader reader = new BufferedReader(new FileReader("data/Test_Dataset.csv"));
            String line;

            // Skip the header row
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                // Split the line into features (based on CSV format)
                String[] row = line.split(",");

                // Extract numerical features (e.g., Years of Experience, Age)
                float[] numericalFeatures = new float[]{
                        Float.parseFloat(row[0]), // Years of Experience
                        Float.parseFloat(row[2])  // Age
                };

                // Extract categorical features (e.g., Education Level)
                String[] categoricalFeatures = new String[]{row[1]}; // Education Level

                // Make a prediction on the current feature vector
                CatBoostPredictions predictions = model.predict(numericalFeatures, categoricalFeatures);

                // Extract and print prediction results
                System.out.println("Prediction: " + predictions);
            }

            reader.close();

        } catch (CatBoostError e) {
            System.err.println("Error during prediction: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Error parsing numerical features: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
