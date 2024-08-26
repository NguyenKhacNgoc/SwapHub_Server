package com.server.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.server.ecommerce.dto.request.UserCreationRequest;
import com.server.ecommerce.dto.request.UserUpdateRequest;
import com.server.ecommerce.dto.response.ApiResponse;
import com.server.ecommerce.dto.response.UserDTOResponse;
import com.server.ecommerce.services.UserService;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@RequestBody UserCreationRequest request) {
        userService.createUser(request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @GetMapping("users")
    public ApiResponse<?> getAllUser() {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.getAllUser());
        return apiResponse;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GetMapping("user/{id}")
    public ApiResponse<?> getIn4OfUserByEmail(@PathVariable String id) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.getIn4OfUser(id));
        return apiResponse;

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @PutMapping("/user/update/{id}")
    public ApiResponse<UserDTOResponse> updateUser(@PathVariable String id, @RequestBody UserUpdateRequest request) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.updateUser(id, request));
        return apiResponse;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GetMapping("/checkexistPNB")
    public ApiResponse<?> checkexistPNB(@RequestParam("phoneNumber") String phoneNumber) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.checkexistPNB(phoneNumber));
        return apiResponse;
    }

}
