package com.crm.resources;

import com.crm.dtos.training.TrainingDto;
import com.crm.dtos.training.TrainingTypeView;
import com.crm.mappers.TrainingMapper;
import com.crm.models.TrainingType;
import com.crm.services.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/training")
@RequiredArgsConstructor
@Tag(name = "Training management", description = "Endpoints for managing trainings.")
public class TrainingController {
    private final TrainingService trainingService;
    private final TrainingMapper trainingMapper;

    @Operation(summary = "Add training")
    @PostMapping
    public ResponseEntity<String> addTraining(@RequestBody @Valid TrainingDto trainingDto) {
        var training = trainingMapper.toTraining(trainingDto);
        var result = trainingService.save(training).getId() != null;
        return ResponseEntity.ok("Training was successfully created: " + result);
    }

    @Operation(summary = "Get training types")
    @GetMapping("/types")
    public ResponseEntity<List<TrainingTypeView>> getTrainingTypes() {
        return ResponseEntity.ok(
                Arrays.stream(TrainingType.values())
                        .map(trainingMapper::toTrainingTypeView)
                        .toList()
        );
    }
}