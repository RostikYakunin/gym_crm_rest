package com.crm;

import com.crm.config.TestConfig;
import com.crm.models.TrainingType;
import com.crm.repositories.TraineeRepo;
import com.crm.repositories.TrainerRepo;
import com.crm.repositories.TrainingRepo;
import com.crm.repositories.entities.Trainee;
import com.crm.repositories.entities.Trainer;
import com.crm.repositories.entities.Training;
import com.crm.repositories.impl.TraineeRepoImpl;
import com.crm.repositories.impl.TrainerRepoImpl;
import com.crm.repositories.impl.TrainingRepoImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public abstract class DbTestBase {
    @PersistenceContext
    private EntityManager entityManager;

    // repos
    protected TrainingRepo trainingRepo;
    protected TrainerRepo trainerRepo;
    protected TraineeRepo traineeRepo;

    // tested objects
    protected Training testTraining;
    protected Trainee testTrainee;
    protected Trainer testTrainer;

    @BeforeEach
    void setUp() {
        trainingRepo = new TrainingRepoImpl();
        traineeRepo = new TraineeRepoImpl();
        trainerRepo = new TrainerRepoImpl();

        ReflectionTestUtils.setField(trainingRepo, "entityManager", entityManager);
        ReflectionTestUtils.setField(traineeRepo, "entityManager", entityManager);
        ReflectionTestUtils.setField(trainerRepo, "entityManager", entityManager);

        testTrainee = Trainee.builder()
                .firstName("testName")
                .lastName("testLastName")
                .userName("testName.testLastName")
                .password("testPassword")
                .isActive(true)
                .address("testAddress")
                .dateOfBirth(LocalDate.parse("1999-10-10"))
                .build();

        testTrainer = Trainer.builder()
                .firstName("testName1")
                .lastName("testLastName1")
                .userName("testName1.testLastName1")
                .password("testPassword1")
                .isActive(true)
                .specialization(TrainingType.FITNESS)
                .build();

        testTraining = Training.builder()
                .trainee(testTrainee)
                .trainer(testTrainer)
                .trainingDate(LocalDateTime.now())
                .trainingDuration(Duration.ZERO)
                .trainingName("TestName")
                .trainingType(TrainingType.FITNESS)
                .build();
    }

    @AfterEach
    void destroy() {
        trainingRepo = null;
        trainerRepo = null;
        traineeRepo = null;
        testTraining = null;
        testTrainee = null;
        testTrainer = null;

        entityManager.createQuery("DELETE FROM Training").executeUpdate();
        entityManager.createQuery("DELETE FROM Trainer").executeUpdate();
        entityManager.createQuery("DELETE FROM Trainee").executeUpdate();

        entityManager.createNativeQuery("ALTER TABLE TRAININGS ALTER COLUMN id RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE USERS ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }
}
