package src.PreProcess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mapping {
    public static List<Map<String, Object>> transformAge(List<Map<String, Object>> dataset) {
        // Use HashMap to allow null values
        Map<String, Double> ageMapping = new HashMap<>();
        ageMapping.put("Under 18 years old", 18.0);
        ageMapping.put("18-24 years old", 21.0);
        ageMapping.put("25-34 years old", 29.5);
        ageMapping.put("35-44 years old", 39.5);
        ageMapping.put("45-54 years old", 49.5);
        ageMapping.put("55-64 years old", 59.5);
        ageMapping.put("65 years or older", 70.0);
        ageMapping.put("Prefer not to say", null);
    
        for (Map<String, Object> row : dataset) {
            Object ageValue = row.get("Age");
            if (ageValue != null && ageMapping.containsKey(ageValue.toString())) {
                row.put("Age", ageMapping.get(ageValue.toString()));
            }
        }
        return dataset;
    }    

    public static List<Map<String, Object>> transformEducationLevel(List<Map<String, Object>> dataset) {
        // Use HashMap to allow null values
        Map<String, String> educationMapping = new HashMap<>();
        educationMapping.put("Bachelor’s degree (B.A., B.S., B.Eng., etc.)", "Bachelor Degree or Equivalent");
        educationMapping.put("Master’s degree (M.A., M.S., M.Eng., MBA, etc.)", "Master Degree or Equivalent");
        educationMapping.put("PhD", "Doctorate or Equivalent");
        educationMapping.put("Some college/university study without earning a degree", "Associate Degree or Short-cycle Tertiary Education");
        educationMapping.put("Primary/elementary school", "Primary Education");
        educationMapping.put("Professional degree (JD, MD, Ph.D, Ed.D, etc.)", "Professional Degree");
        educationMapping.put("Associate degree (A.A., A.S., etc.)", "Associate Degree or Short-cycle Tertiary Education");
        educationMapping.put("Secondary school (e.g. American high school, German Realschule or Gymnasium, etc.)", "Secondary Education");
        educationMapping.put("Something else", null); // Allow null for this case
        educationMapping.put("Bachelor's", "Bachelor Degree or Equivalent");
        educationMapping.put("Master's", "Master Degree or Equivalent");
    
        for (Map<String, Object> row : dataset) {
            Object eduValue = row.get("Education Level");
            if (eduValue != null && educationMapping.containsKey(eduValue.toString())) {
                row.put("Education Level", educationMapping.get(eduValue.toString()));
            }
        }
        return dataset;
    }
    

    public static List<Map<String, Object>> transformExperienceYears(List<Map<String, Object>> dataset) {
        for (Map<String, Object> row : dataset) {
            Object expValue = row.get("Years of Experience");
            Double transformedValue = null;
    
            if (expValue != null) {
                if ("Less than 1 year".equals(expValue)) {
                    transformedValue = 0.0;
                } else if ("More than 50 years".equals(expValue)) {
                    transformedValue = 50.0;
                } else {
                    try {
                        transformedValue = Double.valueOf(expValue.toString());
                    } catch (NumberFormatException e) {
                        // Leave transformedValue as null for invalid inputs
                    }
                }
            }
    
            row.put("Years of Experience", transformedValue);
        }
        return dataset;
    }
}
