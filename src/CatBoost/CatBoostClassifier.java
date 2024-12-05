package src.CatBoost;

import ai.catboost.CatBoostClassifier;
import ai.catboost.CatBoostError;
import ai.catboost.CatBoostModel;
import ai.catboost.CatBoostPredictions;
import ai.catboost.Pool;
import weka.core.Instances;
import weka.core.converters.CSVSaver;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CatBoostClassifier {

    public static void main(String[] args) {
        try {
            // Load Weka datasets
            Instances trainData = loadDataset("data/Train_Dataset.arff");
            Instances testData = loadDataset("data/Test_Dataset.arff");

            // Export Weka Instances to CSV
            saveInstancesToCSV(trainData, "data/Train_Dataset.csv");
            saveInstancesToCSV(testData, "data/Test_Dataset.csv");

            // Load data into CatBoost Pools
            Pool trainPool = Pool.load("data/Train_Dataset.csv", null);
            Pool testPool = Pool.load("data/Test_Dataset.csv", null);

            // Set CatBoost parameters
            Map<String, Object> params = new HashMap<>();
            params.put("iterations", 1000);
            params.put("learning_rate", 0.1);
            params.put("loss_function", "MultiClass");
            params.put("eval_metric", "Accuracy");
            params.put("random_seed", 42);

            // Initialize and train the model
            CatBoostClassifier model = new CatBoostClassifier(params);
            model.fit(trainPool, null);

            // Make predictions
            CatBoostPredictions predictions = model.predict(testPool);

            // Evaluate the model
            evaluateModel(predictions, testPool);

            // Save the model
            model.saveModel("data/CatBoostModel.cbm", "CatBoost model", null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load a dataset from an ARFF file
    private static Instances loadDataset(String filePath) throws Exception {
        DataSource dataSource = new DataSource(filePath);
        Instances data = dataSource.getDataSet();
        // Set class index
        if (data.classIndex() == -1) {
            data.setClassIndex(data.numAttributes() - 1);
        }
        return data;
    }

    // Save Weka Instances to CSV
    private static void saveInstancesToCSV(Instances data, String filePath) throws Exception {
        CSVSaver saver = new CSVSaver();
        saver.setInstances(data);
        saver.setFile(new File(filePath));
        saver.writeBatch();
    }

    // Evaluate the model
    private static void evaluateModel(CatBoostPredictions predictions, Pool testPool) throws CatBoostError {
        double[][] predictionArray = predictions.getRawValues();
        double[] actualLabels = testPool.getLabel();

        int numInstances = predictionArray.length;
        int[] predictedLabels = new int[numInstances];

        for (int i = 0; i < numInstances; i++) {
            // Find the class with the highest probability
            double[] classProbs = predictionArray[i];
            int predictedClass = 0;
            double maxProb = classProbs[0];
            for (int j = 1; j < classProbs.length; j++) {
                if (classProbs[j] > maxProb) {
                    maxProb = classProbs[j];
                    predictedClass = j;
                }
            }
            predictedLabels[i] = predictedClass;
        }

        // Compute accuracy
        int correct = 0;
        for (int i = 0; i < numInstances; i++) {
            if (predictedLabels[i] == (int) actualLabels[i]) {
                correct++;
            }
        }

        double accuracy = (double) correct / numInstances;
        System.out.println("Accuracy: " + (accuracy * 100) + "%");

        // Compute confusion matrix
        int numClasses = predictionArray[0].length;
        int[][] confusionMatrix = new int[numClasses][numClasses];

        for (int i = 0; i < numInstances; i++) {
            int actual = (int) actualLabels[i];
            int predicted = predictedLabels[i];
            confusionMatrix[actual][predicted]++;
        }

        // Print confusion matrix
        System.out.println("Confusion Matrix:");
        for (int i = 0; i < numClasses; i++) {
            for (int j = 0; j < numClasses; j++) {
                System.out.print(confusionMatrix[i][j] + "\t");
            }
            System.out.println();
        }
    }
}

