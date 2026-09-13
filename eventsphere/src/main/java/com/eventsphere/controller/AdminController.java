package com.eventsphere.controller;

import com.eventsphere.repository.CertificateRepository;
import com.eventsphere.repository.EventRepository;
import com.eventsphere.repository.RegistrationRepository;
import com.eventsphere.repository.StudentRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;
import com.eventsphere.entity.Event;
import java.util.List;

@Controller
public class AdminController {

    private final StudentRepository studentRepository;
    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final CertificateRepository certificateRepository;

    public AdminController(
            StudentRepository studentRepository,
            EventRepository eventRepository,
            RegistrationRepository registrationRepository,
            CertificateRepository certificateRepository) {

        this.studentRepository = studentRepository;
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.certificateRepository = certificateRepository;
    }

    @GetMapping("/admin-login")
    public String adminLogin() {
        return "admin-login";
    }

    @PostMapping("/admin-login")
    public String loginAdmin(
            @RequestParam String username,
            @RequestParam String password,
            Model model) {

        if ("admin".equals(username) && "admin098".equals(password)) {
            return "redirect:/admin-dashboard";
        }

        model.addAttribute("loginError", true);
        return "admin-login";
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboard(Model model) {

        long students = studentRepository.count();
        long events = eventRepository.count();
        long registrations = registrationRepository.count();
        long certificates = certificateRepository.count();
        long registeredStudentsCount = registrationRepository.countDistinctStudentsWithRegistration();
        long pendingCertificates = registeredStudentsCount - certificates;

        if (pendingCertificates < 0) {
            pendingCertificates = 0;
        }

        model.addAttribute("pendingCertificates", pendingCertificates);

        long upcomingEventsCount = eventRepository.countByStatus("UPCOMING");
        long completedEventsCount = eventRepository.countByStatus("COMPLETED");

        model.addAttribute("students", students);
        model.addAttribute("events", events);
        model.addAttribute("registrations", registrations);
        model.addAttribute("registeredStudentsCount", registeredStudentsCount);
        model.addAttribute("certificates", certificates);

        model.addAttribute("upcomingEventsCount", upcomingEventsCount);
        model.addAttribute("completedEventsCount", completedEventsCount);

        // Registration Trend
        LinkedHashMap<String, Integer> monthlyRegistrations = new LinkedHashMap<>();

        for (Object[] row : registrationRepository.getMonthlyRegistrations()) {
            monthlyRegistrations.put((String) row[0], ((Number) row[1]).intValue());
        }

        model.addAttribute("monthlyRegistrations", monthlyRegistrations);

        // Certificate Trend
        LinkedHashMap<String, Integer> monthlyCertificates = new LinkedHashMap<>();

        for (Object[] row : certificateRepository.getMonthlyCertificates()) {
            monthlyCertificates.put((String) row[0], ((Number) row[1]).intValue());
        }

        model.addAttribute("monthlyCertificates", monthlyCertificates);

        return "admin-dashboard";
    }

    @GetMapping("/event-schedule")
    public String eventSchedule(Model model) {

        List<Event> schedule = eventRepository.findAllByOrderByDateAscTimeAsc();

        model.addAttribute("schedule", schedule);

        return "event-schedule";
    }
}