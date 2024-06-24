package com.server.ecommerce.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.server.ecommerce.DTO.Request.UserDTO;
import com.server.ecommerce.DTO.Response.UserDTOResponse;
import com.server.ecommerce.Entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserDTO request);

    User updateUser(@MappingTarget User user, UserDTO request);

    UserDTOResponse toUserResponse(User user);

}
