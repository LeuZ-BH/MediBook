package com.dabs.service;

import java.util.List;

import com.dabs.model.Doctor;
import com.dabs.model.Specialization;

public interface DoctorService {
    Doctor findByUserId(int userId);

    List<Doctor> searchDoctors(Integer specId, String name);

    List<Specialization> findAllSpecializations();

    void update(Doctor doctor);

    Doctor findById(int doctorId);
}

