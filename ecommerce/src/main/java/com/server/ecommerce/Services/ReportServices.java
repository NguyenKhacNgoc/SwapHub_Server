package com.server.ecommerce.Services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import com.server.ecommerce.DTO.Request.idRequest;
import com.server.ecommerce.DTO.Response.DetailReportResponse;
import com.server.ecommerce.DTO.Response.ImageUploadResponse;
import com.server.ecommerce.DTO.Response.ReportResponse;
import com.server.ecommerce.DTO.Response.thongkeResponse;
import com.server.ecommerce.Entity.ImgReport;
import com.server.ecommerce.Entity.Report;
import com.server.ecommerce.Entity.User;
import com.server.ecommerce.Exception.AppException;
import com.server.ecommerce.Exception.ErrorCode;
import com.server.ecommerce.Repository.ImgReportRepository;
import com.server.ecommerce.Repository.PostRepository;
import com.server.ecommerce.Repository.ReportRepository;
import com.server.ecommerce.Repository.UserRepository;

@Service
public class ReportServices {
    @Autowired
    private ImgReportRepository imgReportRepository;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailServices emailServices;
    @Autowired
    private PostRepository postRepository;

    public List<ImageUploadResponse> copyImagesReportToImageUploadResponses(List<ImgReport> images) {
        List<ImageUploadResponse> listImageUploadResponses = new ArrayList<>();
        for (ImgReport img : images) {
            ImageUploadResponse imageUploadResponse = new ImageUploadResponse();
            imageUploadResponse.setPublicID(img.getPublicID());
            imageUploadResponse.setSecureUrl(img.getImgUrl());
            listImageUploadResponses.add(imageUploadResponse);
        }
        return listImageUploadResponses;
    }

    public ReportResponse responseReport(Report report) {
        ReportResponse reportResponse = ReportResponse.builder()
                .id(report.getId())
                .accuser(report.getAccuser().getEmail())
                .accused(report.getAccused().getEmail())
                .reason(report.getReason())
                .sendAt(report.getSendAt())
                .postID(report.getPost().getId())
                .status(report.getStatus())
                .verifier("UnKnown")
                .verifyAt(report.getVerifyAt())
                .result(report.getResult())
                .action(report.getAction())
                .build();
        if (report.getVerifier() != null) {
            reportResponse.setVerifier(report.getVerifier().getEmail());
        }
        return reportResponse;
    }

    public DetailReportResponse responseDetailReport(Report report) {
        DetailReportResponse detailReportResponse = new DetailReportResponse();
        detailReportResponse.setReportResponse(responseReport(report));
        detailReportResponse
                .setImages(copyImagesReportToImageUploadResponses(imgReportRepository.findByReport(report)));

        return detailReportResponse;
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public List<ReportResponse> getPendingReport() {
        List<Report> reports = reportRepository.findPendingReport();
        if (reports.isEmpty()) {
            throw new AppException(ErrorCode.REPORT_NOT_EXIST);
        }
        List<ReportResponse> reportResponses = new ArrayList<>();
        for (Report report : reports) {
            reportResponses.add(responseReport(report));
        }
        return reportResponses;

    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public DetailReportResponse getDetailReportByID(String reportID) {
        Report report = reportRepository.findById(reportID)
                .orElseThrow(() -> new AppException(ErrorCode.REPORT_NOT_EXIST));
        return responseDetailReport(report);
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public DetailReportResponse setVerificatedReport(idRequest request) {
        Report report = reportRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.REPORT_NOT_EXIST));
        if (report.getAccused() == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);

        }
        report.setStatus("Đã xác minh");
        report.setResult("Không vi phạm");
        report.setVerifier(userService.getUser());
        report.setVerifyAt(LocalDateTime.now());
        report.setAction("Không có");
        return responseDetailReport(reportRepository.save(report));
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public DetailReportResponse setWarningUser(idRequest request) {
        Report report = reportRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.REPORT_NOT_EXIST));
        User user = report.getAccused();
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);

        }
        user.setStatus("Cảnh báo");
        userRepository.save(user);
        report.setStatus("Đã xác minh");
        report.setResult("Vi phạm");
        report.setVerifier(userService.getUser());
        report.setVerifyAt(LocalDateTime.now());
        report.setAction("Cảnh báo");
        String contentMessage = "Chào bạn " + user.getPhoneNumber()
                + ", chúng tôi đã nhận được một tố cáo từ người dùng về việc bạn đã vi phạm những tiêu chuẩn cộng đồng của SwapHub. Sau khi xác minh, chúng tôi xác định việc vi phạm đó hoàn toàn chính xác. Nếu còn phát hiện bạn vi phạm lần sau tài khoản của bạn sẽ bị khoá vĩnh viễn.";
        emailServices.sendMessage(user.getEmail(), contentMessage);
        return responseDetailReport(reportRepository.save(report));
    }

    public DetailReportResponse blockUser(idRequest request) {
        Report report = reportRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.REPORT_NOT_EXIST));
        User user = report.getAccused();
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        user.setStatus("Khoá");
        userRepository.save(user);
        report.setStatus("Đã xác minh");
        report.setResult("Vi phạm");
        report.setVerifier(userService.getUser());
        report.setVerifyAt(LocalDateTime.now());
        report.setAction("Khoá tài khoản");
        String contentMessage = "Chào bạn " + user.getPhoneNumber()
                + ", chúng tôi đã nhận được một tố cáo về việc bạn đã vi phạm những tiêu chuẩn cộng đồng của SwapHub. Sau khi xác minh thông tin nhận được chúng tôi rất tiếc thông báo với bạn rằng tại khoản của bạn đã bị khoá.";
        emailServices.sendMessage(user.getEmail(), contentMessage);
        return responseDetailReport(reportRepository.save(report));
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public thongkeResponse getStatistics() {
        return thongkeResponse.builder()
                .post(postRepository.countPost())
                .user(userRepository.countUser())
                .vipham(reportRepository.countVipham())
                .build();
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public List<ReportResponse> findViphamByAccused(String email) {
        List<Report> reports = reportRepository.findReportByAccused(email);
        if (reports.isEmpty()) {
            throw new AppException(ErrorCode.REPORT_NOT_EXIST);
        }
        List<ReportResponse> reportResponses = new ArrayList<>();
        for (Report report : reports) {
            reportResponses.add(responseReport(report));
        }
        return reportResponses;

    }

}
