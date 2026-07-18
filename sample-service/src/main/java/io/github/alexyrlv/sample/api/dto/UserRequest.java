package io.github.alexyrlv.sample.api.dto;

import io.github.alexyrlv.sample.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UserRequest(
        @NotBlank String username,
        @NotNull String firstName,
        @NotNull String lastName,
        @NotBlank @Email String email,
        LocalDate dateOfBirth) {

    public User toDomain() {
        return new User(username, firstName, lastName, email, dateOfBirth);
    }
}
