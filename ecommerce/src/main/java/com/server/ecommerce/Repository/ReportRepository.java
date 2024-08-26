package com.server.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.server.ecommerce.entity.Report;

public interface ReportRepository extends JpaRepository<Report, String> {

    @SuppressWarnings("null")
    @Query("SELECT r FROM Report r WHERE r.id = :id")
    Optional<Report> findById(@Param("id") String id);

    @Query("SELECT r from Report r WHERE r.status = 'Chờ xác minh'")
    List<Report> findPendingReport();

    @Query("SELECT COUNT(r) FROM Report r WHERE r.status = 'Đã xác minh' AND r.result = 'Vi phạm' ")
    long countVipham();

    @Query("SELECT r FROM Report r WHERE r.accused.email = :email AND r.status = 'Đã xác minh' ")
    List<Report> findReportByAccused(@Param("email") String email);

}
