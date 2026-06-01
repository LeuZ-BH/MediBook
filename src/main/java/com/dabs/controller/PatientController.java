package com.dabs.controller;

import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dabs.dao.SlotDAO;
import com.dabs.model.DoctorSlot;
import com.dabs.model.Appointment;
import com.dabs.model.Doctor;
import com.dabs.model.Specialization;
import com.dabs.model.User;
import com.dabs.model.enums.AppointmentStatus;
import com.dabs.model.enums.Role;
import com.dabs.service.AppointmentService;
import com.dabs.service.DoctorService;
import com.dabs.service.UserService;

@Controller
public class PatientController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private SlotDAO slotDAO;

    private Integer requirePatient(HttpSession session) {
        if (session.getAttribute("userId") == null) return null;
        if (!"PATIENT".equals(session.getAttribute("role"))) return null;
        return (Integer) session.getAttribute("userId");
    }

    @GetMapping("/patient/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Integer patientId = requirePatient(session);
        if (patientId == null) return "redirect:/login?error=access";

        List<Appointment> appointments = appointmentService.myAppointments(patientId);
        int upcoming = 0;
        for (Appointment a : appointments) {
            if (a.getStatus() == AppointmentStatus.PENDING || a.getStatus() == AppointmentStatus.CONFIRMED) {
                upcoming++;
            }
        }

        // Simple stats as service doesn’t provide dedicated DAO methods yet
        int doctorsAvailable = doctorService.searchDoctors(null, null).size();
        int specs = doctorService.findAllSpecializations().size();

        model.addAttribute("upcomingAppointments", upcoming);
        model.addAttribute("doctorsAvailable", doctorsAvailable);
        model.addAttribute("specializationsCount", specs);
        return "patient/dashboard";
    }

    @GetMapping("/patient/search")
    public String searchDoctors(
            HttpSession session,
            @RequestParam(required = false) Integer spec,
            @RequestParam(required = false) String name,
            Model model) {

        Integer patientId = requirePatient(session);
        if (patientId == null) return "redirect:/login?error=access";
        System.out.println("SPEC = " + spec);
        System.out.println("NAME = " + name);

        List<Doctor> doctors = doctorService.searchDoctors(spec, name);
        System.out.println("Doctors found: " + doctors.size());
        for (Doctor d : doctors) {
            System.out.println("Doctor = " + d.getDoctorId());
        }
        List<Specialization> specializations = doctorService.findAllSpecializations();
        model.addAttribute("doctors", doctors);
        model.addAttribute("specializations", specializations);
        model.addAttribute("selectedSpec", spec);
        model.addAttribute("searchName", name);
        return "patient/search-doctors";
    }

    @Transactional
    @GetMapping("/patient/doctor/{doctorId}/slots")
    public String doctorSlots(
            @PathVariable int doctorId,
            HttpSession session,
            Model model) {

        Integer patientId = requirePatient(session);
        if (patientId == null)
            return "redirect:/login?error=access";

        List<DoctorSlot> slots =
                slotDAO.findAvailableByDoctorId(doctorId);

        model.addAttribute("slots", slots);

        return "patient/doctor-slots";
    }

    @GetMapping("/patient/book/{slotId}")
    public String showBookForm(
            @PathVariable int slotId,
            HttpSession session,
            Model model) {

        Integer patientId = requirePatient(session);
        if (patientId == null) return "redirect:/login?error=access";

        // Load slot via doctor slots list (service has only per-doctor methods so brute query through all appointments not ideal).
        // For now, we pass slotId only and re-fetch from DAO via book endpoint.
        model.addAttribute("slotId", slotId);
        return "patient/book-appointment";
    }

    @PostMapping("/patient/book")
    public String bookAppointment(
            @RequestParam int slotId,
            @RequestParam String reason,
            HttpSession session,
            RedirectAttributes ra) {

        Integer patientId = requirePatient(session);
        if (patientId == null) return "redirect:/login?error=access";

        String res = appointmentService.bookAppointment(patientId, slotId, reason);
        if (res.startsWith("SUCCESS")) {
            ra.addFlashAttribute("success", "Appointment booked. Status: PENDING");
        } else {
            ra.addFlashAttribute("error", res.replace("ERROR: ", ""));
        }
        return "redirect:/patient/my-appointments";
    }

    @GetMapping("/patient/my-appointments")
    public String myAppointments(HttpSession session, Model model) {
        Integer patientId = requirePatient(session);
        if (patientId == null) return "redirect:/login?error=access";

        List<Appointment> appointments = appointmentService.myAppointments(patientId);

        System.out.println("Patient ID = " + patientId);
        System.out.println("Appointments found = " + appointments.size());

        model.addAttribute("appointments", appointments);
        return "patient/my-appointments";
    }

    @PostMapping("/patient/cancel/{id}")
    public String cancelAppointment(
            @PathVariable int id,
            HttpSession session,
            RedirectAttributes ra) {

        Integer patientId = requirePatient(session);
        if (patientId == null) return "redirect:/login?error=access";

        String res = appointmentService.cancelAppointment(id, patientId);
        if (res.startsWith("SUCCESS")) {
            ra.addFlashAttribute("success", "Appointment cancelled successfully");
        } else {
            ra.addFlashAttribute("error", res.replace("ERROR: ", ""));
        }
        return "redirect:/patient/my-appointments";
    }
}

