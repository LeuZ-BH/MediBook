package com.dabs.service;

import java.util.List;

import com.dabs.model.User;

public interface UserService {
    void registerPatient(User user);

    void registerDoctor(
        User user,
        Integer specId,
        String qualification,
        Integer experienceYears,
        String bio,
        String hospitalName,
        Double consultationFee,
        String addressLine,
        String city,
        String state,
        String country
    );

    User findById(int userId);

    User findByEmail(String email);

    User authenticate(String email, String passwordPlain);

    List<User> findAll();

    void delete(int userId);
}

