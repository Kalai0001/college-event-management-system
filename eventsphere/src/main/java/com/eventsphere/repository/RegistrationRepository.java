package com.eventsphere.repository;

import com.eventsphere.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistrationRepository
        extends JpaRepository<Registration, Long> {

    boolean existsByStudentIdAndEventId(
            Long studentId,
            Long eventId);

    List<Registration> findByStudentId(Long studentId);
}