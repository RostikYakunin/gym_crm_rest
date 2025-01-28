package com.crm.dtos.training;

import com.crm.models.TrainingType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainingShortView {
    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime date;
    private TrainingType type;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Duration duration;
    private String trainerUserName;
}