package com.dabs.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.dabs.model.Appointment;
import com.dabs.model.DoctorSlot;

public interface AppointmentService {
    List<DoctorSlot> findAvailableSlotsForDoctor(int doctorId);

    List<DoctorSlot> findAvailableSlotsForDoctorAndDate(int doctorId, LocalDate date);

    List<Appointment> myAppointments(int patientId);

    List<Appointment> doctorAppointments(int doctorId);

    List<Appointment> allAppointments();

    Appointment findById(int id);


    String bookAppointment(int patientId,int slotId,String reason,Integer patientAge,String patientGender,String bloodGroup,Double weight);

    String cancelAppointment(int appointmentId, int requestingUserId);

    String completeAppointment(int appointmentId, int doctorId);

    String confirmAppointment(int appointmentId, int doctorId);

    String cancelAppointmentByDoctor(int appointmentId, int doctorId);

    String deleteSlot(int slotId, int doctorId);


    String createSlot(
        int doctorId,
        LocalDate slotDate,
        LocalTime startTime,
        LocalTime endTime);
}

