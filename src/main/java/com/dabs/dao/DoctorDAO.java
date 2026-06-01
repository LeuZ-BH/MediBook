package com.dabs.dao;

import java.util.List;

import com.dabs.model.Doctor;

public interface DoctorDAO {
    void save(Doctor doctor);

    Doctor findById(int doctorId);

    Doctor findByUserId(int userId);

    List<Doctor> findAll();

    void update(Doctor doctor);

    void delete(Doctor doctor);
}

