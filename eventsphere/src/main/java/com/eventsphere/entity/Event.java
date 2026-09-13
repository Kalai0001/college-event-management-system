// package com.eventsphere.entity;

// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;

// @Entity
// public class Event {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     private String name;
//     private String location;
//     private String date;
//     private String time;
//     private String description;
//     private String venue;
//     private String organizer;

//     // NEW FIELD
//     private Double fee;
//     private String status;
//     private boolean deleted = false;

//     public Event() {
//     }

//     public Long getId() {
//         return id;
//     }

//     public void setId(Long id) {
//         this.id = id;
//     }

//     public String getName() {
//         return name;
//     }

//     public void setName(String name) {
//         this.name = name;
//     }

//     public String getLocation() {
//         return location;
//     }

//     public void setLocation(String location) {
//         this.location = location;
//     }

//     public String getDate() {
//         return date;
//     }

//     public void setDate(String date) {
//         this.date = date;
//     }

//     public String getTime() {
//         return time;
//     }

//     public void setTime(String time) {
//         this.time = time;
//     }

//     public String getDescription() {
//         return description;
//     }

//     public void setDescription(String description) {
//         this.description = description;
//     }

//     public String getVenue() {
//         return venue;
//     }

//     public void setVenue(String venue) {
//         this.venue = venue;
//     }

//     public String getOrganizer() {
//         return organizer;
//     }

//     public void setOrganizer(String organizer) {
//         this.organizer = organizer;
//     }

//     public Double getFee() {
//         return fee;
//     }

//     public void setFee(Double fee) {
//         this.fee = fee;
//     }

//     public String getStatus() {
//         return status;
//     }

//     public void setStatus(String status) {
//         this.status = status;
//     }
// }

package com.eventsphere.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;
    private String date;
    private String time;
    private String description;
    private String venue;
    private String organizer;

    private Double fee;
    private String status;

    // Attendance QR status
    private boolean attendanceOpen = false;
    // Time when attendance was opened
    private LocalDateTime attendanceOpenedAt;
    private boolean deleted = false;

    public Event() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isAttendanceOpen() {
        return attendanceOpen;
    }

    public void setAttendanceOpen(boolean attendanceOpen) {
        this.attendanceOpen = attendanceOpen;
    }

    public LocalDateTime getAttendanceOpenedAt() {
        return attendanceOpenedAt;
    }

    public void setAttendanceOpenedAt(LocalDateTime attendanceOpenedAt) {
        this.attendanceOpenedAt = attendanceOpenedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}