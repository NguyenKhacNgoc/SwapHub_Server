package com.server.ecommerce.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.server.ecommerce.dto.request.UserCreationRequest;
import com.server.ecommerce.dto.request.UserUpdateRequest;
import com.server.ecommerce.dto.response.UserDTOResponse;
import com.server.ecommerce.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    User updateUser(@MappingTarget User user, UserUpdateRequest request);

    UserDTOResponse toUserResponse(User user);

}
