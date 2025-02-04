package com.crm.init;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext
class DataInitializerTest {
    @Autowired
    private DataInitializer dataInitializer;

    private String originalTrainingDataFilePath;

    @BeforeEach
    void setUp() {
        originalTrainingDataFilePath = (String) ReflectionTestUtils.getField(dataInitializer, "trainingDataFilePath");
    }

    @AfterEach
    void tearDown() {
        ReflectionTestUtils.setField(dataInitializer, "trainingDataFilePath", originalTrainingDataFilePath);
    }

    @Test
    @DisplayName("Should not throw exception while entities initialization")
    void initializeData_shouldNotThrowException_WhileInitializationFiles() {
        // Given - When - Then
        assertDoesNotThrow(
                dataInitializer::initializeData,
                "Something went wrong with file deserialization"
        );
    }

    @Test
    @DisplayName("Should throw exception while entities initialization")
    void initializeData_shouldThrowException_WhileInitializationFiles() {
        // Given
        ReflectionTestUtils.setField(dataInitializer, "trainingDataFilePath", "wrong/pass/resources/init/training-data.json");

        // When - Then
        assertThrows(
                RuntimeException.class,
                dataInitializer::initializeData,
                "Something went wrong with file deserialization"
        );
    }
}