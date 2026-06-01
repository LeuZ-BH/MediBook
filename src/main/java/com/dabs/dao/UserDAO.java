package com.dabs.dao;

import java.util.List;

import com.dabs.model.User;

public interface UserDAO {
    void save(User user);

    User findById(int userId);

    User findByEmail(String email);

    List<User> findAll();

    void update(User user);

    void delete(User user);
}

