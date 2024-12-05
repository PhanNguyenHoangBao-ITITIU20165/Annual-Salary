package src.RandomForest;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.Attribute;
import java.io.FileWriter;
import java.util.ArrayList;

public class CategorizeSalary {

    public static void Salary_Categorical(String datasetPath) throws Exception {
        // Load the dataset
        // DataSource source = new DataSource("data/Combined_Dataset.arff");
        DataSource dataSource = new DataSource(datasetPath);
        Instances data = dataSource.getDataSet();

        // Identify the index of the "Salary" attribute
        int salaryIndex = data.attribute("Salary").index();

        // Create a new nominal attribute with custom labels
        ArrayList<String> salaryLevels = new ArrayList<>();
        salaryLevels.add("low");
        salaryLevels.add("medium");
        salaryLevels.add("high");
        Attribute salaryLevelAttr = new Attribute("Salary_Level", salaryLevels);

        // Add the new attribute to the dataset
        data.insertAttributeAt(salaryLevelAttr, data.numAttributes());

        // **Retrieve the attribute from the dataset**
        salaryLevelAttr = data.attribute("Salary_Level");

        // For each instance, set the Salary_Level based on Salary value
        for (int i = 0; i < data.numInstances(); i++) {
            double salaryValue = data.instance(i).value(salaryIndex);
            String salaryLevel;
            if (salaryValue < 50000.0) {
                salaryLevel = "low";
            } else if (salaryValue <= 100000.0) {
                salaryLevel = "medium";
            } else {
                salaryLevel = "high";
            }

            // Set the value using the retrieved attribute
            data.instance(i).setValue(salaryLevelAttr, salaryLevel);
            // Alternatively, you can use the index:
            // data.instance(i).setValue(salaryLevelIndex, salaryLevel);
        }

        // Remove the original 'Salary' attribute (optional)
        data.deleteAttributeAt(salaryIndex);

        // Save the modified dataset
        FileWriter writer = new FileWriter("data/modified_dataset.arff");
        writer.write(data.toString());
        writer.close();

        System.out.println("Salary attribute has been converted to 'low', 'medium', and 'high' based on specified ranges.");
    }
}
