package com.crm.dtos.trainee;

import com.crm.models.TrainingType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TraineeView {
    private String firstName;
    private String lastName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    private String address;
    private Boolean isActive;

    @Builder.Default
    private Set<TrainerListView> trainersList = new HashSet<>();

    @Data
    @AllArgsConstructor
    @Builder
    public static class TrainerListView {
        private String userName;
        private String firstName;
        private String lastName;
        private TrainingType specialization;
    }
}

