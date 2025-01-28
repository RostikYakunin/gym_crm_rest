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
    public List<Training> getTrainerTrainingsByCriteria(
            String trainerUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeUserName,
            TrainingType trainingType
    ) {
        var dynamicJpqlQuery = "SELECT t FROM Training t WHERE t.trainer.userName = :trainerUsername";

        if (fromDate != null) {
            dynamicJpqlQuery += " AND t.trainingDate >= :fromDate";
        }
        if (toDate != null) {
            dynamicJpqlQuery += " AND t.trainingDate <= :toDate";
        }
        if (traineeUserName != null && !traineeUserName.isEmpty()) {
            dynamicJpqlQuery += " AND (t.trainee.firstName LIKE :traineeName OR t.trainee.lastName LIKE :traineeName)";
        }
        if (trainingType != null) {
            dynamicJpqlQuery += " AND t.trainingType = :trainingType";
        }

        var query = entityManager.createQuery(dynamicJpqlQuery, Training.class);
        query.setParameter("trainerUsername", trainerUsername);

        if (fromDate != null) {
            query.setParameter("fromDate", fromDate);
        }
        if (toDate != null) {
            query.setParameter("toDate", toDate);
        }
        if (traineeUserName != null && !traineeUserName.isEmpty()) {
            query.setParameter("traineeName", "%" + traineeUserName + "%");
        }
        if (trainingType != null) {
            query.setParameter("trainingType", trainingType);
        }

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
