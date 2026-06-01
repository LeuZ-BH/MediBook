package com.dabs.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dabs.model.Appointment;

@Repository
public class AppointmentDAOImpl implements AppointmentDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session session() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void save(Appointment appt) {
        session().save(appt);
    }

    @Override
    public Appointment findById(int id) {
        return session().get(Appointment.class, id);
    }

    public List<Appointment> findByPatient(int patientId) {
    return session().createQuery(
            "FROM Appointment a WHERE a.patient.userId = :pid ORDER BY a.bookedAt DESC",
            Appointment.class)
            .setParameter("pid", patientId)
            .getResultList();
}

    @Override
    public List<Appointment> findByDoctorId(int doctorId) {
        return session().createQuery(
                "FROM Appointment a WHERE a.slot.doctor.doctorId = :did ORDER BY a.bookedAt DESC",
                Appointment.class)
                .setParameter("did", doctorId)
                .getResultList();
    }

    @Override
    public List<Appointment> findAll() {
        return session().createQuery("FROM Appointment", Appointment.class).getResultList();
    }

    @Override
    public void update(Appointment appt) {
        session().update(appt);
    }

    @Override
    public List<Appointment> findBySlotId(int slotId) {
        return session().createQuery(
                "FROM Appointment a WHERE a.slot.slotId = :sid",
                Appointment.class)
                .setParameter("sid", slotId)
                .getResultList();
    }

    @Override
    public void delete(Appointment appt) {
        session().delete(appt);
    }
}

