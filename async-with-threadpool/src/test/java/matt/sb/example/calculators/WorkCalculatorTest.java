package matt.sb.example.calculators;

import matt.sb.example.models.WorkResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class WorkCalculatorTest {

    @Test
    public void testCalculateNullInput() {
        WorkCalculator underTest = new WorkCalculator();

        WorkResult result = underTest.runCalculation(null).join();

        assertEquals(0, result.getWorkResult());
        assertNotNull(result.getExecutionThreadName());
    }

    @Test
    public void testCalculate() {
        WorkCalculator underTest = new WorkCalculator();

        WorkResult result = underTest.runCalculation(2).join();

        assertEquals(4, result.getWorkResult());
        assertNotNull(result.getExecutionThreadName());
    }
}
