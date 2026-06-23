package com.eventsphere.controller;

import org.springframework.web.bind.annotation.RequestParam;
import com.eventsphere.entity.Event;
import com.eventsphere.entity.Student;
import com.eventsphere.repository.EventRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class EventPageController {

    private final EventRepository eventRepository;

    public EventPageController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GetMapping("/events-page")
    public String eventsPage(Model model) {
        model.addAttribute("events", eventRepository.findAll());
        return "events";
    }

    @GetMapping("/search-events")
    public String searchEvents(
            @RequestParam String keyword,
            Model model) {

        model.addAttribute(
                "events",
                eventRepository.findByNameContainingIgnoreCase(keyword));

        return "events";
    }

    @GetMapping("/event-details/{id}")
    public String eventDetails(
            @PathVariable Long id,
            Model model) {

        Event event = eventRepository.findById(id).orElse(null);

        model.addAttribute("event", event);

        return "event-details";
    }

    @GetMapping("/add-event")
    public String addEventPage() {
        return "add-event";
    }

    @PostMapping("/save-event")
    public String saveEvent(Event event) {

        eventRepository.save(event);

        return "redirect:/events-page";
    }

    @GetMapping("/delete-event/{id}")
    public String deleteEvent(@PathVariable Long id) {

        eventRepository.deleteById(id);

        return "redirect:/events-page";
    }

    @GetMapping("/edit-event/{id}")
    public String editEvent(@PathVariable Long id, Model model) {

        Event event = eventRepository.findById(id).orElse(null);

        model.addAttribute("event", event);

        return "edit-event";
    }

    @PostMapping("/update-event")
    public String updateEvent(Event event) {

        eventRepository.save(event);

        return "redirect:/events-page";
    }

    @GetMapping("/student-events")
    public String studentEvents(Model model) {

    model.addAttribute("events",
    eventRepository.findAll());

    return "student-events";
    }
    
}