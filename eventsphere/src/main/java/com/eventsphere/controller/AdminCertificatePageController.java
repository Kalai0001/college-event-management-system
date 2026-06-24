// package com.eventsphere.controller;

// import com.eventsphere.repository.RegistrationRepository;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;

// @Controller
// public class AdminCertificatePageController {

//     @Autowired
//     private RegistrationRepository registrationRepository;

//     @GetMapping("/admin/certificates")
//     public String viewCertificates(Model model) {

//         System.out.println("TOTAL = "
//                 + registrationRepository.findAll().size());

//         System.out.println("PAID = "
//                 + registrationRepository.findByPaymentStatus("PAID").size());

//         model.addAttribute(
//                 "registrations",
//                 registrationRepository.findByPaymentStatus("PAID"));

//         return "admin-certificates";
//     }
// }



package com.eventsphere.controller;

import com.eventsphere.repository.RegistrationRepository;
import com.eventsphere.repository.CertificateRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminCertificatePageController {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    @GetMapping("/admin/certificates")
    public String viewCertificates(Model model) {

        model.addAttribute(
                "registrations",
                registrationRepository.findByPaymentStatus("PAID"));

        model.addAttribute(
                "certificateRepository",
                certificateRepository);

        return "admin-certificates";
    }
}