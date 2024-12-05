package src.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.converters.ArffSaver;

import java.io.File;
import java.util.Random;

public class DatasetSplitter {

    public static void splitData(String datasetPath) {
        try {
            // Load the dataset
            //String datasetPath = "data/Reorderd_Dataset.arff"; // Path to your ARFF file
            DataSource dataSource = new DataSource(datasetPath);
            Instances data = dataSource.getDataSet();

            // Shuffle the data
            data.randomize(new Random(42));

            // Split into training (70%), validation (15%), and testing (15%)
            int trainSize = (int) Math.round(data.numInstances() * 0.7);
            int validSize = (int) Math.round(data.numInstances() * 0.15);
            int testSize = data.numInstances() - trainSize - validSize;

            Instances trainData = new Instances(data, 0, trainSize);
            Instances validData = new Instances(data, trainSize, validSize);
            Instances testData = new Instances(data, trainSize + validSize, testSize);

            // Save the splits as ARFF files
            saveArffFile(trainData, "data/Train_Dataset.arff");
            saveArffFile(validData, "data/Validation_Dataset.arff");
            saveArffFile(testData, "data/Test_Dataset.arff");

            System.out.println("Datasets saved successfully!");
            System.out.println("Train Dataset: Train_Dataset.arff");
            System.out.println("Validation Dataset: Validation_Dataset.arff");
            System.out.println("Test Dataset: Test_Dataset.arff");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveArffFile(Instances data, String fileName) throws Exception {
        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        saver.setFile(new File(fileName));
        saver.writeBatch();
    }
}
