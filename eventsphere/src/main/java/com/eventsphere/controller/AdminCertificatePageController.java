// package com.eventsphere.controller;

// import com.eventsphere.entity.Event;
// import com.eventsphere.entity.Registration;
// import com.eventsphere.repository.CertificateRepository;
// import com.eventsphere.repository.EventRepository;
// import com.eventsphere.repository.RegistrationRepository;
// import com.eventsphere.repository.StudentRepository;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;

// import java.util.List;
// import java.util.stream.Collectors;

// @Controller
// public class AdminCertificatePageController {

//         @Autowired
//         private RegistrationRepository registrationRepository;

//         @Autowired
//         private CertificateRepository certificateRepository;

//         @Autowired
//         private EventRepository eventRepository;

//         @Autowired
//         private StudentRepository studentRepository;

//         @GetMapping("/admin/certificates")
//         public String certificates(Model model) {

//                 // Show all registrations in cards
//                 List<Registration> registrations = registrationRepository.findAll();

//                 // Count eligible certificates
//                 List<Registration> eligibleRegistrations = registrations.stream()
//                                 .filter(reg -> {
//                                         Event event = eventRepository.findById(reg.getEventId()).orElse(null);

//                                         return event != null
//                                                         && "COMPLETED".equals(event.getStatus())
//                                                         && "PAID".equals(reg.getPaymentStatus())
//                                                         && "PRESENT".equals(reg.getAttendance());
//                                 })
//                                 .collect(Collectors.toList());

//                 long totalCount = registrations.size();

//                 long paidCount = registrations.stream()
//                                 .filter(r -> "PAID".equals(r.getPaymentStatus()))
//                                 .count();

//                 long pendingCount = registrations.stream()
//                                 .filter(r -> "PENDING".equals(r.getPaymentStatus()))
//                                 .count();

//                 long issuedCount = eligibleRegistrations.stream()
//                                 .filter(r -> certificateRepository.existsByRegistrationId(r.getId()))
//                                 .count();

//                 long pendingIssueCount = eligibleRegistrations.stream()
//                                 .filter(r -> !certificateRepository.existsByRegistrationId(r.getId()))
//                                 .count();

//                 model.addAttribute("registrations", registrations);

//                 model.addAttribute("totalCount", totalCount);
//                 model.addAttribute("paidCount", paidCount);
//                 model.addAttribute("pendingCount", pendingCount);
//                 model.addAttribute("issuedCount", issuedCount);
//                 model.addAttribute("pendingIssueCount", pendingIssueCount);

//                 model.addAttribute("certificateRepository", certificateRepository);
//                 model.addAttribute("studentRepository", studentRepository);
//                 model.addAttribute("eventRepository", eventRepository);

//                 return "admin-certificates";
//         }
// }

package com.eventsphere.controller;

import com.eventsphere.entity.Event;
import com.eventsphere.entity.Registration;
import com.eventsphere.repository.CertificateRepository;
import com.eventsphere.repository.EventRepository;
import com.eventsphere.repository.RegistrationRepository;
import com.eventsphere.repository.StudentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AdminCertificatePageController {

        @Autowired
        private RegistrationRepository registrationRepository;

        @Autowired
        private CertificateRepository certificateRepository;

        @Autowired
        private EventRepository eventRepository;

        @Autowired
        private StudentRepository studentRepository;

        @GetMapping("/admin/certificates")
        public String certificates(Model model) {

                // All registrations
                List<Registration> registrations = registrationRepository.findAll();

                // Only registrations eligible for certificates
                List<Registration> eligibleRegistrations = registrations.stream()
                                .filter(reg -> {
                                        Event event = eventRepository.findById(reg.getEventId()).orElse(null);

                                        return event != null
                                                        && "COMPLETED".equals(event.getStatus())
                                                        && "PAID".equals(reg.getPaymentStatus())
                                                        && "PRESENT".equals(reg.getAttendance());
                                })
                                .collect(Collectors.toList());

                // Dashboard counts
                long totalCount = eligibleRegistrations.size();

                long paidCount = registrations.stream()
                                .filter(r -> "PAID".equals(r.getPaymentStatus()))
                                .count();

                long pendingCount = registrations.stream()
                                .filter(r -> "PENDING".equals(r.getPaymentStatus()))
                                .count();

                long issuedCount = eligibleRegistrations.stream()
                                .filter(r -> certificateRepository.existsByRegistrationId(r.getId()))
                                .count();

                long pendingIssueCount = eligibleRegistrations.stream()
                                .filter(r -> !certificateRepository.existsByRegistrationId(r.getId()))
                                .count();

                // Show ONLY eligible registrations
                model.addAttribute("registrations", eligibleRegistrations);

                model.addAttribute("totalCount", totalCount);
                model.addAttribute("paidCount", paidCount);
                model.addAttribute("pendingCount", pendingCount);
                model.addAttribute("issuedCount", issuedCount);
                model.addAttribute("pendingIssueCount", pendingIssueCount);

                model.addAttribute("certificateRepository", certificateRepository);
                model.addAttribute("studentRepository", studentRepository);
                model.addAttribute("eventRepository", eventRepository);

                return "admin-certificates";
        }
}