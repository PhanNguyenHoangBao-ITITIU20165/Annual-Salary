
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.SwingWrapper;

import java.util.*;
import java.util.stream.Collectors;

public class DataAnalysis {

    public static void analyzeData(List<Map<String, Object>> dataset) {
        // Extract numeric columns
        List<Double> salaries = extractNumericColumn(dataset, "Salary");
        List<Double> ages = extractNumericColumn(dataset, "Age");
        List<Double> yearsOfExperience = extractNumericColumn(dataset, "Years of Experience");

        // Print statistical summaries
        System.out.println("Salary Statistics:");
        printStatistics(salaries);

        System.out.println("Age Statistics:");
        printStatistics(ages);

        System.out.println("Years of Experience Statistics:");
        printStatistics(yearsOfExperience);

        // Create pie charts for distributions
        double[] salaryBins = {0, 50000, 100000, 150000, Double.MAX_VALUE};
        String[] salaryLabels = {"<50k", "50-100k", "100-150k", ">150k"};
        createBinnedPieChart(salaries, salaryBins, salaryLabels, "Salary Distribution");

        double[] ageBins = createDynamicBins(Collections.min(ages), Collections.max(ages), 7);
        String[] ageLabels = {"Under 18", "18-24", "25-34", "35-44", "45-54", "55-64", "65+"};
        createBinnedPieChart(ages, ageBins, ageLabels, "Age Distribution");

        double[] experienceBins = {0, 1, 5, 10, 20, 30, Double.MAX_VALUE};
        String[] experienceLabels = {"0-1", "2-5", "6-10", "11-20", "21-30", "31+"};
        createBinnedPieChart(yearsOfExperience, experienceBins, experienceLabels, "Years of Experience Distribution");

        // Create pie chart for education level
        createPieChart(dataset, "Education Level", "Education Level Distribution");

        // Display categorical summary
        displayCategoricalSummary(dataset, "Education Level");
    }

    private static List<Double> extractNumericColumn(List<Map<String, Object>> dataset, String column) {
        return dataset.stream()
                .map(row -> row.get(column))
                .filter(Objects::nonNull)
                .map(Object::toString)
                .peek(value -> {
                    if (!isNumeric(value)) {
                        System.err.printf("Invalid numeric value '%s' in column '%s'%n", value, column);
                    }
                })
                .filter(DataAnalysis::isNumeric)
                .map(value -> Double.valueOf(value))
                .collect(Collectors.toList());
    }

    private static void printStatistics(List<Double> data) {
        if (data.isEmpty()) {
            System.out.println("No data available.");
            return;
        }

        DescriptiveStatistics stats = new DescriptiveStatistics();
        data.forEach(stats::addValue);

        System.out.printf("Mean: %.2fk\n", stats.getMean());
        System.out.printf("Median: %.2fk\n", stats.getPercentile(50));
        System.out.printf("Standard Deviation: %.2fk\n", stats.getStandardDeviation());
        System.out.printf("Minimum: %.2fk\n", stats.getMin());
        System.out.printf("Maximum: %.2fk\n", stats.getMax());
        System.out.println();
    }

    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static void createBinnedPieChart(List<Double> data, double[] bins, String[] labels, String title) {
        if (data.isEmpty()) {
            System.out.println("No data available for " + title);
            return;
        }

        // Bin the data
        Map<String, Long> binnedData = new LinkedHashMap<>();
        for (int i = 0; i < bins.length - 1; i++) {
            double lower = bins[i];
            double upper = bins[i + 1];
            String label = labels[i];

            // Capture current i in a new variable for use inside lambda
            final int currentIndex = i;
            long count = data.stream()
                    .filter(value -> value >= lower && (currentIndex < bins.length - 2 ? value < upper : value <= upper))
                    .count();
            binnedData.put(label, count);
        }

        // Combine small segments
        long threshold = 5;
        long othersCount = binnedData.entrySet().stream()
                .filter(entry -> entry.getValue() < threshold)
                .mapToLong(Map.Entry::getValue)
                .sum();
        binnedData.entrySet().removeIf(entry -> entry.getValue() < threshold);
        if (othersCount > 0) {
            binnedData.put("Others", othersCount);
        }

        // Print binned data for debugging
        System.out.println("Binned Data for " + title + ":");
        binnedData.forEach((bin, count) -> System.out.printf("%s: %d%n", bin, count));

        // Create Pie Chart
        PieChart chart = new PieChartBuilder().width(800).height(600).title(title).build();
        binnedData.forEach((label, count) -> chart.addSeries(label + " (" + count + ")", count));
        new SwingWrapper<>(chart).displayChart();
    }

    private static void displayCategoricalSummary(List<Map<String, Object>> dataset, String column) {
        Map<String, Long> counts = dataset.stream()
                .map(row -> (String) row.get(column))
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(value -> value, Collectors.counting()));

        System.out.println("Summary for " + column + ":");
        counts.forEach((key, value) -> System.out.printf("%s: %d%n", key, value));
        System.out.println();
    }

    private static double[] createDynamicBins(double min, double max, int binCount) {
        double[] bins = new double[binCount + 1];
        double binWidth = (max - min) / binCount;
        for (int i = 0; i <= binCount; i++) {
            bins[i] = min + i * binWidth;
        }
        return bins;
    }

    private static void createPieChart(List<Map<String, Object>> dataset, String column, String title) {
        Map<String, Long> counts = dataset.stream()
                .map(row -> (String) row.get(column))
                .map(value -> (value == null || value.trim().isEmpty()) ? "Unknown" : value) // Handle null or empty values
                .collect(Collectors.groupingBy(value -> value, Collectors.counting()));

        System.out.println("Data for pie chart '" + title + "': " + counts); // Debugging output

        PieChart chart = new PieChartBuilder().width(800).height(600).title(title).build();
        counts.forEach((label, count) -> chart.addSeries(label + " (" + count + ")", count));
        new SwingWrapper<>(chart).displayChart();
    }

    private static void displayUniqueValues(List<Map<String, Object>> dataset, String column) {
        Set<String> uniqueValues = dataset.stream()
                .map(row -> (String) row.get(column))
                .collect(Collectors.toSet());

        System.out.println("Unique values in column '" + column + "': " + uniqueValues);
    }

    public static void main(String[] args) {
        // Load both datasets
        List<Map<String, Object>> salaryData = DataPreprocessing.loadDataFromCSV("data/Salary_Data_Quoted_Preprocess.csv");
        List<Map<String, Object>> stackOverflowData = DataPreprocessing.loadDataFromCSV("data/Stackoverflow_Quoted_Preprocess.csv");

        // Combine datasets
        List<Map<String, Object>> combinedDataset = new ArrayList<>();
        combinedDataset.addAll(salaryData);
        combinedDataset.addAll(stackOverflowData);
        DataPreprocessing.writeDatasetToFile(combinedDataset, "data/Combined_Dataset.csv");

        // Analyze the combined dataset
        displayUniqueValues(combinedDataset, "education_level");
        analyzeData(combinedDataset);
    }
}
