package com.crm.utils;

import com.crm.repositories.entities.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class UserUtilsTest {
    private Trainee testUser;

    @BeforeEach
    void setUp() {
        testUser = Trainee.builder()
                .firstName("John")
                .lastName("Doe")
                .id(1L)
                .build();
    }

    @Test
    @DisplayName("generateUniqueUsername should create a unique username without conflicts")
    void generateUniqueUsername_ShouldCreateUniqueUsername_WhenNoConflicts() {
        // Given
        Set<String> existingUsernames = new HashSet<>();
        Function<String, Boolean> usernameExistsChecker = existingUsernames::contains;

        // When
        String result = UserUtils.generateUniqueUsername(testUser, usernameExistsChecker);

        // Then
        assertEquals("John.Doe", result);
    }

    @Test
    @DisplayName("generateUniqueUsername should append counter for conflicting usernames")
    void generateUniqueUsername_ShouldAppendCounter_WhenConflictsExist() {
        // Given
        Set<String> existingUsernames = Set.of("John.Doe");
        Function<String, Boolean> usernameExistsChecker = existingUsernames::contains;

        // When
        String result = UserUtils.generateUniqueUsername(testUser, usernameExistsChecker);

        // Then
        assertEquals("John.Doe1", result);
    }

    @Test
    @DisplayName("generateUniqueUsername should handle multiple conflicts correctly")
    void generateUniqueUsername_ShouldHandleMultipleConflicts() {
        // Given
        Set<String> existingUsernames = Set.of("John.Doe", "John.Doe1", "John.Doe2");
        Function<String, Boolean> usernameExistsChecker = existingUsernames::contains;

        // When
        String result = UserUtils.generateUniqueUsername(testUser, usernameExistsChecker);

        // Then
        assertEquals("John.Doe3", result);
    }

    @Test
    @DisplayName("Hash password should generate a non-empty and different hash")
    void hashPassword_ShouldGenerateValidHash() {
        // Given
        var password = "SecurePassword123";

        // When
        var hashedPassword = UserUtils.hashPassword(password);

        // Then
        assertNotNull(hashedPassword);
        assertNotEquals(hashedPassword, password);
    }

    @Test
    @DisplayName("Matches password should return true for correct password")
    void matchesPasswordHash_ShouldReturnTrueForCorrectPassword() {
        // Given
        var password = "SecurePassword123";
        var hashedPassword = UserUtils.hashPassword(password);

        // When
        var result = UserUtils.matchesPasswordHash(password, hashedPassword);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Matches password should return false for incorrect password")
    void matchesPasswordHash_ShouldReturnFalseForIncorrectPassword() {
        // Given
        var password = "SecurePassword123";
        var hashedPassword = UserUtils.hashPassword(password);
        var wrongPassword = "WrongPassword456";

        // When
        var result = UserUtils.matchesPasswordHash(wrongPassword, hashedPassword);

        // Then
        assertFalse(result);
    }
}