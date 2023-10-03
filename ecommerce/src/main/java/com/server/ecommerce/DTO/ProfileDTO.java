package com.server.ecommerce.DTO;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProfileDTO {
    private String fullName;
    private String email;
    private String phoneNumber;

    private String address;

    private String sex;

    private Date dateofbirth;

}
