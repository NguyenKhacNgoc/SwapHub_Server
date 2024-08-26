package com.server.ecommerce.dto.request;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserUpdateRequest {
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private AddressDTO address;
    private String sex;
    private Date dateofbirth;

}
