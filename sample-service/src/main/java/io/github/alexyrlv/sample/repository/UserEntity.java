package io.github.alexyrlv.sample.repository;

import io.github.alexyrlv.sample.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * JPA representation of a user row. Package-private on purpose:
 * the entity never leaves the repository layer — the rest of the
 * service works with the immutable {@link User} record.
 */
@Entity
@Table(name = "users")
class UserEntity {

    @Id
    private String username;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    protected UserEntity() {
        // required by JPA
    }

    static UserEntity fromDomain(User user) {
        UserEntity entity = new UserEntity();
        entity.username = user.username();
        entity.firstName = user.firstName();
        entity.lastName = user.lastName();
        entity.email = user.email();
        entity.dateOfBirth = user.dateOfBirth();
        return entity;
    }

    User toDomain() {
        return new User(username, firstName, lastName, email, dateOfBirth);
    }
}
