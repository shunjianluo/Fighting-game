package com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserStore {

    private static final UserStore INSTANCE = new UserStore();

    private final List<User> users = new ArrayList<>();

    private UserStore() {
    }

    public static UserStore getInstance() {
        return INSTANCE;
    }

    public boolean usernameExists(String username) {
        return users.stream().anyMatch(user -> user.getUsername().equals(username));
    }

    public boolean userIdExists(String userId) {
        return users.stream().anyMatch(user -> user.getUserId().equals(userId));
    }

    public void add(User user) {
        users.add(user);
    }

    public Optional<User> login(String username, String password) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .filter(user -> user.getPassword().equals(password))
                .findFirst();
    }
}