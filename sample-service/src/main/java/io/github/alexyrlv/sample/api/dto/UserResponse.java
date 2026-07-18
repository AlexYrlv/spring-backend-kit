package io.github.alexyrlv.sample.api.dto;

import io.github.alexyrlv.sample.domain.User;

import java.time.LocalDate;

public record UserResponse(String username, String firstName, String lastName, String email, LocalDate dateOfBirth) {

    public static UserResponse from(User user) {
        return new UserResponse(user.username(), user.firstName(), user.lastName(), user.email(), user.dateOfBirth());
    }
}
