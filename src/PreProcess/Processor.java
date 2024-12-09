package src.PreProcess;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Processor {
    public static List<Map<String, Object>> fillColumnsNaWithMean(List<Map<String, Object>> dataset, List<String> columns) {
        for (String column : columns) {
            // Calculate the mean of the column, ignoring invalid entries like "NA"
            double mean = dataset.stream()
                .map(row -> row.get(column))
                .filter(value -> value != null && !value.toString().trim().isEmpty() && !value.toString().equalsIgnoreCase("NA")) // Exclude invalid values
                .mapToDouble(value -> Double.parseDouble(value.toString()))
                .average()
                .orElse(0.0);
    
            // Replace null, empty, or "NA" values with the mean
            dataset.forEach(row -> {
                Object value = row.get(column);
                if (value == null || value.toString().trim().isEmpty() || value.toString().equalsIgnoreCase("NA")) {
                    row.put(column, mean);
                }
            });
        }
        return dataset;
    }            

    public static List<Map<String, Object>> removeDuplicates(List<Map<String, Object>> dataset) {
        return new ArrayList<>(new LinkedHashSet<>(dataset));
    }

    public static List<Map<String, Object>> addressingOutliers(List<Map<String, Object>> datasets) {
        // Extract salary values, ignoring nulls and invalid data
        List<Double> salaryValues = datasets.stream()
            .map(row -> row.get("salary"))
            .filter(Objects::nonNull)
            .map(Object::toString)
            .filter(value -> encloseStringInQuotes.isNumeric(value))
            .map(Double::valueOf)
            .sorted()
            .collect(Collectors.toList());
    
        // Return original dataset if salaryValues is empty
        if (salaryValues.isEmpty()) {
            return datasets;
        }
    
        // Calculate Q1, Q3, and IQR
        double q1 = getPercentile(salaryValues, 25);
        double q3 = getPercentile(salaryValues, 75);
        double iqr = q3 - q1;
    
        double lowerBound = q1 - 1.5 * iqr;
        double upperBound = q3 + 1.5 * iqr;
    
        // Filter rows to remove outliers
        return datasets.stream()
            .filter(row -> {
                Object salaryObj = row.get("salary");
                if (salaryObj == null || !encloseStringInQuotes.isNumeric(salaryObj.toString())) {
                    return true; // Keep rows with null or invalid salary
                }
                double salary = Double.valueOf(salaryObj.toString());
                return salary >= lowerBound && salary <= upperBound;
            })
            .collect(Collectors.toList());
    }
    
    // Helper method to calculate percentiles
    private static double getPercentile(List<Double> values, double percentile) {
        if (values.isEmpty()) return 0.0;
        int index = (int) Math.ceil(percentile / 100.0 * values.size()) - 1;
        return values.get(Math.max(0, Math.min(index, values.size() - 1)));
    }
}
