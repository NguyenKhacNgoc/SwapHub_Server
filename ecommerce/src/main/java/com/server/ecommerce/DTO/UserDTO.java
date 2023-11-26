package com.server.ecommerce.DTO;

import java.util.Date;

public class UserDTO {
    private String email;
    private String password;
    private int verificationCode;
    private String fullName;

    private String phoneNumber;

    private String address;

    private String sex;

    private Date dateofbirth;

    public UserDTO() {
    }

    public UserDTO(String email, String password, int verificationCode, String fullName, String phoneNumber,
            String address, String sex, Date dateofbirth) {
        this.email = email;
        this.password = password;
        this.verificationCode = verificationCode;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.sex = sex;
        this.dateofbirth = dateofbirth;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getVerificationCode() {
        return this.verificationCode;
    }

    public void setVerificationCode(int verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getDateofbirth() {
        return this.dateofbirth;
    }

    public void setDateofbirth(Date dateofbirth) {
        this.dateofbirth = dateofbirth;
    }

}
