package com.dabs.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dabs.model.Appointment;
import com.dabs.model.Doctor;
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
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null)
            return "redirect:/login?error=access";

        Doctor doctor = doctorService.findByUserId(userId);

        if (doctor == null)
            return "redirect:/login?error=access";

        Integer doctorId = doctor.getDoctorId();

        // Stats approximated using appointment list
        List<Appointment> all = appointmentService.doctorAppointments(doctorId);

        System.out.println("Doctor ID = " + doctorId);
        System.out.println("Appointment Count = " + all.size());

        for (Appointment a : all) {
            System.out.println(
                "Appt = " + a.getAppointmentId()
                + " Status = " + a.getStatus()
            );
        }

        List<String> recentActivities = new ArrayList<>();


        all.stream()
            .sorted((a, b) ->
                b.getBookedAt().compareTo(a.getBookedAt()))
            .limit(5)
            .forEach(a -> {

                String activity;

                if (a.getStatus() == AppointmentStatus.COMPLETED) {

                    activity =
                        "Completed appointment for "
                        + a.getPatient().getName();

                } else if (a.getStatus() == AppointmentStatus.CONFIRMED) {

                    activity =
                        "Confirmed appointment for "
                        + a.getPatient().getName();

                } else if (a.getStatus() == AppointmentStatus.CANCELLED) {

                    activity =
                        "Cancelled appointment for "
                        + a.getPatient().getName();

                } else {

                    activity =
                        "New booking from "
                        + a.getPatient().getName();
                }

                recentActivities.add(activity);
            });


        int totalSlots = appointmentService.findAvailableSlotsForDoctor(doctorId).size();
        int bookedSlots = 0;
        int availableSlots = totalSlots;

        int pendingCount = 0;
        int confirmedCount = 0;
        int completedCount = 0;
        int cancelledCount = 0;

        for (Appointment a : all) {

            if (a.getStatus() == AppointmentStatus.PENDING)
                pendingCount++;

            if (a.getStatus() == AppointmentStatus.CONFIRMED)
                confirmedCount++;

            if (a.getStatus() == AppointmentStatus.COMPLETED)
                completedCount++;

            if (a.getStatus() == AppointmentStatus.CANCELLED)
                cancelledCount++;
        }

        model.addAttribute("totalSlots", totalSlots);
        model.addAttribute("bookedSlots", bookedSlots);
        model.addAttribute("availableSlots", availableSlots);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("confirmedCount", confirmedCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("cancelledCount", cancelledCount);

        model.addAttribute("recentActivities",recentActivities);
        model.addAttribute("appointments", all);
        model.addAttribute("activePage", "dashboard");
        model.addAttribute("doctor", doctor);
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
    public String myAppointments(
            @RequestParam(required = false) String status,
            HttpSession session,
            Model model) {

        Integer doctorId = requireDoctor(session);

        if (doctorId == null) {
            return "redirect:/login?error=access";
        }

        List<Appointment> all =
                appointmentService.doctorAppointments(doctorId);

        if (status != null && !status.isEmpty()) {
            all = all.stream()
                    .filter(a -> a.getStatus().name().equals(status))
                    .collect(Collectors.toList());

            model.addAttribute("selectedStatus", status);
        }

        model.addAttribute("appointments", all);
        model.addAttribute("activePage", "appointments");

        return "doctor/my-appointments";
    }

    @GetMapping("/doctor/history")
    public String history(
            @RequestParam(required = false) 
            String status, HttpSession session,Model model) { 

        Integer userId =
            (Integer) session.getAttribute("userId");

        if (userId == null)
            return "redirect:/login";

        Doctor doctor =
            doctorService.findByUserId(userId);

        if (doctor == null)
            return "redirect:/login";

        List<Appointment> history =
            appointmentService.doctorAppointments(
                doctor.getDoctorId()
            );

        if(status != null && !status.isEmpty()) {
        history = history.stream()
                .filter(a ->
                    a.getStatus().name().equals(status))
                .collect(Collectors.toList());
        }    

        System.out.println("History Size = " + history.size());

        System.out.println("History Size = " + history.size());

            for (Appointment a : history) {
                System.out.println(
                    "Appt=" + a.getAppointmentId()
                );
            }

        long totalCount = history.size();

        long completedCount = history.stream()
                .filter(a -> a.getStatus().name().equals("COMPLETED"))
                .count();

        long cancelledCount = history.stream()
                .filter(a -> a.getStatus().name().equals("CANCELLED"))
                .count();

        long pendingCount = history.stream()
                .filter(a -> a.getStatus().name().equals("PENDING"))
                .count();

        long confirmedCount = history.stream()
                .filter(a -> a.getStatus().name().equals("CONFIRMED"))
                .count();

        model.addAttribute("totalCount", totalCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("cancelledCount", cancelledCount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("confirmedCount", confirmedCount);

        // Sorting History ( recent first)
        history.sort((a, b) ->
        b.getBookedAt().compareTo(a.getBookedAt()));
        // 

        model.addAttribute("history", history);
        model.addAttribute("activePage", "history");
        model.addAttribute("selectedStatus", status);

        return "doctor/history";
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

    @GetMapping("/doctor/profile")
    public String profile(HttpSession session, Model model) {

        Integer userId = requireDoctor(session);

        Doctor doctor = doctorService.findByUserId(userId);


        if (doctor != null) {
            System.out.println("DOCTOR ID = " + doctor.getDoctorId());

            if (doctor.getUser() != null) {
                System.out.println("DOCTOR USER = " + doctor.getUser().getName());
            } else {
                System.out.println("DOCTOR USER IS NULL");
            }
        }


        model.addAttribute("doctor", doctor);
        model.addAttribute("debugUserId", doctor.getUser().getUserId());
        model.addAttribute("debugUserName", doctor.getUser().getName());

        model.addAttribute("doctor", doctor);
        model.addAttribute("debugUserId", userId);

        return "doctor/profile";
    }

    @GetMapping("/doctor/profile/edit")
    public String editProfile(HttpSession session, Model model) {

        Integer userId = (Integer) session.getAttribute("userId");

        Doctor doctor = doctorService.findByUserId(userId);

        model.addAttribute("doctor", doctor);

        return "doctor/edit-profile";
    }

    @PostMapping("/doctor/profile/update")
    public String updateProfile(@ModelAttribute Doctor doctor) {

        Doctor existing =
                doctorService.findById(doctor.getDoctorId());

        existing.setQualification(doctor.getQualification());
        existing.setExperienceYears(doctor.getExperienceYears());
        existing.setConsultationFee(doctor.getConsultationFee());
        existing.setHospitalName(doctor.getHospitalName());
        existing.setCity(doctor.getCity());
        existing.setState(doctor.getState());
        existing.setCountry(doctor.getCountry());
        existing.setAddressLine(doctor.getAddressLine());
        existing.setBio(doctor.getBio());

        doctorService.update(existing);

        return "redirect:/doctor/dashboard";
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

    @GetMapping("/doctor/appointment-details/{id}")
    public String viewAppointmentDetails(
            @PathVariable int id,
            Model model) {

        Appointment appt = appointmentService.findById(id);

        model.addAttribute("appointment", appt);

        return "doctor/appointment-details";
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

