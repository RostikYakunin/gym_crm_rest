package com.crm.dtos.trainer;

import com.crm.models.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class TrainerView {
    private String firstName;
    private String lastName;
    private TrainingType specialization;
    private Boolean isActive;

    @Builder.Default
    private List<TraineeListView> traineesList = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @Builder
    public static class TraineeListView {
        private String firstName;
        private String lastName;
        private String userName;
    }
}