package io.github.alexyrlv.sample.service;

import io.github.alexyrlv.sample.domain.User;
import io.github.alexyrlv.sample.repository.UserStorage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserStorage storage;

    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public List<User> list() {
        return storage.findAll();
    }

    public Optional<User> get(String username) {
        return storage.find(username);
    }

    @Transactional
    public User create(User user) {
        if (storage.exists(user.username())) {
            throw new UserAlreadyExistsException(user.username());
        }
        return storage.save(user);
    }

    @Transactional
    public Optional<User> update(String username, User user) {
        if (!username.equals(user.username())) {
            throw new IllegalArgumentException("Username mismatch: " + username + " vs " + user.username());
        }
        if (!storage.exists(username)) {
            return Optional.empty();
        }
        return Optional.of(storage.save(user));
    }

    @Transactional
    public boolean delete(String username) {
        return storage.delete(username);
    }
}
