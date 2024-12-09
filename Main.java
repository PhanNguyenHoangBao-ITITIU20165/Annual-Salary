
import src.RandomForest.*;
import src.DataAnalysis.*;
import src.CatBoost.*;


public class Main {

    public static void main(String[] args) {
        try {
            // Preprocess and analyze the dataset
            DataAnalysis.AnalyzeData();

            // RandomForest Classification
            RandomForestClassifier.RandomForest();

            // CatBoost Prediction
            CatBoostClassifier.CatBoost();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
