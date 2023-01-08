package matt.sb.example.services;

import matt.sb.example.calculators.WorkCalculator;
import matt.sb.example.models.AccumulatedWork;
import matt.sb.example.models.WorkResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WorkServiceTest {
    @Mock
    private WorkCalculator mockWorkCalculator;

    @Test
    public void testCalculate() {
        when(mockWorkCalculator.runCalculation(1))
                .thenReturn(CompletableFuture.completedFuture(
                        new WorkResult(1, 0L, "foo")));

        when(mockWorkCalculator.runCalculation(2))
                .thenReturn(CompletableFuture.completedFuture(
                        new WorkResult(4, 0L, "foo")));

        WorkService underTest = new WorkService(mockWorkCalculator);

        AccumulatedWork result = underTest.doCalculations(Arrays.asList(1, 2));

        assertFalse(result.isCompletedWithErrors());
        assertEquals(2, result.getResults().size());
    }

    @Test
    public void testCalculateWithException() {
        when(mockWorkCalculator.runCalculation(1))
                .thenReturn(CompletableFuture.completedFuture(
                        new WorkResult(1, 0L, "foo")));

        when(mockWorkCalculator.runCalculation(2))
                .thenReturn(CompletableFuture.failedFuture(
                        new RuntimeException()));

        WorkService underTest = new WorkService(mockWorkCalculator);

        AccumulatedWork result = underTest.doCalculations(Arrays.asList(1, 2));

        assertTrue(result.isCompletedWithErrors());
        assertEquals(1, result.getResults().size());
    }
}
