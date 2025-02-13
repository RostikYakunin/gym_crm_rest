package com.crm.repositories.entities;

import com.crm.enums.TrainingType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "trainers")
@DynamicUpdate
@ToString(exclude = "trainings")
public class Trainer extends User {
    @Enumerated(EnumType.STRING)
    @Column(name = "specialization", nullable = false)
    private TrainingType specialization;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Training> trainings = new ArrayList<>();
}
