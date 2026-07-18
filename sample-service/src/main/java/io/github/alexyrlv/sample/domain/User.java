package io.github.alexyrlv.sample.domain;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Immutable user entity. Invariants are enforced on construction:
 * an instance that exists is always valid.
 */
public record User(String username, String firstName, String lastName, String email, LocalDate dateOfBirth) {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public User {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username must not be blank");
        }
        if (firstName == null) {
            throw new IllegalArgumentException("firstName must not be null");
        }
        if (lastName == null) {
            throw new IllegalArgumentException("lastName must not be null");
        }
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("email is not valid: " + email);
        }
    }
}
