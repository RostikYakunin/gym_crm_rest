package com.crm.utils;

import com.crm.repositories.entities.User;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

import java.util.function.Function;

@UtilityClass
@Slf4j
public class UserUtils {
    private static final String USERNAME_SEPARATOR = ".";

    public static String generateUniqueUsername(
            User user,
            Function<String, Boolean> usernameExistsChecker
    ) {
        log.info("Stated creating unique username... ");

        var baseUsername = user.getFirstName() + USERNAME_SEPARATOR + user.getLastName();
        var uniqueUsername = baseUsername;
        var counter = 1;

        while (usernameExistsChecker.apply(uniqueUsername)) {
            log.info("Username=" + uniqueUsername + " already exists, starting generating new username... ");
            uniqueUsername = baseUsername + counter;
            counter++;
        }

        return uniqueUsername;
    }

    public static String hashPassword(String password) {
        log.info("Started hashing password... ");
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean matchesPasswordHash(String inputtedPassword, String passwordHash) {
        log.info("Started checking password and hash... ");
        return BCrypt.checkpw(inputtedPassword, passwordHash);
    }
}
