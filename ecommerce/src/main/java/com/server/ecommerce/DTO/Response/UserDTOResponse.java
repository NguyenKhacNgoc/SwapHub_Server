package com.server.ecommerce.DTO.Response;

import java.util.Date;
import java.util.Set;

import com.server.ecommerce.Entity.Address;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserDTOResponse {
    private String id;
    private String fullName;
    private String email;
    private String phoneNumber;

    private Address address;

    private String sex;

    private Date dateofbirth;
    private String status;
    private Set<String> role;

}
