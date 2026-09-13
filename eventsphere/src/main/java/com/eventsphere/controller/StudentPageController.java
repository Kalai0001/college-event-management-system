package com.eventsphere.controller;

import com.eventsphere.entity.Student;
import com.eventsphere.entity.Event;
import com.eventsphere.entity.Registration;

import com.eventsphere.repository.StudentRepository;
import com.eventsphere.service.EmailService;
import com.eventsphere.repository.RegistrationRepository;
import com.eventsphere.repository.EventRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eventsphere.repository.CertificateRepository;
import com.eventsphere.entity.Certificate;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

@Controller
public class StudentPageController {

        private final StudentRepository studentRepository;
        private final RegistrationRepository registrationRepository;
        private final EventRepository eventRepository;
        private final CertificateRepository certificateRepository;
        private final EmailService emailService;

        public StudentPageController(
                        StudentRepository studentRepository,
                        EventRepository eventRepository,
                        RegistrationRepository registrationRepository,
                        CertificateRepository certificateRepository,
                        EmailService emailService) {

                this.studentRepository = studentRepository;
                this.eventRepository = eventRepository;
                this.registrationRepository = registrationRepository;
                this.certificateRepository = certificateRepository;
                this.emailService = emailService;
        }

        @GetMapping("/students-page")
        public String studentsPage(Model model) {

                model.addAttribute("students",
                                studentRepository.findAll());

                return "students";
        }

        @GetMapping("/signup")
        public String signupPage() {
                return "signup";
        }


        @PostMapping("/signup")
        public String signup(Student student, Model model, HttpSession session) {

                Student existingStudent = studentRepository.findByEmail(student.getEmail());

                if (existingStudent != null) {
                        model.addAttribute("signupError", "Email is already registered. Please login.");
                        return "signup";
                }

                // Generate 6-digit OTP
                int otp = 100000 + (int) (Math.random() * 900000);

                // Store student details and OTP in session
                session.setAttribute("pendingStudent", student);
                session.setAttribute("otp", String.valueOf(otp));
                session.setAttribute("otpTime", System.currentTimeMillis());

                try {

                        System.out.println("=====================================");
                        System.out.println("Sending OTP to: " + student.getEmail());
                        System.out.println("Generated OTP: " + otp);

                        emailService.sendEmail(
                                        student.getEmail(),
                                        "EventSphere Email Verification",
                                        "Your OTP for EventSphere registration is: " + otp);

                        System.out.println("OTP email sent successfully.");
                        System.out.println("=====================================");

                } catch (Exception e) {

                        e.printStackTrace();

                        model.addAttribute("signupError", "Failed to send OTP. Please try again.");
                        return "signup";
                }

                return "redirect:/verify-otp";
        }


        @GetMapping("/verify-otp")
        public String verifyOtpPage(Model model) {

                model.addAttribute("otpDuration", 60);

                return "verify-otp";
        }

        @PostMapping("/verify-otp")
        public String verifyOtp(
                        @RequestParam String otp,
                        HttpSession session,
                        Model model) {

                String sessionOtp = (String) session.getAttribute("otp");
                Student student = (Student) session.getAttribute("pendingStudent");
                Long otpTime = (Long) session.getAttribute("otpTime");

                if (sessionOtp == null || student == null || otpTime == null) {
                        return "redirect:/signup";
                }

                long currentTime = System.currentTimeMillis();

                // OTP valid for 60 seconds
                if (currentTime - otpTime > 60000) {

                        session.removeAttribute("otp");
                        session.removeAttribute("otpTime");
                        session.removeAttribute("pendingStudent");

                        model.addAttribute("error", "OTP has expired. Please register again.");

                        return "verify-otp";
                }
                if (!sessionOtp.equals(otp)) {

                        model.addAttribute("error", "Invalid OTP");

                        return "verify-otp";
                }

                student.setEmailVerified(true);

                studentRepository.save(student);

                session.removeAttribute("otp");
                session.removeAttribute("otpTime");
                session.removeAttribute("pendingStudent");

                return "redirect:/login";
        }

        @PostMapping("/resend-otp")
        public String resendOtp(HttpSession session, Model model) {

                Student student = (Student) session.getAttribute("pendingStudent");

                if (student == null) {
                        return "redirect:/signup";
                }

                // Generate new OTP
                int otp = 100000 + (int) (Math.random() * 900000);

                // Save new OTP and new time
                session.setAttribute("otp", String.valueOf(otp));
                session.setAttribute("otpTime", System.currentTimeMillis());

                try {

                        emailService.sendEmail(
                                        student.getEmail(),
                                        "EventSphere Email Verification",
                                        "Your new OTP is: " + otp);

                        model.addAttribute("success", "A new OTP has been sent to your email.");

                } catch (Exception e) {

                        model.addAttribute("error", "Failed to resend OTP.");

                }

                return "verify-otp";
        }

        @GetMapping("/login")
        public String loginPage() {
                return "login";
        }

        @PostMapping("/login")
        public String login(
                        String email,
                        String password,
                        Model model,
                        HttpSession session) {

                Student student = studentRepository.findByEmailAndPassword(
                                email,
                                password);

                if (student == null) {
                        model.addAttribute(
                                        "error",
                                        "Invalid Email or Password");

                        return "login";
                }

                session.setAttribute(
                                "loggedInStudent",
                                student);

                return "redirect:/dashboard";
        }

        // @GetMapping("/verify-otp")
        // public String verifyOtpPage() {
        // return "verify-otp";
        // }

        @GetMapping("/dashboard")
        public String dashboard(
                        Model model,
                        HttpSession session) {

                Student student = (Student) session.getAttribute(
                                "loggedInStudent");

                if (student == null) {
                        return "redirect:/login";
                }

                model.addAttribute("student", student);

                List<Registration> registrations = registrationRepository.findByStudentId(
                                student.getId());

                List<Event> myRegistrationList = new ArrayList<>();

                for (Registration reg : registrations) {

                        Event event = eventRepository.findById(
                                        reg.getEventId())
                                        .orElse(null);

                        if (event != null) {
                                myRegistrationList.add(event);
                        }
                }

                model.addAttribute(
                                "myRegistrationList",
                                myRegistrationList);

                model.addAttribute(
                                "myRegistrations",
                                registrations.size());

                model.addAttribute(
                                "totalEvents",
                                eventRepository.count());
                int certificateCount = certificateRepository.findByStudentId(
                                student.getId()).size();

                model.addAttribute(
                                "certificates",
                                certificateCount);

                return "dashboard";
        }

        @GetMapping("/profile")
        public String profile(
                        Model model,
                        HttpSession session) {

                Student student = (Student) session.getAttribute(
                                "loggedInStudent");

                if (student == null) {
                        return "redirect:/login";
                }

                model.addAttribute("student", student);

                int registeredEvents = registrationRepository.findByStudentId(
                                student.getId()).size();

                model.addAttribute(
                                "registeredEvents",
                                registeredEvents);
                int certificateCount = certificateRepository.findByStudentId(
                                student.getId()).size();

                model.addAttribute(
                                "certificates",
                                certificateCount);

                model.addAttribute(
                                "upcomingEvents",
                                eventRepository.count());

                return "profile";
        }

        @GetMapping("/add-student")
        public String addStudentPage() {
                return "add-student";
        }

        @PostMapping("/save-student")
        public String saveStudent(Student student) {

                studentRepository.save(student);

                return "redirect:/students-page";
        }

        @GetMapping("/delete-student/{id}")
        public String deleteStudent(
                        @PathVariable Long id) {

                studentRepository.deleteById(id);

                return "redirect:/students-page";
        }

        @GetMapping("/edit-student/{id}")
        public String editStudent(
                        @PathVariable Long id,
                        Model model) {

                Student student = studentRepository.findById(id)
                                .orElse(null);

                model.addAttribute(
                                "student",
                                student);

                return "edit-student";
        }

        @PostMapping("/update-student")
        public String updateStudent(
                        Student student) {

                studentRepository.save(student);

                return "redirect:/students-page";
        }

        @GetMapping("/certificates")
        public String certificates(
                        HttpSession session,
                        Model model) {

                Student student = (Student) session.getAttribute("loggedInStudent");

                if (student == null) {
                        return "redirect:/login";
                }
                System.out.println("STUDENT ID = " + student.getId());
                model.addAttribute(
                                "certificates",
                                certificateRepository.findByStudentId(
                                                student.getId()));

                return "certificates";
        }

        @GetMapping("/certificate-details/{id}")
        public String certificateDetails(
                        @PathVariable Long id,
                        HttpSession session,
                        Model model) {

                Student student = (Student) session.getAttribute("loggedInStudent");

                if (student == null) {
                        return "redirect:/login";
                }

                Certificate certificate = certificateRepository.findById(id)
                                .orElse(null);

                if (certificate == null) {
                        return "redirect:/certificates";
                }

                Event event = eventRepository.findById(
                                certificate.getEventId())
                                .orElse(null);

                model.addAttribute("student", student);
                model.addAttribute("certificate", certificate);
                model.addAttribute("event", event);

                return "certificate-details";
        }

        @GetMapping("/search-students")
        public String searchStudents(@RequestParam("keyword") String keyword,
                        Model model) {

                List<Student> students = studentRepository
                                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrDepartmentContainingIgnoreCase(
                                                keyword, keyword, keyword);

                model.addAttribute("students", students);

                return "students";
        }

}