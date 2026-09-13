// package com.eventsphere.repository;

// import com.eventsphere.entity.Student;
// import org.springframework.data.jpa.repository.JpaRepository;

// public interface StudentRepository extends JpaRepository<Student, Long> {

//     Student findByEmailAndPassword(String email, String password);

// }

package com.eventsphere.repository;

import com.eventsphere.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;   // <-- Add this import

public interface StudentRepository extends JpaRepository<Student, Long> {

    Student findByEmailAndPassword(String email, String password);

    Student findByEmail(String email);

    
    List<Student> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrDepartmentContainingIgnoreCase(
            String name,
            String email,
            String department
    );
}