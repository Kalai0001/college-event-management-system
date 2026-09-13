// package com.eventsphere.repository;

// import com.eventsphere.entity.Registration;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;

// import java.util.List;

// public interface RegistrationRepository extends JpaRepository<Registration, Long> {

//         boolean existsByStudentIdAndEventId(
//                         Long studentId,
//                         Long eventId);

//         List<Registration> findByStudentId(Long studentId);

//         List<Registration> findByPaymentStatus(String paymentStatus);

//         @Query("SELECT COUNT(DISTINCT r.studentId) FROM Registration r")
//         long countDistinctStudentsWithRegistration();

//         @Query(value = """
//                         SELECT DATE_FORMAT(registration_date,'%b') AS month,
//                                COUNT(*) AS total
//                         FROM registration
//                         WHERE registration_date IS NOT NULL
//                         GROUP BY MONTH(registration_date), DATE_FORMAT(registration_date,'%b')
//                         ORDER BY MONTH(registration_date)
//                         """, nativeQuery = true)
//         List<Object[]> getMonthlyRegistrations();
// }

package com.eventsphere.repository;

import com.eventsphere.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

        boolean existsByStudentIdAndEventId(
                        Long studentId,
                        Long eventId);

        Registration findByStudentIdAndEventId(
                        Long studentId,
                        Long eventId);

        List<Registration> findByStudentId(Long studentId);

        List<Registration> findByPaymentStatus(String paymentStatus);

        @Query("SELECT COUNT(DISTINCT r.studentId) FROM Registration r")
        long countDistinctStudentsWithRegistration();

        @Query(value = """
                        SELECT DATE_FORMAT(registration_date,'%b') AS month,
                               COUNT(*) AS total
                        FROM registration
                        WHERE registration_date IS NOT NULL
                        GROUP BY MONTH(registration_date), DATE_FORMAT(registration_date,'%b')
                        ORDER BY MONTH(registration_date)
                        """, nativeQuery = true)
        List<Object[]> getMonthlyRegistrations();
}