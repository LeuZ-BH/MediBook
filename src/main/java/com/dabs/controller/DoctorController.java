package com.dabs.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dabs.model.Appointment;
import com.dabs.model.DoctorSlot;
import com.dabs.model.enums.AppointmentStatus;
import com.dabs.model.enums.SlotStatus;
import com.dabs.service.AppointmentService;
import com.dabs.service.DoctorService;
import com.dabs.service.UserService;

@Controller
public class DoctorController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorService doctorService;

    private Integer requireDoctor(HttpSession session) {
        if (session.getAttribute("userId") == null) return null;
        if (!"DOCTOR".equals(session.getAttribute("role"))) return null;
        if (session.getAttribute("doctorId") == null) return null;
        return (Integer) session.getAttribute("doctorId");
    }

    @GetMapping("/doctor/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Integer doctorId = requireDoctor(session);
        if (doctorId == null) return "redirect:/login?error=access";

        // Stats approximated using appointment list
        List<Appointment> all = appointmentService.allAppointments();
        int totalSlots = appointmentService.findAvailableSlotsForDoctor(doctorId).size();
        int bookedSlots = 0;
        int availableSlots = totalSlots;

        for (Appointment a : all) {
            if (a.getSlot().getDoctor().getDoctorId() == doctorId) {
                if (a.getStatus() == AppointmentStatus.CONFIRMED || a.getStatus() == AppointmentStatus.PENDING) {
                    bookedSlots++;
                }
            }
        }

        model.addAttribute("totalSlots", totalSlots);
        model.addAttribute("bookedSlots", bookedSlots);
        model.addAttribute("availableSlots", availableSlots);


        model.addAttribute("appointments", all);
        model.addAttribute("activePage", "dashboard");
        return "doctor/dashboard";
    }

    @GetMapping("/doctor/slots")
    public String slots(HttpSession session, Model model) {

        Integer doctorId = requireDoctor(session);

        if (doctorId == null)
            return "redirect:/login?error=access";

        List<DoctorSlot> slots =
                appointmentService.findAvailableSlotsForDoctor(doctorId);

        List<Appointment> appointments =
                appointmentService.doctorAppointments(doctorId);

        model.addAttribute("slots", slots);
        model.addAttribute("appointments", appointments);
        model.addAttribute("activePage", "slots");

        return "doctor/manage-slots";
    }

    

    @GetMapping("/doctor/slots/add")
    public String addSlotForm(HttpSession session) {
        Integer doctorId = requireDoctor(session);
        if (doctorId == null) return "redirect:/login?error=access";
        return "doctor/manage-slots";
    }

    @PostMapping("/doctor/slots/add")
    public String addSlot(
            HttpSession session,
            @RequestParam String slotDate,
            @RequestParam String startTime,
            @RequestParam String endTime,
            RedirectAttributes ra) {
        Integer doctorId = requireDoctor(session);
        if (doctorId == null) return "redirect:/login?error=access";

        LocalDate date = LocalDate.parse(slotDate);
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);

        String result = appointmentService.createSlot(
                doctorId,
                date,
                start,
                end);

        if ("SUCCESS".equals(result)) {
            ra.addFlashAttribute("success", "Slot created successfully");
        } else {
            ra.addFlashAttribute("error", result);
        }

        return "redirect:/doctor/slots";
    }

    @PostMapping("/doctor/slots/delete/{id}")
    public String deleteSlot(
            @PathVariable int id,
            HttpSession session,
            RedirectAttributes ra) {

        Integer doctorId = requireDoctor(session);

        if (doctorId == null) {
            return "redirect:/login?error=access";
        }

        String res = appointmentService.deleteSlot(id, doctorId);

        if (res.startsWith("SUCCESS")) {
            ra.addFlashAttribute(
                    "success",
                    "Slot deleted successfully");
        } else {
            ra.addFlashAttribute(
                    "error",
                    res.replace("ERROR: ", ""));
        }

        return "redirect:/doctor/slots";
    }

    @GetMapping("/doctor/appointments")
    public String myAppointments(HttpSession session, Model model) {

        Integer doctorId = requireDoctor(session);
        if (doctorId == null)
            return "redirect:/login?error=access";

        List<Appointment> all =
                appointmentService.doctorAppointments(doctorId);

        model.addAttribute("appointments", all);
        model.addAttribute("activePage", "appointments");

        return "doctor/my-appointments";
    }

    @PostMapping("/doctor/appointments/confirm/{id}")
    public String confirm(
            @PathVariable int id,
            HttpSession session,
            RedirectAttributes ra) {

        Integer doctorId = requireDoctor(session);

        if (doctorId == null) {
            return "redirect:/login?error=access";
        }

        String res =
                appointmentService.confirmAppointment(id, doctorId);

        if (res.startsWith("SUCCESS")) {
            ra.addFlashAttribute(
                    "success",
                    "Appointment confirmed");
        } else {
            ra.addFlashAttribute(
                    "error",
                    res.replace("ERROR: ", ""));
        }

        return "redirect:/doctor/appointments";
    }

    @PostMapping("/doctor/appointments/cancel/{id}")
    public String cancel(
            @PathVariable int id,
            HttpSession session,
            RedirectAttributes ra) {

        Integer doctorId = requireDoctor(session);

        if (doctorId == null) {
            return "redirect:/login?error=access";
        }

        String res =
                appointmentService.cancelAppointmentByDoctor(id, doctorId);

        if (res.startsWith("SUCCESS")) {
            ra.addFlashAttribute(
                    "success",
                    "Appointment cancelled");
        } else {
            ra.addFlashAttribute(
                    "error",
                    res.replace("ERROR: ", ""));
        }

        return "redirect:/doctor/appointments";
    }

    @PostMapping("/doctor/appointments/complete/{id}")
    public String complete(
            @PathVariable int id,
            HttpSession session,
            RedirectAttributes ra) {
        Integer doctorId = requireDoctor(session);
        if (doctorId == null) return "redirect:/login?error=access";

        String res = appointmentService.completeAppointment(id, doctorId);
        if (res.startsWith("SUCCESS")) ra.addFlashAttribute("success", "Appointment marked COMPLETED");
        else ra.addFlashAttribute("error", res.replace("ERROR: ", ""));

        return "redirect:/doctor/appointments";
    }
}

