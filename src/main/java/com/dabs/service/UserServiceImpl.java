package com.dabs.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.dabs.dao.DoctorDAO;
import com.dabs.model.Doctor;
import com.dabs.dao.SpecializationDAO;
import com.dabs.model.Specialization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dabs.dao.UserDAO;
import com.dabs.model.User;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private DoctorDAO doctorDAO;

    @Autowired
    private SpecializationDAO specializationDAO;

    private String hash(String plain) {
        return com.dabs.util.PasswordUtil.hash(plain);
    }


    @Override
    @Transactional
    public void registerPatient(User user) {
        if (user.getPasswordHash() != null) {
            
        }
        userDAO.save(user);
    }

    @Override
    @Transactional
    public void registerDoctor(
        User user, Integer specId,String qualification,Integer experienceYears,String bio,
        String hospitalName,Double consultationFee,String addressLine,String city,String state,String country) {

        userDAO.save(user);

        Doctor doctor = new Doctor();

        doctor.setUser(user);

        doctor.setQualification(qualification);
        doctor.setExperienceYears(experienceYears);
        doctor.setBio(bio);
        doctor.setHospitalName(hospitalName);
        doctor.setConsultationFee(consultationFee);
        doctor.setAddressLine(addressLine);
        doctor.setCity(city);
        doctor.setState(state);
        doctor.setCountry(country);

        Specialization specialization = null;

        if(specId != null){
            specialization = specializationDAO.findById(specId);
        }

        doctor.setSpecialization(specialization);
        doctorDAO.save(doctor);
    }

   
    @Override
    @Transactional
    public User findById(int userId) {
        return userDAO.findById(userId);
    }

    @Override
    @Transactional
    public User findByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    @Override
    @Transactional
    public User authenticate(String email, String passwordPlain) {
        User user = userDAO.findByEmail(email);
        if (user == null) return null;
        String hashed = hash(passwordPlain);
        if (user.getPasswordHash() == null) return null;
        if (!user.getPasswordHash().equals(hashed)) return null;
        return user;
    }

    @Override
    @Transactional
    public List<User> findAll() {
        return userDAO.findAll();
    }

    @Override
    @Transactional
    public void delete(int userId) {
        User u = userDAO.findById(userId);
        if (u != null) {
            userDAO.delete(u);
        }
    }
}

