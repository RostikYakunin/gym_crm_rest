package com.crm.dtos.trainee;

import com.crm.models.TrainingType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class TraineeView {
    private String firstName;
    private String lastName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    private String address;
    private Boolean isActive;

    @Builder.Default
    private List<TrainerListView> trainersList = new ArrayList<>();

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

