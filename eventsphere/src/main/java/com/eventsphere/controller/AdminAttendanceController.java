package com.eventsphere.controller;

import com.eventsphere.entity.Event;
import com.eventsphere.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import jakarta.servlet.http.HttpServletResponse;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.List;
import java.time.LocalDateTime;
import java.time.Duration;

@Controller
public class AdminAttendanceController {

    @Autowired
    private EventRepository eventRepository;

    @GetMapping("/admin/attendance")
    public String attendancePage(Model model) {

        List<Event> completedEvents = eventRepository.findByStatus("COMPLETED");

        model.addAttribute("events", completedEvents);

        return "admin-attendance";
    }

    // Open Attendance
    @GetMapping("/admin/open-attendance/{id}")
    public String openAttendance(@PathVariable Long id) {

        Event event = eventRepository.findById(id).orElse(null);

        if (event != null) {
            event.setAttendanceOpen(true);
            event.setAttendanceOpenedAt(LocalDateTime.now());
            eventRepository.save(event);
        }

        return "redirect:/admin/attendance";
    }

    // Close Attendance
    @GetMapping("/admin/close-attendance/{id}")
    public String closeAttendance(@PathVariable Long id) {

        Event event = eventRepository.findById(id).orElse(null);

        if (event != null) {
            event.setAttendanceOpen(false);
            event.setAttendanceOpenedAt(null);
            eventRepository.save(event);
        }

        return "redirect:/admin/attendance";
    }

    @GetMapping("/admin/generate-qr/{id}")
    public void generateQr(
            @PathVariable Long id,
            HttpServletResponse response) throws Exception {

        Event event = eventRepository.findById(id).orElse(null);

        if (event == null || !event.isAttendanceOpen()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Attendance is closed.");
            return;
        }

        String qrText = "http://192.168.43.73:8080/attendance/" + id;

        BitMatrix matrix = new MultiFormatWriter().encode(
                qrText,
                BarcodeFormat.QR_CODE,
                300,
                300);

        BufferedImage image = new BufferedImage(
                300,
                300,
                BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < 300; x++) {
            for (int y = 0; y < 300; y++) {
                image.setRGB(
                        x,
                        y,
                        matrix.get(x, y)
                                ? 0xFF000000
                                : 0xFFFFFFFF);
            }
        }

        response.setContentType("image/png");

        ImageIO.write(image, "PNG", response.getOutputStream());
    }

    @GetMapping("/admin/attendance-qr/{id}")
    public String showAttendanceQr(
            @PathVariable Long id,
            Model model) {

        Event event = eventRepository.findById(id).orElse(null);

        if (event == null) {
            return "redirect:/admin/attendance";
        }

        if (!event.isAttendanceOpen()) {
            model.addAttribute("message", "Attendance is closed.");
            return "attendance-result";
        }
        System.out.println("Opened At: " + event.getAttendanceOpenedAt());
        System.out.println("Current Time: " + LocalDateTime.now());
        long remainingSeconds = 0;

        if (event.getAttendanceOpenedAt() != null) {
            LocalDateTime expiryTime = event.getAttendanceOpenedAt().plusMinutes(15);

            remainingSeconds = Duration
                    .between(LocalDateTime.now(), expiryTime)
                    .getSeconds();

            if (remainingSeconds < 0) {
                remainingSeconds = 0;
            }
        }

        model.addAttribute("event", event);
        model.addAttribute("remainingSeconds", remainingSeconds);

        return "attendance-qr";
    }

}