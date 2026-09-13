package com.eventsphere.controller;

import org.springframework.web.bind.annotation.RequestParam;

import com.eventsphere.entity.Event;
import com.eventsphere.entity.Student;
import com.eventsphere.repository.EventRepository;
import com.eventsphere.repository.RegistrationRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class EventPageController {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;

    public EventPageController(
            EventRepository eventRepository,
            RegistrationRepository registrationRepository) {

        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
    }

    // Student/Admin upcoming events
    @GetMapping("/events-page")
    public String eventsPage(Model model) {

        model.addAttribute(
                "events",
                eventRepository.findByStatus("UPCOMING"));

        return "events";
    }

    // Search events
    @GetMapping("/search-events")
    public String searchEvents(
            @RequestParam String keyword,
            Model model) {

        model.addAttribute(
                "events",
                eventRepository.findByNameContainingIgnoreCase(keyword));

        return "events";
    }

    // Event details
    @GetMapping("/event-details/{id}")
    public String eventDetails(
            @PathVariable Long id,
            Model model) {

        Event event = eventRepository
                .findById(id)
                .orElse(null);

        model.addAttribute("event", event);

        return "event-details";
    }

    // Add event page
    @GetMapping("/add-event")
    public String addEventPage() {

    return "add-event";
    }

    @PostMapping("/save-event")
    public String saveEvent(Event event, Model model) {

        boolean exists = eventRepository.existsByDateAndTimeAndVenue(
                event.getDate(),
                event.getTime(),
                event.getVenue());

        if (exists) {
            model.addAttribute("error",
                    "An event is already scheduled at this venue on the selected date and time.");
            return "add-event";
        }

        event.setStatus("UPCOMING");

        eventRepository.save(event);

        return "redirect:/events-page";
    }

    // Save event
    // @PostMapping("/save-event")
    // public String saveEvent(Event event) {

    //     event.setStatus("UPCOMING");

    //     eventRepository.save(event);

    //     return "redirect:/events-page";
    // }

    // Complete event
    @GetMapping("/complete-event/{id}")
    public String completeEvent(
            @PathVariable Long id) {

        Event event = eventRepository.findById(id)
                .orElse(null);

        if (event != null) {

            event.setStatus("COMPLETED");

            eventRepository.save(event);
        }

        return "redirect:/events-page";
    }

    // Edit event
    @GetMapping("/edit-event/{id}")
    public String editEvent(
            @PathVariable Long id,
            Model model) {

        Event event = eventRepository.findById(id)
                .orElse(null);

        model.addAttribute("event", event);

        return "edit-event";
    }

    // Update event
    @PostMapping("/update-event")
    public String updateEvent(Event event) {

        eventRepository.save(event);

        return "redirect:/events-page";
    }

    // Student events
    @GetMapping("/student-events")
    public String studentEvents(Model model) {

        model.addAttribute(
                "events",
                eventRepository.findByStatus("UPCOMING"));

        return "student-events";
    }

    // Completed events page
    @GetMapping("/completed-events")
    public String completedEvents(Model model) {

        model.addAttribute(
                "events",
                eventRepository.findByStatus("COMPLETED"));

        return "completed-events";
    }

    // Student completed events
    @GetMapping("/student-completed-events")
    public String studentCompletedEvents(
            HttpSession session,
            Model model) {

        Student student = (Student) session.getAttribute("loggedInStudent");

        if (student == null) {

            return "redirect:/login";
        }

        model.addAttribute(
                "events",
                eventRepository.findByStatus("COMPLETED"));

        model.addAttribute(
                "studentId",
                student.getId());

        model.addAttribute(
                "registrationRepository",
                registrationRepository);

        return "student-completed-events";
    }

}