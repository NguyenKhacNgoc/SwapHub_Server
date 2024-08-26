package com.server.ecommerce.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.server.ecommerce.dto.request.UserCreationRequest;
import com.server.ecommerce.dto.request.UserUpdateRequest;
import com.server.ecommerce.dto.response.UserDTOResponse;
import com.server.ecommerce.entity.User;
import com.server.ecommerce.exception.AppException;
import com.server.ecommerce.exception.ErrorCode;
import com.server.ecommerce.mapper.UserMapper;
import com.server.ecommerce.repository.UserRepository;

import jakarta.ws.rs.core.Response;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Value("${keycloak.realm}")
    private String realm;
    @Autowired
    private Keycloak keycloak;

    public UserDTOResponse toUserDTOResponse(User user) {
        return userMapper.toUserResponse(user);
    }

    public List<UserDTOResponse> getAllUser() {
        List<User> users = userRepository.findAll();
        List<UserDTOResponse> userDTOResponses = new ArrayList<>();
        for (User user : users) {
            userDTOResponses.add(toUserDTOResponse(user));

        }
        return userDTOResponses;

    }

    public UserDTOResponse getIn4OfUser(String id) {
        if (id == null) {
            return getMyInfo();

        }
        return userMapper.toUserResponse(
                userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));

    }

    public void createUser(UserCreationRequest request) {
        UsersResource usersResource = keycloak.realm(realm).users();
        UserRepresentation userPRepresentation = new UserRepresentation();
        userPRepresentation.setUsername(request.getEmail());
        userPRepresentation.setEmail(request.getEmail());
        userPRepresentation.setFirstName(request.getFullName());
        userPRepresentation.setLastName(request.getFullName());
        userPRepresentation.setEnabled(true);
        userPRepresentation.setCredentials(Collections.singletonList(new CredentialRepresentation() {
            {
                setType(CredentialRepresentation.PASSWORD);
                setValue(request.getPassword());
                setTemporary(false);
            }
        }));
        Response response = usersResource.create(userPRepresentation);
        if (response.getStatus() == 201) {
            String pathUri = response.getLocation().getPath();
            int lastSlashIndex = pathUri.lastIndexOf("/");
            String userId = pathUri.substring(lastSlashIndex + 1);

            User user = userMapper.toUser(request);
            user.setId(userId);
            user.setStatus("Bình thường");
            userRepository.save(user);

        } else if (response.getStatus() == 409) {
            throw new AppException(ErrorCode.USER_EXISTED);

        }
    }

    public UserDTOResponse updateUser(String id, UserUpdateRequest request) {

        if (id == null) {
            id = SecurityContextHolder.getContext().getAuthentication().getName();

        }

        var user = keycloak.realm(realm).users().get(id).toRepresentation();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFullName());
        user.setLastName(request.getFullName());
        keycloak.realm(realm).users().get(id).update(user);
        return userMapper.toUserResponse(
                userRepository.save(userMapper.updateUser(userRepository.findById(id).get(), request)));

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
