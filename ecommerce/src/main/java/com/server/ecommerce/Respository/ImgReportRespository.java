package com.server.ecommerce.Respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.server.ecommerce.Entity.ImgReport;
import com.server.ecommerce.Entity.Report;

public interface ImgReportRespository extends JpaRepository<ImgReport, Long> {
    @Query("SELECT img from ImgReport img WHERE img.report = :reportId")
    List<ImgReport> findByReport(@Param("reportId") Report report);

}
