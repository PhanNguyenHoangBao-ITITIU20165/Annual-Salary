

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Standardiztion {
    public static List<Map<String, Object>> standardizeColumnNames(List<Map<String, Object>> dataset) {
        Map<String, String> renameMap = Map.of(
            "EdLevel", "Education Level",
            "Education Level", "Education Level",
            "YearsCodePro", "Years of Experience",
            "Years of Experience", "Years of Experience",
            "ConvertedCompYearly", "Salary",
            "Salary", "Salary"
        );

        for (Map<String, Object> row : dataset) {
            Map<String, Object> updatedRow = new HashMap<>();
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                String standardizedKey = renameMap.getOrDefault(entry.getKey(), entry.getKey());
                standardizedKey = Character.toUpperCase(standardizedKey.charAt(0)) + standardizedKey.substring(1);
                updatedRow.put(standardizedKey, entry.getValue());
            }
            row.clear();
            row.putAll(updatedRow);
        }
        return dataset;
    }
}
