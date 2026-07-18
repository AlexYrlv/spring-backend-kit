package io.github.alexyrlv.sample.repository;

import io.github.alexyrlv.sample.domain.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Domain-typed port of the persistence layer: accepts and returns
 * {@link User} records, keeps JPA entities as an internal detail.
 */
@Component
public class UserStorage {

    private final UserJpaRepository repository;

    public UserStorage(UserJpaRepository repository) {
        this.repository = repository;
    }

    public List<User> findAll() {
        return repository.findAll().stream().map(UserEntity::toDomain).toList();
    }

    public Optional<User> find(String username) {
        return repository.findById(username).map(UserEntity::toDomain);
    }

    public boolean exists(String username) {
        return repository.existsById(username);
    }

    public User save(User user) {
        return repository.save(UserEntity.fromDomain(user)).toDomain();
    }

    public boolean delete(String username) {
        if (!repository.existsById(username)) {
            return false;
        }
        repository.deleteById(username);
        return true;
    }
}
