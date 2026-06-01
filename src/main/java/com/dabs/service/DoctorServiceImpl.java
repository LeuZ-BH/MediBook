package com.dabs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dabs.dao.DoctorDAO;
import com.dabs.dao.SpecializationDAO;
import com.dabs.model.Doctor;
import com.dabs.model.Specialization;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorDAO doctorDAO;

    @Autowired
    private SpecializationDAO specializationDAO;

    @Override
    @Transactional
    public Doctor findByUserId(int userId) {
        return doctorDAO.findByUserId(userId);
    }

    @Override
    @Transactional
    public List<Doctor> searchDoctors(Integer specId, String name) {

        List<Doctor> doctors = doctorDAO.findAll();

        if (specId != null) {
            doctors.removeIf(d ->
                d.getSpecialization() == null ||
                d.getSpecialization().getSpecId() != specId);
        }

        if (name != null && !name.trim().isEmpty()) {
            String search = name.toLowerCase();

            doctors.removeIf(d ->
                d.getUser() == null ||
                d.getUser().getName() == null ||
                !d.getUser().getName().toLowerCase().contains(search));
        }

        return doctors;
    }

    @Override
    @Transactional
    public List<Specialization> findAllSpecializations() {
        return specializationDAO.findAll();
    }
}

