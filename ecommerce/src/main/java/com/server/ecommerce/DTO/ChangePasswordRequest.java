package com.server.ecommerce.DTO;

public class ChangePasswordRequest {
    private String password;
    private String newPassword;

    public ChangePasswordRequest() {
    }

    public ChangePasswordRequest(String password, String newPassword) {
        this.password = password;
        this.newPassword = newPassword;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return this.newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}
