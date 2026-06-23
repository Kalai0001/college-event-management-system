package com.eventsphere.repository;

import com.eventsphere.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Student findByEmailAndPassword(String email, String password);

}