package com.crm.repositories.impl;

import com.crm.models.TrainingType;
import com.crm.repositories.TraineeRepo;
import com.crm.repositories.entities.Trainee;
import com.crm.repositories.entities.Training;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@Slf4j
public class TraineeRepoImpl extends AbstractUserRepo<Trainee> implements TraineeRepo {
    @Override
    protected Class<Trainee> getEntityClass() {
        return Trainee.class;
    }

    @Override
    protected String getEntityClassName() {
        return Trainee.class.getSimpleName();
    }

    @Override
    public List<Training> getTraineeTrainingsByCriteria(String traineeUsername, LocalDate fromDate, LocalDate toDate, String trainerUserName, TrainingType trainingType) {
        var jpql = """
                SELECT t FROM Training t
                WHERE t.trainee.username = :traineeUsername
                AND (:fromDate IS NULL OR t.trainingDate >= :fromDate)
                AND (:toDate IS NULL OR t.trainingDate <= :toDate)
                AND (:trainerName IS NULL OR t.trainer.firstName LIKE CONCAT('%', :trainerName, '%') OR t.trainer.lastName LIKE CONCAT('%', :trainerName, '%'))
                AND (:trainingType IS NULL OR t.trainingType = :trainingType)
                """;

        var query = entityManager.createQuery(jpql, Training.class);
        query.setParameter("traineeUsername", traineeUsername);
        query.setParameter("fromDate", fromDate != null ? fromDate.atStartOfDay() : null);
        query.setParameter("toDate", toDate != null ? toDate.atTime(23, 59, 59) : null);
        query.setParameter("trainerName", trainerUserName);
        query.setParameter("trainingType", trainingType);

        return query.getResultList();
    }
}
