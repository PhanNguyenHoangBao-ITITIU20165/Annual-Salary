package src.PreProcess;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;



public class DataPreprocessing {    

    public static void writeDatasetToFile(List<Map<String, Object>> dataset, String outputFilePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            // Write headers
            if (!dataset.isEmpty()) {
                writer.write(String.join(",", dataset.get(0).keySet()));
                writer.newLine();
            }

            // Write data rows
            for (Map<String, Object> row : dataset) {
                writer.write(row.values().stream()
                        .map(value -> value == null ? "" : value.toString())
                        .collect(Collectors.joining(",")));
                writer.newLine();
            }

            System.out.println("Processed data saved to: " + outputFilePath);
            } catch (IOException e) {
            System.err.println("An error occurred while writing the file: " + e.getMessage());
        }
    }

    public static List<Map<String, Object>> loadDataFromCSV(String filePath) {
        List<Map<String, Object>> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IOException("The file is empty: " + filePath);
            }
            String[] headers = headerLine.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
    
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                Map<String, Object> row = new HashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    String key = headers[i].trim();
                    String value = i < values.length ? values[i].trim().replace("\"", "") : null;
                    row.put(key, value);
                }
                data.add(row);
            }
        } catch (IOException e) {
            System.err.println("An error occurred while loading the file: " + e.getMessage());
        }
        return data;
    }   

    public static void main(String[] args) {
        String salaryDataInput = "data/Salary_Data.csv";
        String salaryDataQuotedOutput = "data/Salary_Data_Quoted.csv";
        String salaryDataPreprocessedOutput = "data/Salary_Data_Quoted_Preprocess.csv";

        String stackoverflowInput = "data/Stackoverflow_Developer_Survey_2023.csv";
        String stackoverflowQuotedOutput = "data/Stackoverflow_Developer_Survey_Quoted.csv";
        String stackoverflowPreprocessedOutput = "data/Stackoverflow_Quoted_Preprocess.csv";

        // Enclose strings in quotes
        encloseStringInQuotes.encloseStringsInQuotes(salaryDataInput, salaryDataQuotedOutput);
        encloseStringInQuotes.encloseStringsInQuotes(stackoverflowInput, stackoverflowQuotedOutput);

         // Load the quoted data into List<Map<String, Object>>
        List<Map<String, Object>> salaryData = loadDataFromCSV(salaryDataQuotedOutput);
        List<Map<String, Object>> stackoverflowData = loadDataFromCSV(stackoverflowQuotedOutput);

        salaryData = DropColumn.dropIrrelevantColumns(salaryData, Arrays.asList("Age", "Education Level", "Years of Experience", "Salary"));
        salaryData = Standardiztion.standardizeColumnNames(salaryData);
        salaryData = Mapping.transformAge(salaryData);
        salaryData = Mapping.transformEducationLevel(salaryData);
        salaryData = Mapping.transformExperienceYears(salaryData);
        salaryData = Processor.fillColumnsNaWithMean(salaryData, Arrays.asList("Age", "Years of Experience", "Salary"));
        salaryData = Processor.removeDuplicates(salaryData);
        salaryData = Processor.addressingOutliers(salaryData);
        
        
        //Write the processed salary data to the file
        writeDatasetToFile(salaryData, salaryDataPreprocessedOutput);

        // Preprocess StackOverflow data
        stackoverflowData = DropColumn.dropIrrelevantColumns(stackoverflowData, Arrays.asList("Age", "EdLevel", "YearsCodePro", "ConvertedCompYearly"));
        stackoverflowData = Standardiztion.standardizeColumnNames(stackoverflowData);
        stackoverflowData = Mapping.transformAge(stackoverflowData);
        stackoverflowData = Mapping.transformEducationLevel(stackoverflowData);
        stackoverflowData = Mapping.transformExperienceYears(stackoverflowData);
        stackoverflowData = Processor.fillColumnsNaWithMean(stackoverflowData, Arrays.asList("Age", "Years of Experience", "Salary"));
        stackoverflowData = Processor.removeDuplicates(stackoverflowData);
        stackoverflowData = Processor.addressingOutliers(stackoverflowData);

        // Write the processed StackOverflow data to the file
        writeDatasetToFile(stackoverflowData, stackoverflowPreprocessedOutput);
    }
}

