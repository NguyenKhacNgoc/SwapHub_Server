package com.server.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.server.ecommerce.entity.ImgReport;
import com.server.ecommerce.entity.Report;

public interface ImgReportRepository extends JpaRepository<ImgReport, String> {
    @Query("SELECT img from ImgReport img WHERE img.report = :reportId")
    List<ImgReport> findByReport(@Param("reportId") Report report);

}
