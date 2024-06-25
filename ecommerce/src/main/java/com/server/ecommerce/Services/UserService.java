package com.server.ecommerce.Services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.server.ecommerce.DTO.Request.ChangePasswordRequest;
import com.server.ecommerce.DTO.Request.UserDTO;
import com.server.ecommerce.DTO.Response.UserDTOResponse;
import com.server.ecommerce.Entity.User;
import com.server.ecommerce.Enums.Role;
import com.server.ecommerce.Exception.AppException;
import com.server.ecommerce.Exception.ErrorCode;
import com.server.ecommerce.Mapper.UserMapper;
import com.server.ecommerce.Repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    public UserDTOResponse toUserDTOResponse(User user) {
        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public List<UserDTOResponse> getAllUser() {
        List<User> users = userRepository.findAll();
        List<UserDTOResponse> userDTOResponses = new ArrayList<>();
        for (User user : users) {
            userDTOResponses.add(toUserDTOResponse(user));

        }
        return userDTOResponses;

    }

    @PostAuthorize("returnObject.email == authentication.name")
    public UserDTOResponse getIn4OfUser(String email) {
        return userMapper.toUserResponse(
                userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));

    }

    public UserDTOResponse createUser(UserDTO request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        User user = userMapper.toUser(request);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
        user.setRole(roles);
        user.setStatus("Bình thường");
        return userMapper.toUserResponse(userRepository.save(user));
    }

    public UserDTOResponse updateUser(UserDTO request) {
        return userMapper
                .toUserResponse(userRepository.save(userMapper.updateUser(userRepository.findByEmail(request.getEmail())
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)), request)));

    }

    public UserDTOResponse changePassWord(ChangePasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassWord(), user.getPassword());
        if (authenticated) {
            user.setPassword(passwordEncoder.encode(request.getNewPassWord()));
            userRepository.save(user);
            return userMapper.toUserResponse(user);
        }
        throw new AppException(ErrorCode.UNAUTHENTICATED);

    }

    public UserDTOResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        return userMapper.toUserResponse(userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    public User getUser() {
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    public User getUserFromEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    public Boolean checkexistPNB(String phoneNumber) {
        Optional<User> exUser = userRepository.findByPhoneNumber(phoneNumber);
        if (exUser.isPresent()) {
            return true;
        } else
            return false;

    }
}
