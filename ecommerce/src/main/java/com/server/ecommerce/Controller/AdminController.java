package com.server.ecommerce.Controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.server.ecommerce.DTO.AuthResponse;

import com.server.ecommerce.DTO.PostResponseDTO;
import com.server.ecommerce.DTO.PostUpdateDTO;
import com.server.ecommerce.DTO.ReportResponse;
import com.server.ecommerce.DTO.UserDTO;
import com.server.ecommerce.DTO.thongkeResponse;
import com.server.ecommerce.Entity.Posts;
import com.server.ecommerce.Entity.Report;
import com.server.ecommerce.Entity.User;
import com.server.ecommerce.JWT.JwtTokenUtil;

import com.server.ecommerce.Respository.PostRespository;
import com.server.ecommerce.Respository.ReportRespository;
import com.server.ecommerce.Respository.UserRespository;
import com.server.ecommerce.Services.AuthServices;
import com.server.ecommerce.Services.EmailServices;
import com.server.ecommerce.Services.ResponseServices;

import io.micrometer.core.instrument.util.IOUtils;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private PostRespository postRespository;
    @Autowired
    private AuthServices authServices;
    @Autowired
    private ResponseServices responseServices;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private ReportRespository reportRespository;

    @Autowired
    private UserRespository userRespository;
    @Autowired
    private EmailServices emailServices;

    @GetMapping("/home")
    public String getHome() {
        return "admin-home";
    }

    @GetMapping("/getpendingpost")
    public String getPendingPost(Model model) {
        List<Posts> postPendings = postRespository.findByStatusPending();
        List<PostResponseDTO> postResponseDTOs = new ArrayList<>();
        for (Posts post : postPendings) {
            postResponseDTOs.add(responseServices.postResponse(post));
        }
        model.addAttribute("posts", postResponseDTOs);
        return "post/pending-post";
    }

    @GetMapping("/getListPostByUser")
    public String getListPostByUser(Model model,
            @RequestParam("email") String email) {
        Optional<User> existingUser = userRespository.findByEmail(email);
        if (existingUser.isPresent()) {
            List<Posts> posts = postRespository.findByUser(existingUser.get());
            List<PostResponseDTO> postResponseDTOs = new ArrayList<>();
            for (Posts post : posts) {
                postResponseDTOs.add(responseServices.postResponse(post));
            }
            model.addAttribute("posts", postResponseDTOs);
            return "post/listpost";
        } else
            return "page404";

    }

    @GetMapping("/getPostOfReport")
    public String getPostOfReport(Model model, @RequestParam("postID") Long postID) {
        Optional<Posts> exPost = postRespository.findById(postID);
        if (exPost.isPresent()) {
            PostResponseDTO postResponseDTO = responseServices.postResponse(exPost.get());
            model.addAttribute("post", postResponseDTO);
            return "post/detail-post";

        } else
            return "page404";
    }

    @GetMapping("/getProfileUser")
    public String getProfileUser(Model model, @RequestParam("email") String email) {
        Optional<User> existingUser = userRespository.findByEmail(email);
        if (existingUser.isPresent()) {
            model.addAttribute("profile", responseServices.responseProfileDTO(existingUser.get()));
            return "profile/profile";
        } else
            return "page404";
    }

    @GetMapping("/provinces")
    @ResponseBody
    public ResponseEntity<String> getProvinces() {
        try {
            // Đọc file provinces.json từ thư mục resources
            Resource resource = new ClassPathResource("provinces.json");
            InputStream inputStream = resource.getInputStream();
            String jsonContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            return ResponseEntity.ok(jsonContent);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to read provinces data.");
        }
    }

    @GetMapping("/login")
    public String login() {
        return "login";

    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> pLogin(@RequestBody UserDTO request) {
        if (authServices.authenticateAdmin(request.getEmail(), request.getPassword())) {

            String token = authServices.generateToken(request.getEmail());
            AuthResponse authResponse = new AuthResponse(token);
            return ResponseEntity.ok(authResponse);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping("/api/reviewPost")
    @ResponseBody
    public ResponseEntity<?> reviewPost(@RequestBody PostUpdateDTO request,
            @RequestHeader("Authorization") String authorization) {
        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            Optional<Posts> existingPost = postRespository.findById(request.getId());
            if (existingPost.isPresent()) {
                Posts post = existingPost.get();
                post.setStatus("ok");
                postRespository.save(post);
                return ResponseEntity.ok("Đã duyệt");

            } else {
                return ResponseEntity.notFound().build();

            }

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }

    @GetMapping("/getPendingReport")
    public String getPendingReport(Model model) {
        List<Report> reports = reportRespository.findPendingReport();
        List<ReportResponse> reportResponses = new ArrayList<>();
        for (Report report : reports) {
            reportResponses.add(responseServices.responseReport(report));
        }
        model.addAttribute("reports", reportResponses);
        return "/report/pending-report";
    }

    @GetMapping("/report/detail/{reportId}")
    public String getDetaiReport(@PathVariable("reportId") Long reportId, Model model) {
        Optional<Report> existingReport = reportRespository.findById(reportId);
        if (existingReport.isPresent()) {

            model.addAttribute("report", responseServices.responseDetailReport(existingReport.get()));
            return "report/detail-report";

        } else
            return "page404";

    }

    @GetMapping("/reportdetail/{reportId}")
    public String getReportDetail(@PathVariable("reportId") Long reportId, Model model) {
        Optional<Report> exReport = reportRespository.findById(reportId);
        if (exReport.isPresent()) {
            model.addAttribute("report", responseServices.responseDetailReport(exReport.get()));
            return "report/reportdetail";
        } else
            return "page404";
    }

    @PutMapping("/api/setVerificated")
    @ResponseBody
    public ResponseEntity<?> setVerificated(@RequestHeader("Authorization") String authorization,
            @RequestBody ReportResponse request) {
        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            if (userRespository.findByEmail(request.getAccused()).isPresent()
                    && reportRespository.findById(request.getId()).isPresent()) {

                Report report = reportRespository.findById(request.getId()).get();
                report.setStatus("Đã xác minh");
                report.setResult("Không vi phạm");
                report.setVerifier(userRespository.findByEmail(jwtTokenUtil.getEmailFromToken(token)).get());
                report.setVerifyAt(LocalDateTime.now());
                report.setAction("Không có");
                reportRespository.save(report);
                return ResponseEntity.ok().build();
            } else
                return ResponseEntity.badRequest().body("Người dùng không tồn tại");
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }

    @PutMapping("/api/setwarninguser")
    @ResponseBody
    public ResponseEntity<?> setWarningUser(@RequestHeader("Authorization") String authorization,
            @RequestBody ReportResponse request) {
        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            if (userRespository.findByEmail(request.getAccused()).isPresent()
                    && reportRespository.findById(request.getId()).isPresent()) {
                User user = userRespository.findByEmail(request.getAccused()).get();
                user.setStatus("Cảnh báo");
                userRespository.save(user);
                Report report = reportRespository.findById(request.getId()).get();
                report.setStatus("Đã xác minh");
                report.setResult("Vi phạm");
                report.setVerifier(userRespository.findByEmail(jwtTokenUtil.getEmailFromToken(token)).get());
                report.setVerifyAt(LocalDateTime.now());
                report.setAction("Cảnh báo");
                reportRespository.save(report);
                String contentMessage = "Chào bạn " + user.getPhoneNumber()
                        + ", chúng tôi đã nhận được một tố cáo từ người dùng về việc bạn đã vi phạm những tiêu chuẩn cộng đồng của SwapHub. Sau khi xác minh, chúng tôi xác định việc vi phạm đó hoàn toàn chính xác. Nếu còn phát hiện bạn vi phạm lần sau tài khoản của bạn sẽ bị khoá vĩnh viễn.";
                emailServices.sendMessage(request.getAccused(), contentMessage);
                return ResponseEntity.ok().build();
            } else
                return ResponseEntity.badRequest().body("Người dùng không tồn tại");
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PutMapping("/api/blockuser")
    @ResponseBody
    public ResponseEntity<?> blockUser(@RequestHeader("Authorization") String authorization,
            @RequestBody ReportResponse request) {
        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            if (userRespository.findByEmail(request.getAccused()).isPresent()
                    && reportRespository.findById(request.getId()).isPresent()) {
                User user = userRespository.findByEmail(request.getAccused()).get();
                user.setStatus("Khoá");
                userRespository.save(user);
                Report report = reportRespository.findById(request.getId()).get();
                report.setStatus("Đã xác minh");
                report.setResult("Vi phạm");
                report.setVerifier(userRespository.findByEmail(jwtTokenUtil.getEmailFromToken(token)).get());
                report.setVerifyAt(LocalDateTime.now());
                report.setAction("Khoá tài khoản");
                String contentMessage = "Chào bạn " + user.getPhoneNumber()
                        + ", chúng tôi đã nhận được một tố cáo về việc bạn đã vi phạm những tiêu chuẩn cộng đồng của SwapHub. Sau khi xác minh thông tin nhận được chúng tôi rất tiếc thông báo với bạn rằng tại khoản của bạn đã bị khoá.";
                emailServices.sendMessage(request.getAccused(), contentMessage);
                reportRespository.save(report);
                return ResponseEntity.ok().build();
            } else
                return ResponseEntity.badRequest().body("Người dùng không tồn tại");
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }

    @GetMapping("/thongke")
    public String thongke() {
        return "/statistics/statistics";
    }

    @GetMapping("/api/getStatistics")
    @ResponseBody
    public ResponseEntity<?> getStatistics(@RequestHeader("Authorization") String authorization) {
        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            thongkeResponse thongkeResponse = new thongkeResponse();
            thongkeResponse.setPost(postRespository.countPost());
            thongkeResponse.setUser(userRespository.countUser());
            thongkeResponse.setVipham(reportRespository.countVipham());
            return ResponseEntity.ok(thongkeResponse);
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/api/findUser")
    @ResponseBody
    public ResponseEntity<?> findUser(@RequestHeader("Authorization") String authorization,
            @RequestParam("email") String email) {
        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            Optional<User> existingUser = userRespository.findByEmail(email);
            if (existingUser.isPresent()) {
                return ResponseEntity.ok(responseServices.responseProfileDTO(existingUser.get()));

            } else
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }

    @GetMapping("/api/findPost")
    public ResponseEntity<?> findPost(@RequestHeader("Authorization") String authorization,
            @RequestParam("email") String email) {
        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            Optional<User> existingUser = userRespository.findByEmail(email);
            if (existingUser.isPresent()) {
                List<Posts> posts = postRespository.findByUser(existingUser.get());
                List<PostResponseDTO> postResponseDTOs = new ArrayList<>();
                for (Posts post : posts) {
                    postResponseDTOs.add(responseServices.postResponse(post));
                }
                return ResponseEntity.ok(postResponseDTOs);
            } else
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/api/findVipham")
    public ResponseEntity<?> findVipham(@RequestHeader("Authorization") String authorization,
            @RequestParam("email") String email) {
        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            List<Report> reports = reportRespository.findReportByAccused(email);
            List<ReportResponse> reportResponses = new ArrayList<>();
            for (Report report : reports) {
                reportResponses.add(responseServices.responseReport(report));
            }
            return ResponseEntity.ok(reportResponses);

        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
