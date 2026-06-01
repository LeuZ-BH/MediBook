package com.dabs.dao;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dabs.model.DoctorSlot;
import com.dabs.model.enums.SlotStatus;

@Repository
public class SlotDAOImpl implements SlotDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session session() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void save(DoctorSlot slot) {
        session().save(slot);
    }

    @Override
    public DoctorSlot findById(int slotId) {
        return session().get(DoctorSlot.class, slotId);
    }

    @Override
    public List<DoctorSlot> findByDoctorId(int doctorId) {
        return session().createQuery(
        "FROM DoctorSlot s WHERE s.doctor.doctorId = :did " +
        "ORDER BY " +
        "CASE " +
        "WHEN s.status = com.dabs.model.enums.SlotStatus.PENDING THEN 1 " +
        "WHEN s.status = com.dabs.model.enums.SlotStatus.CONFIRMED THEN 2 " +
        "WHEN s.status = com.dabs.model.enums.SlotStatus.AVAILABLE THEN 3 " +
        "WHEN s.status = com.dabs.model.enums.SlotStatus.COMPLETED THEN 4 " +
        "WHEN s.status = com.dabs.model.enums.SlotStatus.CANCELLED THEN 5 " +
        "ELSE 6 END, " +
        "s.slotDate DESC, s.startTime ASC",
        DoctorSlot.class)
        .setParameter("did", doctorId)
        .getResultList();
    }

    @Override
    public List<DoctorSlot> findAvailableByDoctorId(int doctorId) {
        return session().createQuery(
                "FROM DoctorSlot s WHERE s.doctor.doctorId = :did AND s.status = :st ORDER BY s.slotDate ASC, s.startTime ASC",
                DoctorSlot.class)
                .setParameter("did", doctorId)
                .setParameter("st", SlotStatus.AVAILABLE)
                .getResultList();
    }

    @Override
    public List<DoctorSlot> findAvailableByDoctorAndDate(int doctorId, LocalDate date) {
        return session().createQuery(
                "FROM DoctorSlot s WHERE s.doctor.doctorId = :did AND s.slotDate = :d AND s.status = :st ORDER BY s.startTime ASC",
                DoctorSlot.class)
                .setParameter("did", doctorId)
                .setParameter("d", date)
                .setParameter("st", SlotStatus.AVAILABLE)
                .getResultList();
    }

    @Override
    public void update(DoctorSlot slot) {
        session().update(slot);
    }

    @Override
    public void delete(DoctorSlot slot) {
        session().delete(slot);
    }
}

