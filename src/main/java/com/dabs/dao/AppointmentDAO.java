package com.dabs.dao;

import java.util.List;

import com.dabs.model.Appointment;

public interface AppointmentDAO {
    void save(Appointment appt);

    Appointment findById(int id);

    List<Appointment> findByPatient(int patientId);

    List<Appointment> findByDoctorId(int doctorId);

    List<Appointment> findAll();

    void update(Appointment appt);

    void delete(Appointment appt);

    List<Appointment> findBySlotId(int slotId);
}

