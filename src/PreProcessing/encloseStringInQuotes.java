
import java.io.*;

public class encloseStringInQuotes {
    public static void encloseStringsInQuotes(String inputFilePath, String outputFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {

            // Read the header and write it directly
            String header = reader.readLine();
            if (header != null) {
                writer.write(header);
                writer.newLine();
            }

            // Process the remaining lines
            String line;
            while ((line = reader.readLine()) != null) {
                // Use a regex to handle splitting while respecting quotes
                String[] values = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                for (int i = 0; i < values.length; i++) {
                    // Trim and check for numeric or already quoted values
                    String trimmed = values[i].trim();
                    if (!isNumeric(trimmed) && !(trimmed.startsWith("\"") && trimmed.endsWith("\""))) {
                        values[i] = "\"" + trimmed.replace("\"", "\"\"") + "\"";
                    }
                }

                // Write the updated line to the output file
                writer.write(String.join(",", values));
                writer.newLine();
            }

            System.out.println("File processed successfully. Saved to: " + outputFilePath);
        } catch (IOException e) {
            System.err.println("An error occurred while processing the file: " + e.getMessage());
        }
    }

    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
