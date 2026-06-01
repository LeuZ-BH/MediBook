package com.dabs.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dabs.model.Doctor;
import com.dabs.model.User;
import com.dabs.model.enums.Role;
import com.dabs.service.DoctorService;
import com.dabs.service.UserService;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginGet(HttpSession session) {
        if (session.getAttribute("userId") != null) {
            String role = (String) session.getAttribute("role");
            if (role == null) return "redirect:/login";
            if (role.equals("PATIENT")) return "redirect:/patient/dashboard";
            if (role.equals("DOCTOR")) return "redirect:/doctor/dashboard";
            if (role.equals("ADMIN")) return "redirect:/admin/dashboard";
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String loginPost(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes ra) {

        User user = userService.authenticate(email, password);
        if (user == null) {
            ra.addFlashAttribute("error", "Invalid email or password");
            return "redirect:/login";
        }

        session.setAttribute("userId", user.getUserId());
        session.setAttribute("userName", user.getName());
        session.setAttribute("role", user.getRole().name());

        if (user.getRole() == Role.PATIENT) return "redirect:/patient/dashboard";
        if (user.getRole() == Role.DOCTOR) {
            Doctor doc = doctorService.findByUserId(user.getUserId());
            if (doc != null) session.setAttribute("doctorId", doc.getDoctorId());
            return "redirect:/doctor/dashboard";
        }
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/register")
    public String registerGet(HttpSession session) {
        if (session.getAttribute("userId") != null) return "redirect:/patient/dashboard";
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerPost(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) String phone,
            HttpSession session,
            RedirectAttributes ra) {

        if (userService.findByEmail(email) != null) {
            ra.addFlashAttribute("error", "Email already registered");
            return "redirect:/register";
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(Role.PATIENT);
        // password hashing inside service is not done for register here; we store hash directly
        user.setPasswordHash(com.dabs.util.PasswordUtil.hash(password));

        userService.registerPatient(user);

        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logoutGet(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}

