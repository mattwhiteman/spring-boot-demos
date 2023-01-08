package matt.sb.example.calculators;

import lombok.extern.slf4j.Slf4j;
import matt.sb.example.models.WorkResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class WorkCalculator {

    @Async
    public CompletableFuture<WorkResult> runCalculation(Integer value) {
        long startTime = System.currentTimeMillis();
        log.info("Async worker {} starting work", Thread.currentThread().getName());
        WorkResult retVal = new WorkResult();
        retVal.setExecutionThreadName(Thread.currentThread().getName());

        if (value == null) {
            retVal.setWorkResult(0);
        }
        else {
            retVal.setWorkResult(value*value);
        }

        try {
            // Sleep to mimic complex work and cause other threads to run simultaneously
            Thread.sleep(( (value == null ? 0 : value) % 3) * 200);
        } catch (InterruptedException ignored) {
        }

        retVal.setExecutionTime(System.currentTimeMillis() - startTime);

        log.info("Async worker {} finished work in {}ms", retVal.getExecutionThreadName(),
                retVal.getExecutionTime());
        return CompletableFuture.completedFuture(retVal);
    }
}
