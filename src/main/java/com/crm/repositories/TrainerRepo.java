package com.crm.repositories;

import com.crm.repositories.entities.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrainerRepo extends JpaRepository<Trainer, Long>, CustomTrainerRepo {
    Optional<Trainer> findByUserName(String userName);

    boolean existsByUserName(String userName);

    @Query("""
             SELECT tr FROM Trainer tr
             WHERE tr.id NOT IN (
               SELECT t.trainer.id FROM Training t
               WHERE t.trainee.userName = :traineeUsername
            )
            """)
    List<Trainer> getUnassignedTrainersByTraineeUsername(@Param("traineeUsername") String traineeUsername);

    @Query("SELECT COUNT(t) > 0 FROM Trainee t WHERE t.firstName = :firstName AND t.lastName = :lastName")
    boolean existsTraineeByFirstAndLastName(@Param("firstName") String firstName, @Param("lastName") String lastName);
}
