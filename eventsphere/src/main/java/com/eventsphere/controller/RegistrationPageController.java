// package com.eventsphere.controller;

// import com.eventsphere.entity.Registration;
// import com.eventsphere.entity.Student;
// import com.eventsphere.repository.EventRepository;
// import com.eventsphere.repository.RegistrationRepository;
// import com.eventsphere.repository.StudentRepository;
// import com.eventsphere.repository.CertificateRepository;
// import com.eventsphere.entity.Certificate;
// import com.eventsphere.entity.Event;

// import jakarta.servlet.http.HttpSession;

// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PathVariable;

// import java.time.LocalDateTime;

// @Controller
// public class RegistrationPageController {

//         private final RegistrationRepository registrationRepository;
//         private final StudentRepository studentRepository;
//         private final EventRepository eventRepository;
//         private final CertificateRepository certificateRepository;

//         public RegistrationPageController(
//                         RegistrationRepository registrationRepository,
//                         StudentRepository studentRepository,
//                         EventRepository eventRepository,
//                         CertificateRepository certificateRepository) {

//                 this.registrationRepository = registrationRepository;
//                 this.studentRepository = studentRepository;
//                 this.eventRepository = eventRepository;
//                 this.certificateRepository = certificateRepository;
//         }

//         @GetMapping("/registrations-page")
//         public String registrationsPage(Model model) {

//                 model.addAttribute("registrations",
//                                 registrationRepository.findAll());

//                 model.addAttribute("students",
//                                 studentRepository.findAll());

//                 model.addAttribute("events",
//                                 eventRepository.findAll());

//                 return "registrations";
//         }

//         @GetMapping("/add-registration")
//         public String addRegistrationPage(Model model) {

//                 model.addAttribute("students",
//                                 studentRepository.findAll());

//                 model.addAttribute("events",
//                                 eventRepository.findAll());

//                 return "add-registration";
//         }

//         @PostMapping("/save-registration")
//         public String saveRegistration(Registration registration) {

//                 registration.setRegistrationDate(LocalDateTime.now());

//                 registrationRepository.save(registration);

//                 return "redirect:/registrations-page";
//         }

//         @GetMapping("/register-event/{eventId}")
//         public String registerEvent(
//                         @PathVariable Long eventId,
//                         HttpSession session) {

//                 System.out.println("REGISTER EVENT HIT: " + eventId);

//                 Student student = (Student) session.getAttribute("loggedInStudent");

//                 // Student not logged in
//                 if (student == null) {
//                         System.out.println("STUDENT IS NULL");
//                         return "redirect:/login";
//                 }

//                 // Check if event exists
//                 Event event = eventRepository.findById(eventId).orElse(null);

//                 if (event == null) {
//                         return "redirect:/student-events";
//                 }

//                 // Don't allow registration if event is completed
//                 if ("COMPLETED".equalsIgnoreCase(event.getStatus())) {
//                         return "redirect:/student-completed-events";
//                 }

//                 // Check if the student already has a registration
//                 Registration existingRegistration = registrationRepository.findByStudentIdAndEventId(
//                                 student.getId(),
//                                 eventId);

//                 if (existingRegistration != null) {

//                         // Payment not completed yet
//                         if ("PENDING".equalsIgnoreCase(existingRegistration.getPaymentStatus())) {

//                                 System.out.println("PENDING PAYMENT FOUND");

//                                 return "redirect:/payment/" + existingRegistration.getId();
//                         }

//                         // Already paid
//                         if ("PAID".equalsIgnoreCase(existingRegistration.getPaymentStatus())) {

//                                 System.out.println("ALREADY REGISTERED");

//                                 return "redirect:/my-registrations";
//                         }
//                 }

//                 // Create a new registration
//                 Registration registration = new Registration();

//                 registration.setStudentId(student.getId());
//                 registration.setEventId(eventId);
//                 registration.setPaymentStatus("PENDING");
//                 registration.setRegistrationDate(LocalDateTime.now());

//                 registrationRepository.save(registration);

//                 System.out.println("REGISTRATION SAVED ID = " + registration.getId());

//                 return "redirect:/payment/" + registration.getId();
//         }

//         // Rest of your code remains the same...
//         @GetMapping("/payment/{registrationId}")
//         public String paymentPage(
//                         @PathVariable Long registrationId,
//                         Model model) {

//                 System.out.println("PAYMENT PAGE HIT: " + registrationId);

//                 Registration registration = registrationRepository.findById(registrationId)
//                                 .orElse(null);

//                 if (registration == null) {

//                         System.out.println("REGISTRATION NOT FOUND");

//                         return "redirect:/student-events";
//                 }

//                 System.out.println("REGISTRATION FOUND");

//                 model.addAttribute("registration", registration);

//                 model.addAttribute(
//                                 "event",
//                                 eventRepository.findById(
//                                                 registration.getEventId())
//                                                 .orElse(null));

//                 return "payment";
//         }

//         @GetMapping("/confirm-payment/{registrationId}")
//         public String confirmPayment(
//                         @PathVariable Long registrationId) {

//                 Registration registration = registrationRepository.findById(registrationId)
//                                 .orElse(null);

//                 if (registration != null) {

//                         registration.setPaymentStatus("PAID");

//                         registrationRepository.save(registration);
//                 }

//                 return "redirect:/my-registrations";
//         }

//         @GetMapping("/delete-registration/{id}")
//         public String deleteRegistration(
//                         @PathVariable Long id) {

//                 registrationRepository.deleteById(id);

//                 return "redirect:/registrations-page";
//         }

//         @GetMapping("/edit-registration/{id}")
//         public String editRegistration(
//                         @PathVariable Long id,
//                         Model model) {

//                 Registration registration = registrationRepository.findById(id)
//                                 .orElse(null);

//                 model.addAttribute("registration", registration);

//                 model.addAttribute("students",
//                                 studentRepository.findAll());

//                 model.addAttribute("events",
//                                 eventRepository.findAll());

//                 return "edit-registration";
//         }

//         @PostMapping("/update-registration")
//         public String updateRegistration(
//                         Registration registration) {

//                 registrationRepository.save(registration);

//                 return "redirect:/registrations-page";
//         }

//         @GetMapping("/my-registrations")
//         public String myRegistrations(
//                         HttpSession session,
//                         Model model) {

//                 Student student = (Student) session.getAttribute("loggedInStudent");

//                 if (student == null) {
//                         return "redirect:/login";
//                 }

//                 model.addAttribute(
//                                 "registrations",
//                                 registrationRepository.findByStudentId(
//                                                 student.getId()));

//                 return "my-registrations";
//         }

//         @GetMapping("/generate-certificate/{registrationId}")
//         public String generateCertificate(@PathVariable Long registrationId) {

//                 Registration reg = registrationRepository.findById(registrationId)
//                                 .orElse(null);

//                 // Registration not found
//                 if (reg == null) {
//                         return "redirect:/admin/certificates";
//                 }

//                 // Event must be completed
//                 Event event = eventRepository.findById(reg.getEventId()).orElse(null);

//                 if (event == null || !"COMPLETED".equals(event.getStatus())) {
//                         return "redirect:/admin/certificates";
//                 }

//                 // Student must have PAID
//                 if (!"PAID".equals(reg.getPaymentStatus())) {
//                         return "redirect:/admin/certificates";
//                 }

//                 // Student must be PRESENT
//                 if (!"PRESENT".equals(reg.getAttendance())) {
//                         return "redirect:/admin/certificates";
//                 }

//                 // Certificate already generated
//                 if (certificateRepository.existsByRegistrationId(registrationId)) {
//                         return "redirect:/admin/certificates";
//                 }

//                 Certificate cert = new Certificate();

//                 cert.setStudentId(reg.getStudentId());
//                 cert.setEventId(reg.getEventId());
//                 cert.setRegistrationId(registrationId);
//                 cert.setCertificateCode("CERT-" + System.currentTimeMillis());
//                 cert.setCertificateDate(LocalDateTime.now());

//                 certificateRepository.save(cert);

//                 return "redirect:/admin/certificates";
//         }

//         @GetMapping("/generate-all-certificate")
//         public String generateAllCertificates() {

//                 for (Registration reg : registrationRepository.findAll()) {

//                         // Event must be completed
//                         Event event = eventRepository.findById(reg.getEventId()).orElse(null);

//                         if (event == null || !"COMPLETED".equals(event.getStatus())) {
//                                 continue;
//                         }

//                         // Student must have PAID
//                         if (!"PAID".equals(reg.getPaymentStatus())) {
//                                 continue;
//                         }

//                         // Student must be PRESENT
//                         if (!"PRESENT".equals(reg.getAttendance())) {
//                                 continue;
//                         }

//                         // Certificate already generated
//                         if (certificateRepository.existsByRegistrationId(reg.getId())) {
//                                 continue;
//                         }

//                         Certificate cert = new Certificate();

//                         cert.setStudentId(reg.getStudentId());
//                         cert.setEventId(reg.getEventId());
//                         cert.setRegistrationId(reg.getId());
//                         cert.setCertificateCode("CERT-" + System.currentTimeMillis() + "-" + reg.getId());
//                         cert.setCertificateDate(LocalDateTime.now());

//                         certificateRepository.save(cert);
//                 }

//                 return "redirect:/admin/certificates";
//         }
// }

package com.eventsphere.controller;

import com.eventsphere.entity.Registration;
import com.eventsphere.entity.Student;
import com.eventsphere.repository.EventRepository;
import com.eventsphere.repository.RegistrationRepository;
import com.eventsphere.repository.StudentRepository;
import com.eventsphere.repository.CertificateRepository;
import com.eventsphere.entity.Certificate;
import com.eventsphere.entity.Event;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

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

                registration.setRegistrationDate(LocalDateTime.now());

                registrationRepository.save(registration);

                return "redirect:/registrations-page";
        }

        @GetMapping("/register-event/{eventId}")
        public String registerEvent(
                        @PathVariable Long eventId,
                        HttpSession session) {

                Student student = (Student) session.getAttribute("loggedInStudent");

                // Student not logged in
                if (student == null) {
                        return "redirect:/login";
                }

                // Check if event exists
                Event event = eventRepository.findById(eventId).orElse(null);

                if (event == null) {
                        return "redirect:/student-events";
                }

                // Don't allow registration if event is completed
                if ("COMPLETED".equalsIgnoreCase(event.getStatus())) {
                        return "redirect:/student-completed-events";
                }

                // Check if the student already has a registration
                Registration existingRegistration = registrationRepository.findByStudentIdAndEventId(
                                student.getId(),
                                eventId);

                if (existingRegistration != null) {

                        // Payment not completed yet
                        if ("PENDING".equalsIgnoreCase(existingRegistration.getPaymentStatus())) {
                                return "redirect:/payment/" + existingRegistration.getId();
                        }

                        // Already paid
                        if ("PAID".equalsIgnoreCase(existingRegistration.getPaymentStatus())) {
                                return "redirect:/my-registrations";
                        }
                }

                // Create a new registration
                Registration registration = new Registration();

                registration.setStudentId(student.getId());
                registration.setEventId(eventId);
                registration.setPaymentStatus("PENDING");
                registration.setRegistrationDate(LocalDateTime.now());

                registrationRepository.save(registration);

                return "redirect:/payment/" + registration.getId();
        }

        @GetMapping("/payment/{registrationId}")
        public String paymentPage(
                        @PathVariable Long registrationId,
                        Model model) {

                Registration registration = registrationRepository.findById(registrationId)
                                .orElse(null);

                if (registration == null) {
                        return "redirect:/student-events";
                }

                model.addAttribute("registration", registration);

                model.addAttribute(
                                "event",
                                eventRepository.findById(
                                                registration.getEventId())
                                                .orElse(null));

                return "payment";
        }

        /**
         * Marks the registration as PAID (mock payment - no real gateway involved)
         * and forwards the chosen payment method + masked details to the
         * success/receipt page via redirect parameters.
         */
        @GetMapping("/confirm-payment/{registrationId}")
        public String confirmPayment(
                        @PathVariable Long registrationId,
                        @RequestParam(required = false) String paymentMethod,
                        @RequestParam(required = false) String cardNumber,
                        @RequestParam(required = false) String upiId,
                        @RequestParam(required = false) String bankName) {

                Registration registration = registrationRepository.findById(registrationId)
                                .orElse(null);

                if (registration != null) {
                        registration.setPaymentStatus("PAID");
                        registrationRepository.save(registration);
                }

                String method = (paymentMethod != null && !paymentMethod.isBlank())
                                ? paymentMethod.toUpperCase()
                                : "CARD";

                StringBuilder redirectUrl = new StringBuilder("redirect:/payment-success/" + registrationId);
                redirectUrl.append("?method=").append(method);

                if ("CARD".equals(method) && cardNumber != null) {
                        String digits = cardNumber.replaceAll("\\D", "");
                        if (digits.length() >= 4) {
                                redirectUrl.append("&last4=")
                                                .append(digits.substring(digits.length() - 4));
                        }
                } else if ("UPI".equals(method) && upiId != null && !upiId.isBlank()) {
                        redirectUrl.append("&upi=").append(upiId);
                } else if ("NETBANKING".equals(method) && bankName != null && !bankName.isBlank()) {
                        redirectUrl.append("&bank=").append(bankName);
                }

                return redirectUrl.toString();
        }

        /**
         * Premium mock receipt page. Generates deterministic, realistic-looking
         * payment/transaction identifiers purely for display - no real payment
         * gateway is involved anywhere in this flow.
         */
        @GetMapping("/payment-success/{registrationId}")
        public String paymentSuccess(
                        @PathVariable Long registrationId,
                        @RequestParam(required = false, defaultValue = "CARD") String method,
                        @RequestParam(required = false) String last4,
                        @RequestParam(required = false) String upi,
                        @RequestParam(required = false) String bank,
                        Model model) {

                Registration registration = registrationRepository.findById(registrationId)
                                .orElse(null);

                if (registration == null) {
                        return "redirect:/student-events";
                }

                Event event = eventRepository.findById(registration.getEventId()).orElse(null);

                long seed = registrationId * 104729L;
                String paymentId = "PAY" + String.format("%010d", (seed % 9000000000L) + 1000000000L);
                String transactionId = "TXN"
                                + String.format("%012d",
                                                ((registrationId * 987654323L) % 900000000000L) + 100000000000L);
                String referenceNumber = "EVS-" + LocalDateTime.now().getYear() + "-"
                                + String.format("%06d", registrationId);

                String methodLabel;
                String methodDetail;

                switch (method == null ? "CARD" : method.toUpperCase()) {
                        case "UPI":
                                methodLabel = "UPI";
                                methodDetail = (upi != null) ? maskUpi(upi) : "UPI";
                                break;
                        case "NETBANKING":
                                methodLabel = "Net Banking";
                                methodDetail = (bank != null && !bank.isBlank()) ? bank : "Net Banking";
                                break;
                        default:
                                methodLabel = "Credit / Debit Card";
                                methodDetail = (last4 != null) ? "•••• •••• •••• " + last4 : "Card";
                }

                model.addAttribute("registration", registration);
                model.addAttribute("event", event);
                model.addAttribute("paymentId", paymentId);
                model.addAttribute("transactionId", transactionId);
                model.addAttribute("referenceNumber", referenceNumber);
                model.addAttribute("methodLabel", methodLabel);
                model.addAttribute("methodDetail", methodDetail);
                model.addAttribute("paymentDateTime", LocalDateTime.now());

                return "payment-success";
        }

        private String maskUpi(String upi) {
                int at = upi.indexOf('@');
                if (at <= 1 || at == upi.length() - 1) {
                        return upi;
                }
                String user = upi.substring(0, at);
                String domain = upi.substring(at);
                String masked = user.charAt(0) + "***" + user.charAt(user.length() - 1);
                return masked + domain;
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

        @GetMapping("/generate-certificate/{registrationId}")
        public String generateCertificate(@PathVariable Long registrationId) {

                Registration reg = registrationRepository.findById(registrationId)
                                .orElse(null);

                // Registration not found
                if (reg == null) {
                        return "redirect:/admin/certificates";
                }

                // Event must be completed
                Event event = eventRepository.findById(reg.getEventId()).orElse(null);

                if (event == null || !"COMPLETED".equals(event.getStatus())) {
                        return "redirect:/admin/certificates";
                }

                // Student must have PAID
                if (!"PAID".equals(reg.getPaymentStatus())) {
                        return "redirect:/admin/certificates";
                }

                // Student must be PRESENT
                if (!"PRESENT".equals(reg.getAttendance())) {
                        return "redirect:/admin/certificates";
                }

                // Certificate already generated
                if (certificateRepository.existsByRegistrationId(registrationId)) {
                        return "redirect:/admin/certificates";
                }

                Certificate cert = new Certificate();

                cert.setStudentId(reg.getStudentId());
                cert.setEventId(reg.getEventId());
                cert.setRegistrationId(registrationId);
                cert.setCertificateCode("CERT-" + System.currentTimeMillis());
                cert.setCertificateDate(LocalDateTime.now());

                certificateRepository.save(cert);

                return "redirect:/admin/certificates";
        }

        @GetMapping("/generate-all-certificate")
        public String generateAllCertificates() {

                for (Registration reg : registrationRepository.findAll()) {

                        // Event must be completed
                        Event event = eventRepository.findById(reg.getEventId()).orElse(null);

                        if (event == null || !"COMPLETED".equals(event.getStatus())) {
                                continue;
                        }

                        // Student must have PAID
                        if (!"PAID".equals(reg.getPaymentStatus())) {
                                continue;
                        }

                        // Student must be PRESENT
                        if (!"PRESENT".equals(reg.getAttendance())) {
                                continue;
                        }

                        // Certificate already generated
                        if (certificateRepository.existsByRegistrationId(reg.getId())) {
                                continue;
                        }

                        Certificate cert = new Certificate();

                        cert.setStudentId(reg.getStudentId());
                        cert.setEventId(reg.getEventId());
                        cert.setRegistrationId(reg.getId());
                        cert.setCertificateCode("CERT-" + System.currentTimeMillis() + "-" + reg.getId());
                        cert.setCertificateDate(LocalDateTime.now());

                        certificateRepository.save(cert);
                }

                return "redirect:/admin/certificates";
        }
}