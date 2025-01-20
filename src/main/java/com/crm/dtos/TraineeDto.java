package com.crm.dtos;

import com.crm.repositories.entities.Training;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class TraineeDto extends UserDto {
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Size(min = 5, max = 255, message = "Address must be between 5 and 255 characters")
    private String address;

    @NotNull(message = "Trainings list cannot be null")
    @Builder.Default
    private Set<Training> trainings = new HashSet<>();
}
