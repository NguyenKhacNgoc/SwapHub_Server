package com.server.ecommerce.DTO;

public class ImageUploadResponse {
    private String publicID;
    private String secureUrl;

    public ImageUploadResponse() {
    }

    public ImageUploadResponse(String publicID, String secureUrl) {
        this.publicID = publicID;
        this.secureUrl = secureUrl;
    }

    public String getPublicID() {
        return this.publicID;
    }

    public void setPublicID(String publicID) {
        this.publicID = publicID;
    }

    public String getSecureUrl() {
        return this.secureUrl;
    }

    public void setSecureUrl(String secureUrl) {
        this.secureUrl = secureUrl;
    }

}
