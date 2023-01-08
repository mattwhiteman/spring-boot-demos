package matt.sb.example.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Data
@EqualsAndHashCode
public class AccumulatedWork implements Serializable {
    private static final long serialVersionUID = -8658999641926381175L;

    private boolean completedWithErrors;
    private long totalExecutionTime;

    private List<WorkResult> results = new LinkedList<>();

    public void addWorkResult(WorkResult result) {
        results.add(result);
    }
}
