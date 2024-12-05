

import java.util.List;
import java.util.Map;

public class DropColumn {
    public static List<Map<String, Object>> dropIrrelevantColumns(List<Map<String, Object>> dataset, List<String> columnsToKeep) {
        for (Map<String, Object> row : dataset) {
            row.keySet().removeIf(col -> !columnsToKeep.contains(col));
        }
        return dataset;
    }
}
