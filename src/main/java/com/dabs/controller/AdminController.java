package com.dabs.controller;

import java.time.LocalDate;
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
import org.springframework.transaction.annotation.Transactional;
import com.dabs.model.Appointment;
import com.dabs.model.Doctor;
import com.dabs.model.Specialization;
import com.dabs.model.User;
import com.dabs.model.enums.AppointmentStatus;
import com.dabs.model.enums.Role;
import com.dabs.service.AppointmentService;
import com.dabs.service.DoctorService;
import com.dabs.service.UserService;
import com.dabs.dao.SpecializationDAO;
import com.dabs.dao.AppointmentDAO;

@Controller
@Transactional
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private com.dabs.dao.SpecializationDAO specializationDAO;

    private boolean requireAdmin(HttpSession session) {
        if (session.getAttribute("userId") == null) return false;
        if (!"ADMIN".equals(session.getAttribute("role"))) return false;
        return true;
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!requireAdmin(session)) return "redirect:/login?error=access";

        List<User> users = userService.findAll();
        int patients = 0;
        int doctors = 0;
        for (User u : users) {
            if (u.getRole() == Role.PATIENT) patients++;
            if (u.getRole() == Role.DOCTOR) doctors++;
        }

        int apptToday = 0;
        List<Appointment> allAppts = appointmentService.allAppointments();
        LocalDate today = LocalDate.now();
        for (Appointment a : allAppts) {
            if (a.getSlot() != null && a.getSlot().getSlotDate() != null && a.getSlot().getSlotDate().equals(today)) {
                apptToday++;
            }
        }

        int specs = specializationDAO.findAll().size();

        model.addAttribute("totalPatients", patients);
        model.addAttribute("totalDoctors", doctors);
        model.addAttribute("appointmentsToday", apptToday);
        model.addAttribute("totalSpecializations", specs);

        return "admin/dashboard";
    }

    @GetMapping("/admin/users")
    public String allUsers(HttpSession session, Model model) {
        if (!requireAdmin(session)) return "redirect:/login?error=access";
        model.addAttribute("users", userService.findAll());
        return "admin/all-users";
    }

    @PostMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable int id, HttpSession session, RedirectAttributes ra) {
        if (!requireAdmin(session)) return "redirect:/login?error=access";
        userService.delete(id);
        ra.addFlashAttribute("success", "User deleted");
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/appointments")
    public String allAppointments(
            HttpSession session,
            @RequestParam(required = false) AppointmentStatus status,
            Model model) {
        if (!requireAdmin(session)) return "redirect:/login?error=access";

        List<Appointment> appts = appointmentService.allAppointments();
        if (status != null) {
            appts.removeIf(a -> a.getStatus() != status);
        }
        model.addAttribute("appointments", appts);
        model.addAttribute("statusFilter", status);
        return "admin/all-appointments";
    }

    @GetMapping("/admin/specializations")
    public String allSpecializations(HttpSession session, Model model) {
        if (!requireAdmin(session)) return "redirect:/login?error=access";
        List<Specialization> specs = specializationDAO.findAll();
        model.addAttribute("specializations", specs);
        return "admin/manage-specializations";
    }

    @PostMapping("/admin/specializations/add")
    public String addSpecialization(
            HttpSession session,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            RedirectAttributes ra) {
        if (!requireAdmin(session)) return "redirect:/login?error=access";

        if (specializationDAO.findByName(name) != null) {
            ra.addFlashAttribute("error", "Specialization already exists");
            return "redirect:/admin/specializations";
        }

        Specialization s = new Specialization();
        s.setName(name);
        s.setDescription(description);
        specializationDAO.save(s);
        ra.addFlashAttribute("success", "Specialization added");
        return "redirect:/admin/specializations";
    }

    @PostMapping("/admin/specializations/delete/{id}")
    public String deleteSpecialization(@PathVariable int id, HttpSession session, RedirectAttributes ra) {
        if (!requireAdmin(session)) return "redirect:/login?error=access";
        Specialization s = specializationDAO.findById(id);
        if (s != null) specializationDAO.delete(s);
        ra.addFlashAttribute("success", "Specialization deleted");
        return "redirect:/admin/specializations";
    }
}

