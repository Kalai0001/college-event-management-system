
package com.eventsphere.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.List;

@Service
public class EmailService {

    @Value("${brevo.api-key}")
    private String apiKey;

    @Value("${brevo.sender-email}")
    private String senderEmail;

    @Value("${brevo.sender-name:EventSphere College Event Management}")
    private String senderName;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendEmail(String to, String subject, String body) {
        try {
            Map<String, Object> payload = Map.of(
                "sender", Map.of(
                    "name", senderName,
                    "email", senderEmail
                ),
                "to", List.of(Map.of("email", to)),
                "subject", subject,
                "textContent", body
            );

            String json = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                    "https://api.brevo.com/v3/smtp/email"
                ))
                .timeout(Duration.ofSeconds(30))
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

            HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() < 200
                    || response.statusCode() >= 300) {
                throw new IllegalStateException(
                    "Brevo email API failed with HTTP status: "
                    + response.statusCode()
                );
            }

            System.out.println("Email sent successfully to: " + to);

        } catch (Exception e) {
            System.err.println("Email sending failed: " + e.getMessage());
            throw new RuntimeException("Unable to send email", e);
        }
    }
}


// package com.eventsphere.service;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.mail.SimpleMailMessage;
// import org.springframework.mail.javamail.JavaMailSender;
// import org.springframework.stereotype.Service;

// @Service
// public class EmailService {

//     @Autowired
//     private JavaMailSender mailSender;

//     public void sendEmail(String to, String subject, String body) {

//         try {

//             System.out.println("Sending email to: " + to);

//             SimpleMailMessage message = new SimpleMailMessage();
//             message.setTo(to);
//             message.setSubject(subject);
//             message.setText(body);

//             mailSender.send(message);

//             System.out.println("Email sent successfully.");

//         } catch (Exception e) {

//             System.out.println("Email sending failed.");
//             e.printStackTrace();

//             throw e;
//         }
//     }
// }