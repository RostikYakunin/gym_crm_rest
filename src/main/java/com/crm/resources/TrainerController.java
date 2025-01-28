package com.crm.resources;

import com.crm.dtos.UserLoginDto;
import com.crm.dtos.UserStatusUpdateDto;
import com.crm.dtos.trainer.*;
import com.crm.dtos.training.TrainingShortView;
import com.crm.mappers.TrainerMapper;
import com.crm.mappers.TrainingMapper;
import com.crm.services.TrainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/trainer")
@RequiredArgsConstructor
@Tag(name = "REST API for Trainer", description = "Provides resource methods for managing trainers")
public class TrainerController {
    private final TrainerService trainerService;
    private final TrainerMapper trainerMapper;
    private final TrainingMapper trainingMapper;

    @Operation(
            summary = "Register a new trainer",
            description = "Creates a new trainer account and returns credentials.",
            parameters = {
                    @Parameter(name = "trainerDto", description = "TrainerDto object", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Trainer created"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @PostMapping
    public ResponseEntity<TrainerDto> registerTrainer(@RequestBody @Valid TrainerSaveDto trainerDto) {
        var trainer = trainerMapper.toTrainer(trainerDto);
        var savedTrainer = trainerService.save(trainer);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(trainerMapper.toDto(savedTrainer));
    }

    @Operation(
            summary = "Change trainer`s password",
            description = "Updates the password for a given username.",
            parameters = {
                    @Parameter(name = "loginDto", description = "UserLoginDto object", required = true),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @PutMapping("/password")
    public ResponseEntity<String> changePassword(@RequestBody @Valid UserLoginDto loginDto) {
        var foundTrainer = trainerService.findByUsername(loginDto.getUserName());
        var result = trainerService.changePassword(
                foundTrainer,
                loginDto.getOldPassword(),
                loginDto.getNewPassword()
        );

        return result
                ? ResponseEntity.ok("Password successfully changed")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password was not changed");
    }

    @Operation(
            summary = "Get trainer profile",
            description = "Retrieves trainer details by username.",
            parameters = {
                    @Parameter(name = "username", description = "Trainer`s username.", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/{username}")
    public ResponseEntity<TrainerView> getTrainerProfile(@PathVariable("username") String username) {
        var foundTrainer = trainerService.findByUsername(username);
        return foundTrainer != null
                ? ResponseEntity.ok(trainerMapper.toTrainerView(foundTrainer))
                : ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Update trainer`s profile",
            description = "Modifies trainer details.",
            parameters = {
                    @Parameter(name = "updateDto", description = "TrainerUpdateDto object.", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @PutMapping
    public ResponseEntity<TrainerView> updateTrainer(@RequestBody @Valid TrainerUpdateDto updateDto) {
        var currentTrainer = trainerService.findByUsername(updateDto.getUserName());
        var fromDto = trainerMapper.toTrainer(updateDto);
        trainerMapper.updateTrainer(currentTrainer, fromDto);

        var updatedTrainer = trainerService.update(currentTrainer);
        return updatedTrainer != null
                ? ResponseEntity.ok(trainerMapper.toTrainerView(updatedTrainer))
                : ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Get not assigned active trainers for a trainee",
            parameters = {
                    @Parameter(name = "username", description = "Trainee`s username", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "List was found successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/unassigned/{username}")
    public ResponseEntity<List<TrainerShortView>> getNotAssignedTrainers(@PathVariable("username") String username) {
        return ResponseEntity.ok(
                trainerService.getUnassignedTrainersByTraineeUsername(username)
                        .stream()
                        .map(trainerMapper::toTrainerShortView)
                        .toList()
        );
    }

    @Operation(
            summary = "Get trainer trainings list",
            parameters = {
                    @Parameter(name = "username", description = "Trainer`s username", required = true),
                    @Parameter(name = "periodFrom", description = "Criteria - period from."),
                    @Parameter(name = "periodTo", description = "Criteria - period to."),
                    @Parameter(name = "traineeName", description = "Trainee`s username")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "List was found successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/trainings")
    public ResponseEntity<List<TrainingShortView>> getTrainerTrainings(
            @RequestParam("username") String username,
            @RequestParam(value = "period-from", required = false) LocalDate periodFrom,
            @RequestParam(value = "period-to", required = false) LocalDate periodTo,
            @RequestParam(value = "trainee-username", required = false) String traineeUserName
    ) {
        return ResponseEntity.ok(
                trainerService.findTrainerTrainingsByCriteria(
                                username,
                                periodFrom,
                                periodTo,
                                traineeUserName
                        )
                        .stream()
                        .map(trainingMapper::toTrainingShortView)
                        .toList()
        );
    }

    @Operation(
            summary = "Activate/De-Activate Trainer",
            description = "Toggle current trainer`s status to chosen.",
            parameters = {
                    @Parameter(name = "statusUpdateDto", description = "UserStatusUpdateDto object", required = true),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Trainer profile deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @PatchMapping("/status")
    public ResponseEntity<String> updateTraineeStatus(@RequestBody @Valid UserStatusUpdateDto statusUpdateDto) {
        var foundTrainer = trainerService.findByUsername(statusUpdateDto.getUserName());
        if (foundTrainer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainer not found");
        }

        var isActive = statusUpdateDto.getIsActive()
                ? trainerService.activateStatus(foundTrainer.getId())
                : trainerService.deactivateStatus(foundTrainer.getId());

        return ResponseEntity.ok(
                "Trainer with userName=" + foundTrainer.getUserName() +
                        (isActive ? " was activated." : " was deactivated.")
        );
    }
}
