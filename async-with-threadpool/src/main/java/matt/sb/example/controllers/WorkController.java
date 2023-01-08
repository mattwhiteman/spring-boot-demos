package matt.sb.example.controllers;

import matt.sb.example.models.AccumulatedWork;
import matt.sb.example.services.WorkService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WorkController {
    private final WorkService workService;

    public WorkController(WorkService workService) {
        this.workService = workService;
    }

    @PostMapping("/work")
    public AccumulatedWork getWorkCalculations(@RequestBody List<Integer> values) {
        return workService.doCalculations(values);
    }
}
