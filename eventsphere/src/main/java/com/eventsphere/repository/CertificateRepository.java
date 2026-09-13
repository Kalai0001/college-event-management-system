// package com.eventsphere.repository;

// import com.eventsphere.entity.Certificate;
// import org.springframework.data.jpa.repository.JpaRepository;

// import java.util.List;

// public interface CertificateRepository
//         extends JpaRepository<Certificate, Long> {

//     List<Certificate> findByStudentId(Long studentId);
//     boolean existsByRegistrationId(Long registrationId);
// }

package com.eventsphere.repository;

import com.eventsphere.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CertificateRepository
        extends JpaRepository<Certificate, Long> {

    List<Certificate> findByStudentId(Long studentId);

    boolean existsByRegistrationId(Long registrationId);

    @Query(value = """
            SELECT DATE_FORMAT(certificate_date,'%b') AS month,
                   COUNT(*) AS total
            FROM certificate
            WHERE certificate_date IS NOT NULL
            GROUP BY MONTH(certificate_date), DATE_FORMAT(certificate_date,'%b')
            ORDER BY MONTH(certificate_date)
            """, nativeQuery = true)
    List<Object[]> getMonthlyCertificates();
}