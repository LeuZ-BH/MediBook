package com.dabs.controller;

import java.security.Principal;
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

import com.dabs.model.PatientNote;
import java.time.LocalDateTime;
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
import com.dabs.service.PatientNoteService;
import com.dabs.service.UserService;

@Controller
public class PatientController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PatientNoteService patientNoteService;

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

        Appointment nextAppointment = null;

        for (Appointment a : appointments) {

            if (a.getStatus() == AppointmentStatus.PENDING
                    || a.getStatus() == AppointmentStatus.CONFIRMED) {

                if (nextAppointment == null
                        || a.getSlot().getSlotDate().isBefore(
                                nextAppointment.getSlot().getSlotDate())) {

                    nextAppointment = a;
                }
            }
        }
        int total = appointments.size();
        int upcoming = 0;
        int completed = 0;
        int cancelled = 0;

        for (Appointment a : appointments) {

            if (a.getStatus() == AppointmentStatus.PENDING
                    || a.getStatus() == AppointmentStatus.CONFIRMED) {
                upcoming++;
            }

            if (a.getStatus() == AppointmentStatus.COMPLETED) {
                completed++;
            }

            if (a.getStatus() == AppointmentStatus.CANCELLED) {
                cancelled++;
            }
        }

        int doctorsAvailable = doctorService.searchDoctors(null, null).size();
        int specs = doctorService.findAllSpecializations().size();

        model.addAttribute("recentAppointments",
            appointments.stream()
                    .limit(5)
                    .toList()
        );

        model.addAttribute(
            "patientNotes",
            patientNoteService.getPatientNotes(patientId)
        );

        model.addAttribute("nextAppointment", nextAppointment);
        model.addAttribute("totalAppointments", total);
        model.addAttribute("upcomingAppointments", upcoming);
        model.addAttribute("completedAppointments", completed);
        model.addAttribute("cancelledAppointments", cancelled);
        model.addAttribute("upcomingAppointments", upcoming);
        model.addAttribute("doctorsAvailable", doctorsAvailable);
        model.addAttribute("specializationsCount", specs);
        return "patient/dashboard";
    }

    @PostMapping("/patient/notes/save")
    public String saveNote(
            @RequestParam("noteText") String noteText,
            HttpSession session) {

        Integer patientId = requirePatient(session);

        if (patientId == null) {
            return "redirect:/login";
        }

        User patient = userService.findById(patientId.intValue());

        PatientNote note = new PatientNote();
        note.setPatient(patient);
        note.setNoteText(noteText);
        note.setCreatedAt(LocalDateTime.now());

        patientNoteService.saveNote(note);

        return "redirect:/patient/dashboard";
    }

    @GetMapping("/patient/notes/edit/{id}")
    public String editNotePage(
            @PathVariable Integer id,
            Model model) {

        PatientNote note = patientNoteService.getNote(id);

        model.addAttribute("note", note);

        return "patient/edit-note";
    }

    @PostMapping("/patient/notes/update")
    public String updateNote(
            @RequestParam Integer noteId,
            @RequestParam String noteText) {

        PatientNote note = patientNoteService.getNote(noteId);

        note.setNoteText(noteText);

        patientNoteService.updateNote(note);

        return "redirect:/patient/dashboard";
    }
    
    @GetMapping("/patient/notes/delete/{id}")
    public String deleteNote(
            @PathVariable Integer id) {

        patientNoteService.deleteNote(id);

        return "redirect:/patient/dashboard";
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

    @GetMapping("/patient/history")
    public String history(HttpSession session, Model model) {

        Integer patientId = requirePatient(session);

        if (patientId == null) {
            return "redirect:/login?error=access";
        }

        List<Appointment> appointments =
                appointmentService.myAppointments(patientId);

        model.addAttribute("appointments", appointments);

        return "patient/history";
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

        model.addAttribute("slotId", slotId);
        return "patient/book-appointment";
    }

    @PostMapping("/patient/book")
    public String bookAppointment(
            @RequestParam int slotId,
            @RequestParam String reason,
            @RequestParam Integer patientAge,
            @RequestParam String patientGender,
            @RequestParam String bloodGroup,
            @RequestParam Double weight,
            HttpSession session,
            RedirectAttributes ra) {

        Integer patientId = requirePatient(session);
        if (patientId == null) return "redirect:/login?error=access";

        String res = appointmentService.bookAppointment( patientId ,slotId,reason,patientAge,patientGender,bloodGroup,weight);
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

