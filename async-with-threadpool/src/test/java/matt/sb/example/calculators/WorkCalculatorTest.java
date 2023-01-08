package matt.sb.example.calculators;

import matt.sb.example.models.WorkResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class WorkCalculatorTest {

    @Test
    public void testCalculateNullInput() {
        WorkCalculator underTest = new WorkCalculator();

        // Test is for the underlying RuntimeException instead of the CompletedException.
        // This is because the async execution and exception wrapping is all done by
        // an interceptor added at bean creation time by spring. Since the object is
        // being created manually, no such interceptor is in place, so this method call
        // runs synchronously and does not wrap uncaught exceptions.
        assertThrows(RuntimeException.class, () -> underTest.runCalculation(null));
    }

    @Test
    public void testCalculate() {
        WorkCalculator underTest = new WorkCalculator();

        WorkResult result = underTest.runCalculation(2).join();

        assertEquals(4, result.getWorkResult());
        assertNotNull(result.getExecutionThreadName());
    }
}
