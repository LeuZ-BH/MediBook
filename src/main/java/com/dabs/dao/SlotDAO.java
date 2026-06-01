package com.dabs.dao;

import java.time.LocalDate;
import java.util.List;

import com.dabs.model.DoctorSlot;

public interface SlotDAO {
    void save(DoctorSlot slot);

    DoctorSlot findById(int slotId);

    List<DoctorSlot> findByDoctorId(int doctorId);

    List<DoctorSlot> findAvailableByDoctorId(int doctorId);

    List<DoctorSlot> findAvailableByDoctorAndDate(int doctorId, LocalDate date);

    void update(DoctorSlot slot);

    void delete(DoctorSlot slot);
}

