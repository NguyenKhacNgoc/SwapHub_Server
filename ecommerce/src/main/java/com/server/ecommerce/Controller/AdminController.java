package com.server.ecommerce.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.server.ecommerce.dto.request.idRequest;
import com.server.ecommerce.dto.response.ApiResponse;
import com.server.ecommerce.services.PostServices;
import com.server.ecommerce.services.ReportServices;
import com.server.ecommerce.services.UserService;

import io.micrometer.core.instrument.util.IOUtils;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private PostServices postServices;
    @Autowired
    private UserService userService;
    @Autowired
    private ReportServices reportServices;

    @GetMapping("/home")
    public String getHome() {
        return "admin-home";
    }

    @GetMapping("/getpendingpost")
    public String getPendingPost(Model model) {
        model.addAttribute("posts", postServices.getPendingPost());
        return "post/pending-post";
    }

    @GetMapping("/getListPostByUser")
    public String getListPostByUser(Model model,
            @RequestParam("email") String email) {
        var posts = postServices.getPostsByUser(email);
        if (posts.isEmpty()) {
            return "page404";

        }
        model.addAttribute("posts", posts);
        return "post/listpost";

    }

    @GetMapping("/getPostOfReport")
    public String getPostOfReport(Model model, @RequestParam("postID") String postID) {
        var post = postServices.getPostByID(postID);
        if (post == null) {
            return "page404";
        }
        model.addAttribute("post", post);
        return "post/detail-post";

    }

    @GetMapping("/getProfileUser")
    public String getProfileUser(Model model, @RequestParam("email") String email) {
        try {
            model.addAttribute("profile", userService.getUserFromEmail(email));
            return "profile/profile";
        } catch (Exception e) {
            return "page404";
        }
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @PutMapping("/api/reviewPost")
    @ResponseBody
    public ApiResponse<?> reviewPost(@RequestBody idRequest request) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(postServices.acpPost(request));
        return apiResponse;
    }

    @GetMapping("/getPendingReport")
    public String getPendingReport(Model model) {
        model.addAttribute("reports", reportServices.getPendingReport());
        return "/report/pending-report";
    }

    @GetMapping("/report/detail/{reportId}")
    public String getDetaiReport(@PathVariable("reportId") String reportId, Model model) {
        try {
            model.addAttribute("report", reportServices.getDetailReportByID(reportId));
            return "report/detail-report";
        } catch (Exception exception) {
            return "page404";
        }
    }

    @GetMapping("/reportdetail/{reportId}")
    public String getReportDetail(@PathVariable("reportId") String reportId, Model model) {
        try {
            model.addAttribute("report", reportServices.getDetailReportByID(reportId));
            return "report/reportdetail";
        } catch (Exception exception) {
            return "page404";
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @PutMapping("/api/setVerificated")
    @ResponseBody
    public ApiResponse<?> setVerificated(@RequestBody idRequest request) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(reportServices.setVerificatedReport(request));
        return apiResponse;

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @PutMapping("/api/setwarninguser")
    @ResponseBody
    public ApiResponse<?> setWarningUser(@RequestBody idRequest request) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(reportServices.setWarningUser(request));
        return apiResponse;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @PutMapping("/api/blockuser")
    @ResponseBody
    public ApiResponse<?> blockUser(@RequestBody idRequest request) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(reportServices.blockUser(request));
        return apiResponse;
    }

    @GetMapping("/thongke")
    public String thongke() {
        return "/statistics/statistics";
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GetMapping("/api/getStatistics")
    @ResponseBody
    public ApiResponse<?> getStatistics() {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(reportServices.getStatistics());
        return apiResponse;

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GetMapping("/api/findUser")
    @ResponseBody
    public ApiResponse<?> findUser(@RequestParam("email") String email) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.getUserFromEmail(email));
        return apiResponse;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GetMapping("/api/findPost")
    @ResponseBody
    public ApiResponse<?> findPostOfUser(@RequestParam("email") String email) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(postServices.getPostsByUser(email));
        return apiResponse;

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GetMapping("/api/findVipham")
    @ResponseBody
    public ApiResponse<?> findVipham(@RequestParam("email") String email) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(reportServices.findViphamByAccused(email));
        return apiResponse;
    }

}
