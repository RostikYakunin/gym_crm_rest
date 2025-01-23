package com.crm.dtos.trainee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class TraineeTrainerUpdateDto {
    @NotBlank(message = "User name is mandatory")
    @Size(min = 2, max = 255, message = "Last name must be between 2 and 255 characters")
    private String userName;

    @Builder.Default
    @NotEmpty(message = "Trainers` user name list is mandatory")
    private List<String> trainerUserNames = new ArrayList<>();
}
