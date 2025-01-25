package com.crm.repositories.impl;

import com.crm.models.TrainingType;
import com.crm.repositories.TrainerRepo;
import com.crm.repositories.entities.Trainer;
import com.crm.repositories.entities.Training;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@Slf4j
public class TrainerRepoImpl extends AbstractUserRepo<Trainer> implements TrainerRepo {
    @Override
    protected Class<Trainer> getEntityClass() {
        return Trainer.class;
    }

    @Override
    protected String getEntityClassName() {
        return Trainer.class.getSimpleName();
    }

    @Override
    public void delete(Trainer trainer) {
        log.debug("Start deleting entity... ");
        trainer.setTrainings(null);

        trainer = entityManager.merge(trainer);
        entityManager.remove(trainer);
    }

    @Override
    public List<Training> getTrainerTrainingsByCriteria(String trainerUsername, LocalDate fromDate, LocalDate toDate, String traineeUserName, TrainingType trainingType) {
        var jpql = """
                SELECT t FROM Training t
                WHERE t.trainer.userName = :trainerUsername
                AND (:fromDate IS NULL OR t.trainingDate >= :fromDate)
                AND (:toDate IS NULL OR t.trainingDate <= :toDate)
                AND (:traineeName IS NULL OR t.trainee.firstName LIKE CONCAT('%', :traineeName, '%') OR t.trainee.lastName LIKE CONCAT('%', :traineeName, '%'))
                AND (:trainingType IS NULL OR t.trainingType = :trainingType)
                """;

        var query = entityManager.createQuery(jpql, Training.class);
        query.setParameter("trainerUsername", trainerUsername);
        query.setParameter("fromDate", fromDate != null ? fromDate.atStartOfDay() : null);
        query.setParameter("toDate", toDate != null ? toDate.atTime(23, 59, 59) : null);
        query.setParameter("traineeName", traineeUserName);
        query.setParameter("trainingType", trainingType);

        return query.getResultList();
    }

    @Override
    public List<Trainer> getUnassignedTrainersByTraineeUsername(String traineeUsername) {
        String jpql = """
                 SELECT tr FROM Trainer tr
                 WHERE tr.id NOT IN (
                   SELECT t.trainer.id
                   FROM Training t
                   WHERE t.trainee.userName = :traineeUsername
                )
                """;

        var query = entityManager.createQuery(jpql, Trainer.class);
        query.setParameter("traineeUsername", traineeUsername);

        return query.getResultList();
    }
}
