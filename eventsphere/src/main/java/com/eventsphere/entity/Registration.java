// package com.eventsphere.entity;

// import java.time.LocalDateTime;

// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;

// @Entity
// public class Registration {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     private Long studentId;

//     private Long eventId;

//     private String paymentStatus;

//     // Stores when the student registered
//     private LocalDateTime registrationDate;

//     public Registration() {
//     }

//     public Long getId() {
//         return id;
//     }

//     public void setId(Long id) {
//         this.id = id;
//     }

//     public Long getStudentId() {
//         return studentId;
//     }

//     public void setStudentId(Long studentId) {
//         this.studentId = studentId;
//     }

//     public Long getEventId() {
//         return eventId;
//     }

//     public void setEventId(Long eventId) {
//         this.eventId = eventId;
//     }

//     public String getPaymentStatus() {
//         return paymentStatus;
//     }

//     public void setPaymentStatus(String paymentStatus) {
//         this.paymentStatus = paymentStatus;
//     }

//     public LocalDateTime getRegistrationDate() {
//         return registrationDate;
//     }

//     public void setRegistrationDate(LocalDateTime registrationDate) {
//         this.registrationDate = registrationDate;
//     }
// }

package com.eventsphere.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;

    private Long eventId;

    private String paymentStatus;

    // New field for attendance
    private String attendance = "ABSENT";

    // Stores when the student registered
    private LocalDateTime registrationDate;
    private LocalDateTime attendanceTime;

    public Registration() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LocalDateTime getAttendanceTime() {
        return attendanceTime;
    }

    public void setAttendanceTime(LocalDateTime attendanceTime) {
        this.attendanceTime = attendanceTime;
    }
}