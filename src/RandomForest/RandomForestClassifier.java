package src.RandomForest;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.awt.*;
import java.io.File;

public class RandomForestClassifier {

    public static void RandomForest() {
        try {
            CategorizeSalary.Salary_Categorical("data/Combined_Dataset.arff");
            DatasetSplitter.splitData("data/modified_dataset.arff");
            // Load the training, validation, and testing datasets
            Instances trainData = loadDataset("data/Train_Dataset.arff");
            Instances validData = loadDataset("data/Validation_Dataset.arff");
            Instances testData = loadDataset("data/Test_Dataset.arff");

            // Set class index (target variable) to the last attribute
            if (trainData.classIndex() == -1) trainData.setClassIndex(trainData.numAttributes() - 1);
            if (validData.classIndex() == -1) validData.setClassIndex(validData.numAttributes() - 1);
            if (testData.classIndex() == -1) testData.setClassIndex(testData.numAttributes() - 1);

            // Build Random Forest classifier
            RandomForest randomForest = new RandomForest();
            randomForest.setNumIterations(100); // Number of trees
            randomForest.setNumFeatures(0);    // Use all features
            randomForest.buildClassifier(trainData);

            // Evaluate on test data
            Evaluation testEvaluation = new Evaluation(trainData);
            testEvaluation.evaluateModel(randomForest, testData);

            System.out.println("=== Test Evaluation ===");
            System.out.println(testEvaluation.toSummaryString());
            System.out.println("Confusion Matrix:\n" + testEvaluation.toMatrixString());

            // Save Confusion Matrix as Image
            String[] classNames = new String[trainData.classAttribute().numValues()];
            for (int i = 0; i < trainData.classAttribute().numValues(); i++) {
                classNames[i] = trainData.classAttribute().value(i);
            }
            saveConfusionMatrixAsImage(testEvaluation.confusionMatrix(), classNames, "data/confusion_matrix.png");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Load a dataset from an ARFF file.
     *
     * @param filePath The path to the ARFF file.
     * @return The loaded dataset as an Instances object.
     * @throws Exception If the file cannot be loaded.
     */
    private static Instances loadDataset(String filePath) throws Exception {
        DataSource dataSource = new DataSource(filePath);
        return dataSource.getDataSet();
    }

    /**
     * Save the confusion matrix as a heatmap image.
     *
     * @param confusionMatrix The confusion matrix array.
     * @param classNames      The class labels.
     * @param filePath        The file path to save the image.
     */
    private static void saveConfusionMatrixAsImage(double[][] confusionMatrix, String[] classNames, String filePath) {
        try {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    
            // Populate dataset
            for (int i = 0; i < confusionMatrix.length; i++) {
                for (int j = 0; j < confusionMatrix[i].length; j++) {
                    dataset.addValue(confusionMatrix[i][j], classNames[i], classNames[j]);
                }
            }
    
            // Create the chart
            JFreeChart chart = ChartFactory.createBarChart(
                    "Confusion Matrix", // Title
                    "Predicted Class", // X-Axis Label
                    "Actual Class", // Y-Axis Label
                    dataset
            );
    
            // Customize chart
            CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setDomainGridlinePaint(Color.GRAY);
            plot.setRangeGridlinePaint(Color.GRAY);
    
            BarRenderer renderer = new BarRenderer();
            plot.setRenderer(renderer);
    
            CategoryAxis domainAxis = plot.getDomainAxis();
            domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
    
            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
    
            // Save chart as PNG
            ChartUtilities.saveChartAsPNG(new File(filePath), chart, 800, 600);
            System.out.println("Confusion matrix image saved at: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
}
