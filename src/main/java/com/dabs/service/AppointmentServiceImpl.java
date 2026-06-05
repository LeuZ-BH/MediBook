package com.dabs.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.dabs.dao.DoctorDAO;
import com.dabs.model.Doctor;

import com.dabs.dao.AppointmentDAO;
import com.dabs.dao.SlotDAO;
import com.dabs.dao.UserDAO;
import com.dabs.model.Appointment;
import com.dabs.model.DoctorSlot;
import com.dabs.model.User;
import com.dabs.model.enums.AppointmentStatus;
import com.dabs.model.enums.SlotStatus;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentDAO appointmentDAO;

    @Autowired
    private DoctorDAO doctorDAO;

    @Autowired
    private SlotDAO slotDAO;

    @Autowired
    private UserDAO userDAO;

    @Override
    @Transactional
    public String createSlot(
            int doctorId,
            LocalDate slotDate,
            LocalTime startTime,
            LocalTime endTime) {

        Doctor doctor = doctorDAO.findById(doctorId);

        if (doctor == null) {
            return "ERROR: Doctor not found";
        }

        DoctorSlot slot = new DoctorSlot();

        slot.setDoctor(doctor);
        slot.setSlotDate(slotDate);
        slot.setStartTime(startTime);
        slot.setEndTime(endTime);
        slot.setStatus(SlotStatus.AVAILABLE);

        slotDAO.save(slot);

        return "SUCCESS";
    }


    @Override
    public Appointment findById(int id) {
        return appointmentDAO.findById(id);
    }

    @Override
    @Transactional
    public String deleteSlot(int slotId, int doctorId) {

        DoctorSlot slot = slotDAO.findById(slotId);

        if (slot == null) {
            return "ERROR: Slot not found";
        }

        if (slot.getDoctor().getDoctorId() != doctorId) {
            return "ERROR: Access denied";
        }

        List<Appointment> appointments = appointmentDAO.findBySlotId(slotId);

        if (!appointments.isEmpty()) {
            return "ERROR: Slot has appointment history";
        }

        slotDAO.delete(slot);
        return "SUCCESS";
    }

    @Override
    @Transactional
    public List<DoctorSlot> findAvailableSlotsForDoctor(int doctorId) {
        return slotDAO.findByDoctorId(doctorId);
    }

    @Override
    @Transactional
    public List<DoctorSlot> findAvailableSlotsForDoctorAndDate(int doctorId, LocalDate date) {
        return slotDAO.findAvailableByDoctorAndDate(doctorId, date);
    }

    @Override
    @Transactional
    public List<Appointment> myAppointments(int patientId) {
        return appointmentDAO.findByPatient(patientId);
    }

    @Override
    @Transactional
    public List<Appointment> doctorAppointments(int doctorId) {
        return appointmentDAO.findByDoctorId(doctorId);
    }

    @Override
    @Transactional
    public List<Appointment> allAppointments() {
        return appointmentDAO.findAll();
    }

    @Override
    @Transactional
    public String bookAppointment(int patientId,int slotId,String reason,Integer patientAge,String patientGender,String bloodGroup,Double weight) {
        DoctorSlot slot = slotDAO.findById(slotId);
        if (slot == null) {
            return "ERROR: Slot not found";
        }
        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            return "ERROR: Slot already booked";
        }

        Appointment appt = new Appointment();
        User patient = userDAO.findById(patientId);
        appt.setPatient(patient);
        appt.setSlot(slot);
        appt.setReason(reason);

        appt.setPatientAge(patientAge);
        appt.setPatientGender(patientGender);
        appt.setBloodGroup(bloodGroup);
        appt.setWeight(weight);

        appt.setStatus(AppointmentStatus.PENDING);
        appt.setBookedAt(LocalDateTime.now());

        slot.setStatus(SlotStatus.PENDING);
        slotDAO.update(slot);

        try {
        
        appointmentDAO.save(appt);

        slot.setStatus(SlotStatus.PENDING);
        slotDAO.update(slot);

    } catch (org.hibernate.exception.ConstraintViolationException ex) {
        return "ERROR: Slot already booked";
    }
        return "SUCCESS";
    }

    @Override
    @Transactional
    public String cancelAppointment(int appointmentId, int requestingUserId) {
        Appointment appt = appointmentDAO.findById(appointmentId);
        if (appt == null) {
            return "ERROR: Not found";
        }
        if (appt.getStatus() == AppointmentStatus.COMPLETED) {
            return "ERROR: Cannot cancel completed";
        }
        if (appt.getPatient().getUserId() != requestingUserId) {
            return "ERROR: Unauthorized";
        }

        appt.setStatus(AppointmentStatus.CANCELLED);
        appt.getSlot().setStatus(SlotStatus.CANCELLED);
        appointmentDAO.update(appt);
        return "SUCCESS";
    }


    @Override
    @Transactional
    public String confirmAppointment(int appointmentId, int doctorId) {

        Appointment appt = appointmentDAO.findById(appointmentId);

        if (appt == null) {
            return "ERROR: Not found";
        }

        if (appt.getStatus() != AppointmentStatus.PENDING) {
            return "ERROR: Only pending appointments can be confirmed";
        }

        if (appt.getSlot().getDoctor().getDoctorId() != doctorId) {
            return "ERROR: Unauthorized";
        }

        appt.setStatus(AppointmentStatus.CONFIRMED);

        DoctorSlot slot = appt.getSlot();
        slot.setStatus(SlotStatus.CONFIRMED);

        slotDAO.update(slot);
        appointmentDAO.update(appt);

        return "SUCCESS";
    }


    @Override
    @Transactional
    public String completeAppointment(int appointmentId, int doctorId) {
        Appointment appt = appointmentDAO.findById(appointmentId);
        if (appt == null) {
            return "ERROR: Not found";
        }
       if (appt.getStatus() != AppointmentStatus.CONFIRMED) {
            return "ERROR: Only confirmed appointments can be completed";
        }
        if (appt.getSlot().getDoctor().getDoctorId() != doctorId) {
            return "ERROR: Unauthorized";
        }

        appt.setStatus(AppointmentStatus.COMPLETED);

        DoctorSlot slot = appt.getSlot();
        slot.setStatus(SlotStatus.COMPLETED);

        slotDAO.update(slot);
        appointmentDAO.update(appt);
        return "SUCCESS";
    }

    @Override
    @Transactional
    public String cancelAppointmentByDoctor(int appointmentId, int doctorId) {

        Appointment appt = appointmentDAO.findById(appointmentId);

        if (appt == null) {
            return "ERROR: Not found";
        }

        if (appt.getSlot().getDoctor().getDoctorId() != doctorId) {
            return "ERROR: Unauthorized";
        }

        if (appt.getStatus() == AppointmentStatus.COMPLETED) {
            return "ERROR: Cannot cancel completed appointment";
        }

        if (appt.getStatus() == AppointmentStatus.CANCELLED) {
            return "ERROR: Already cancelled";
        }

        appt.setStatus(AppointmentStatus.CANCELLED);
        appt.getSlot().setStatus(SlotStatus.AVAILABLE);

        appointmentDAO.update(appt);

        return "SUCCESS";
    }
}

