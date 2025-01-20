package com.crm.repositories.entities;

import com.crm.models.TrainingType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "trainings")
@DynamicUpdate
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trainee_id", nullable = false)
    private Trainee trainee;

    @ManyToOne
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    @Column(name = "name", nullable = false)
    private String trainingName;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TrainingType trainingType;

    @Column(name = "date", nullable = false)
    private LocalDateTime trainingDate;

    @Column(name = "duration", nullable = false)
    private Duration trainingDuration;
}
