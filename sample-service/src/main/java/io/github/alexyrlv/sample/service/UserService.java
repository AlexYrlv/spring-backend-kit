package io.github.alexyrlv.sample.service;

import io.github.alexyrlv.sample.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory user store. Thread-safe; data is lost on restart.
 * Persistence arrives with the repository layer (PostgreSQL + Spring Data).
 */
@Service
public class UserService {

    private final ConcurrentHashMap<String, User> store = new ConcurrentHashMap<>();

    public List<User> list() {
        return List.copyOf(store.values());
    }

    public Optional<User> get(String username) {
        return Optional.ofNullable(store.get(username));
    }

    public User create(User user) {
        if (store.putIfAbsent(user.username(), user) != null) {
            throw new UserAlreadyExistsException(user.username());
        }
        return user;
    }

    public Optional<User> update(String username, User user) {
        if (!username.equals(user.username())) {
            throw new IllegalArgumentException("Username mismatch: " + username + " vs " + user.username());
        }
        return Optional.ofNullable(store.computeIfPresent(username, (key, existing) -> user));
    }

    public boolean delete(String username) {
        return store.remove(username) != null;
    }
}
