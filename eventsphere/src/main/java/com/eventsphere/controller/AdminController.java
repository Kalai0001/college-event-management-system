// package com.eventsphere.controller;

// import com.eventsphere.repository.StudentRepository;
// import com.eventsphere.repository.EventRepository;
// import com.eventsphere.repository.RegistrationRepository;
// import com.eventsphere.repository.CertificateRepository;

// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;

// @Controller
// public class AdminController {

//     private final StudentRepository studentRepository;
//     private final EventRepository eventRepository;
//     private final RegistrationRepository registrationRepository;
//     private final CertificateRepository certificateRepository;

//     public AdminController(
//             StudentRepository studentRepository,
//             EventRepository eventRepository,
//             RegistrationRepository registrationRepository,
//             CertificateRepository certificateRepository) {

//         this.studentRepository = studentRepository;
//         this.eventRepository = eventRepository;
//         this.registrationRepository = registrationRepository;
//         this.certificateRepository = certificateRepository;
//     }

//     @GetMapping("/admin-login")
//     public String adminLogin() {
//         return "admin-login";
//     }

//     @GetMapping("/admin-dashboard")
//     public String adminDashboard(Model model) {

//         model.addAttribute(
//                 "students",
//                 studentRepository.count());

//         model.addAttribute(
//                 "events",
//                 eventRepository.count());

//         model.addAttribute(
//                 "registrations",
//                 registrationRepository.count());

//         model.addAttribute(
//                 "certificates",
//                 certificateRepository.count());

//         return "admin-dashboard";
//     }
// }



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

        if ("admin".equals(username)
                && "admin098".equals(password)) {

            return "redirect:/admin-dashboard";
        }

        model.addAttribute("loginError", true);

        return "admin-login";
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboard(Model model) {

        model.addAttribute(
                "students",
                studentRepository.count());

        model.addAttribute(
                "events",
                eventRepository.count());

        model.addAttribute(
                "registrations",
                registrationRepository.count());

        model.addAttribute(
                "certificates",
                certificateRepository.count());

        return "admin-dashboard";
    }
}