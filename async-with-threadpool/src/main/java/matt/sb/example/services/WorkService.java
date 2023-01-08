package matt.sb.example.services;

import lombok.extern.slf4j.Slf4j;
import matt.sb.example.calculators.WorkCalculator;
import matt.sb.example.models.AccumulatedWork;
import matt.sb.example.models.WorkResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

@Component
@Slf4j
public class WorkService {
    private final WorkCalculator workCalculator;

    public WorkService(WorkCalculator workCalculator) {
        this.workCalculator = workCalculator;
    }

    public AccumulatedWork doCalculations(List<Integer> values) {
        AccumulatedWork retVal = new AccumulatedWork();

        long startTime = System.currentTimeMillis();

        List<CompletableFuture<WorkResult>> futuresList =
                values.stream().map(workCalculator::runCalculation)
                        .collect(Collectors.toList());

        futuresList.forEach(workFuture -> {
            try {
                retVal.addWorkResult(workFuture.join());
            } catch (CompletionException | CancellationException e) {
                // In a real REST application, the http status should be changed to indicate
                // a partial response. This would be a design decision specific to the application
                retVal.setCompletedWithErrors(true);
            }
        });
        retVal.setTotalExecutionTime(System.currentTimeMillis() - startTime);

        log.info("All async threads finished in {}ms", retVal.getTotalExecutionTime());

        return retVal;
    }
}
