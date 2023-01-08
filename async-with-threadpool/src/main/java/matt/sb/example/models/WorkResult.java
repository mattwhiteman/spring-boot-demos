package matt.sb.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class WorkResult implements Serializable {
    private static final long serialVersionUID = 3397656460419233647L;

    private Integer workResult;
    private long executionTime;

    private String executionThreadName;
}
