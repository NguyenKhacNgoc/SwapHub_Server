package com.server.ecommerce.DTO;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDTO {
    private String email;
    private String password;
    private int verificationCode;
    private String fullName;

    private String phoneNumber;

    private String province;

    private String district;

    private String ward;

    private String sex;

    private Date dateofbirth;

}
