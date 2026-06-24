package com.eventsphere.controller;

import com.eventsphere.entity.Registration;
import com.eventsphere.entity.Student;
import com.eventsphere.repository.EventRepository;
import com.eventsphere.repository.RegistrationRepository;
import com.eventsphere.repository.StudentRepository;
import com.eventsphere.repository.CertificateRepository;
import com.eventsphere.entity.Certificate;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class RegistrationPageController {

        private final RegistrationRepository registrationRepository;
        private final StudentRepository studentRepository;
        private final EventRepository eventRepository;
        private final CertificateRepository certificateRepository;

        public RegistrationPageController(
                        RegistrationRepository registrationRepository,
                        StudentRepository studentRepository,
                        EventRepository eventRepository,
                        CertificateRepository certificateRepository) {

                this.registrationRepository = registrationRepository;
                this.studentRepository = studentRepository;
                this.eventRepository = eventRepository;
                this.certificateRepository = certificateRepository;
        }

        @GetMapping("/registrations-page")
        public String registrationsPage(Model model) {

                model.addAttribute("registrations",
                                registrationRepository.findAll());

                model.addAttribute("students",
                                studentRepository.findAll());

                model.addAttribute("events",
                                eventRepository.findAll());

                return "registrations";
        }

        @GetMapping("/add-registration")
        public String addRegistrationPage(Model model) {

                model.addAttribute("students",
                                studentRepository.findAll());

                model.addAttribute("events",
                                eventRepository.findAll());

                return "add-registration";
        }

        @PostMapping("/save-registration")
        public String saveRegistration(Registration registration) {

                registrationRepository.save(registration);

                return "redirect:/registrations-page";
        }

        @GetMapping("/register-event/{eventId}")
        public String registerEvent(
                        @PathVariable Long eventId,
                        HttpSession session) {

                System.out.println("REGISTER EVENT HIT: " + eventId);

                Student student = (Student) session.getAttribute("loggedInStudent");

                if (student == null) {
                        System.out.println("STUDENT IS NULL");
                        return "redirect:/login";
                }

                boolean alreadyRegistered = registrationRepository.existsByStudentIdAndEventId(
                                student.getId(),
                                eventId);

                if (alreadyRegistered) {
                        System.out.println("ALREADY REGISTERED");
                        return "redirect:/student-events";
                }

                Registration registration = new Registration();

                registration.setStudentId(student.getId());
                registration.setEventId(eventId);
                registration.setPaymentStatus("PENDING");

                registrationRepository.save(registration);

                System.out.println("REGISTRATION SAVED ID = "
                                + registration.getId());

                return "redirect:/payment/" + registration.getId();
        }

        @GetMapping("/payment/{registrationId}")
        public String paymentPage(
                        @PathVariable Long registrationId,
                        Model model) {

                System.out.println("PAYMENT PAGE HIT: " + registrationId);

                Registration registration = registrationRepository.findById(registrationId)
                                .orElse(null);

                if (registration == null) {

                        System.out.println("REGISTRATION NOT FOUND");

                        return "redirect:/student-events";
                }

                System.out.println("REGISTRATION FOUND");

                model.addAttribute("registration", registration);

                model.addAttribute(
                                "event",
                                eventRepository.findById(
                                                registration.getEventId())
                                                .orElse(null));

                return "payment";
        }

        @GetMapping("/confirm-payment/{registrationId}")
        public String confirmPayment(
                        @PathVariable Long registrationId) {

                Registration registration = registrationRepository.findById(registrationId)
                                .orElse(null);

                if (registration != null) {

                        registration.setPaymentStatus("PAID");

                        registrationRepository.save(registration);
                }

                return "redirect:/my-registrations";
        }

        @GetMapping("/delete-registration/{id}")
        public String deleteRegistration(
                        @PathVariable Long id) {

                registrationRepository.deleteById(id);

                return "redirect:/registrations-page";
        }

        @GetMapping("/edit-registration/{id}")
        public String editRegistration(
                        @PathVariable Long id,
                        Model model) {

                Registration registration = registrationRepository.findById(id)
                                .orElse(null);

                model.addAttribute("registration", registration);

                model.addAttribute("students",
                                studentRepository.findAll());

                model.addAttribute("events",
                                eventRepository.findAll());

                return "edit-registration";
        }

        @PostMapping("/update-registration")
        public String updateRegistration(
                        Registration registration) {

                registrationRepository.save(registration);

                return "redirect:/registrations-page";
        }

        @GetMapping("/my-registrations")
        public String myRegistrations(
                        HttpSession session,
                        Model model) {

                Student student = (Student) session.getAttribute("loggedInStudent");

                if (student == null) {
                        return "redirect:/login";
                }

                model.addAttribute(
                                "registrations",
                                registrationRepository.findByStudentId(
                                                student.getId()));

                return "my-registrations";
        }

        // @GetMapping("/abc")
        // public String abc() {

        //         System.out.println("ABC HIT");

        //         return "payment";
        // }

        @GetMapping("/generate-certificate/{registrationId}")
        public String generateCertificate(@PathVariable Long registrationId) {

                Registration reg = registrationRepository.findById(registrationId).orElse(null);

                if (reg == null || !"PAID".equals(reg.getPaymentStatus())) {
                        return "redirect:/admin/certificates";
                }

                boolean exists = certificateRepository.existsByRegistrationId(registrationId);
                if (exists) {
                        return "redirect:/admin/certificates";
                }

                Certificate cert = new Certificate();
                cert.setStudentId(reg.getStudentId());
                cert.setEventId(reg.getEventId());
                cert.setRegistrationId(registrationId);
                cert.setCertificateCode("CERT-" + System.currentTimeMillis());

                certificateRepository.save(cert);

                return "redirect:/admin/certificates";
        }
}