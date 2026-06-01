package com.dabs.service;

import java.util.List;

import com.dabs.model.User;

public interface UserService {
    void registerPatient(User user);

    User findById(int userId);

    User findByEmail(String email);

    User authenticate(String email, String passwordPlain);

    List<User> findAll();

    void delete(int userId);
}

