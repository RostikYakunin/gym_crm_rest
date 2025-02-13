package com.crm.repositories;

import com.crm.repositories.entities.Training;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingRepo extends JpaRepository<Training, Long> {
}
