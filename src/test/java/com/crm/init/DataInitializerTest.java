package com.crm.init;

import com.crm.config.TestConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DataInitializerTest {
    private DataInitializer dataInitializer;

    @BeforeEach
    void init() {
        var context = new AnnotationConfigApplicationContext(TestConfig.class);
        dataInitializer = context.getBean("dataInitializer", DataInitializer.class);
    }

    @AfterEach
    void destroy() {
        dataInitializer = null;
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