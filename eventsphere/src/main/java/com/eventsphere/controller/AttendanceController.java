
package com.eventsphere.controller;

import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.eventsphere.entity.Event;
import com.eventsphere.entity.Registration;
import com.eventsphere.entity.Student;
import com.eventsphere.repository.EventRepository;
import com.eventsphere.repository.RegistrationRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AttendanceController {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;

    public AttendanceController(
            EventRepository eventRepository,
            RegistrationRepository registrationRepository) {

        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
    }

    @GetMapping("/attendance/{eventId}")
    public String markAttendance(
            @PathVariable Long eventId,
            HttpSession session,
            Model model) {

        Student student = (Student) session.getAttribute("loggedInStudent");

        if (student == null) {
            return "redirect:/login";
        }

        Event event = eventRepository.findById(eventId).orElse(null);

        if (event == null) {
            model.addAttribute("message", "Event not found.");
            return "attendance-result";
        }

        if (!event.isAttendanceOpen()) {
            model.addAttribute("message", "Attendance is closed.");
            return "attendance-result";
        }
        // Check if attendance window has expired (5 minutes)
        if (event.getAttendanceOpenedAt() != null &&
                LocalDateTime.now().isAfter(event.getAttendanceOpenedAt().plusMinutes(15))) {

            event.setAttendanceOpen(false);
            event.setAttendanceOpenedAt(null);
            eventRepository.save(event);

            model.addAttribute("message", "Attendance window has expired.");
            return "attendance-result";
        }

        Registration registration = registrationRepository
                .findByStudentIdAndEventId(
                        student.getId(),
                        eventId);

        if (registration == null) {
            model.addAttribute("message",
                    "You are not registered for this event.");
            return "attendance-result";
        }

        if ("PRESENT".equals(registration.getAttendance())) {
            model.addAttribute("message",
                    "Attendance already marked.");
            return "attendance-result";
        }

        registration.setAttendance("PRESENT");
        registration.setAttendanceTime(LocalDateTime.now());

        registrationRepository.save(registration);

        model.addAttribute("message",
                "Attendance marked successfully.");

        return "attendance-result";
    }
}