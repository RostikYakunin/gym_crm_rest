package com.crm.resources;

import com.crm.dtos.training.TrainingDto;
import com.crm.dtos.training.TrainingView;
import com.crm.enums.TrainingType;
import com.crm.services.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @Operation(
            summary = "Add training",
            description = "Creates a new training and returns.",
            parameters = {
                    @Parameter(name = "trainingDto", description = "TrainingDto object", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Training created"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            })
    @PostMapping
    public ResponseEntity<TrainingView> addTraining(@RequestBody @Valid TrainingDto trainingDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trainingService.addTraining(trainingDto));
    }

    @Operation(
            summary = "Get training types",
            description = "Provides a list of TrainingType.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "TrainingTypes provided"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            })
    @GetMapping("/types")
    public ResponseEntity<List<TrainingType>> getTrainingTypes() {
        return ResponseEntity.ok(Arrays.asList(TrainingType.values()));
    }
}