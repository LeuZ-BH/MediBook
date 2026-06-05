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
@Transactional
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorDAO doctorDAO;

    @Autowired
    private SpecializationDAO specializationDAO;

    @Override
    @Transactional
    public Doctor findByUserId(int userId) {

        System.out.println("SERVICE RECEIVED USER ID = " + userId);

        return doctorDAO.findByUserId(userId);
    }

    

    @Override
    @Transactional
    public List<Doctor> searchDoctors(Integer specId, String name) {

        List<Doctor> doctors = doctorDAO.findAll();

            System.out.println("TOTAL DOCTORS FROM DAO = " + doctors.size());

            for (Doctor d : doctors) {
                System.out.println(
                    "DoctorId=" + d.getDoctorId() +
                    ", User=" + (d.getUser() != null ? d.getUser().getName() : "NULL")
                );
            }

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
    public Doctor findById(int doctorId) {
        return doctorDAO.findById(doctorId);
    }


    @Override
    @Transactional
    public void update(Doctor doctor) {
        doctorDAO.update(doctor);
    }

    @Override
    @Transactional
    public List<Specialization> findAllSpecializations() {
        return specializationDAO.findAll();
    }
}

