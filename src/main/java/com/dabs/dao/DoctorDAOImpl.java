package com.dabs.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dabs.model.Doctor;

@Repository
public class DoctorDAOImpl implements DoctorDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session session() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void save(Doctor doctor) {
        session().save(doctor);
    }

    @Override
    public Doctor findById(int doctorId) {
        return session().get(Doctor.class, doctorId);
    }

    @Override
    public Doctor findByUserId(int userId) {
        return session()
                .createQuery("FROM Doctor d WHERE d.user.userId = :uid", Doctor.class)
                .setParameter("uid", userId)
                .uniqueResult();
    }

    @Override
    public List<Doctor> findAll() {
        return session().createQuery("FROM Doctor", Doctor.class).getResultList();
    }

    @Override
    public void update(Doctor doctor) {
        session().update(doctor);
    }

    @Override
    public void delete(Doctor doctor) {
        session().delete(doctor);
    }
}

