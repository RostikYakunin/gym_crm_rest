package com.crm.repositories;

import com.crm.repositories.entities.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TraineeRepo extends JpaRepository<Trainee, Long>, CustomTraineeRepo {
    Optional<Trainee> findByUserName(String userName);

    boolean existsByUserName(String userName);

    @Query("SELECT COUNT(t) > 0 FROM Trainer t WHERE t.firstName = :firstName AND t.lastName = :lastName")
    boolean existsTrainerByFirstAndLastName(@Param("firstName") String firstName, @Param("lastName") String lastName);
}
