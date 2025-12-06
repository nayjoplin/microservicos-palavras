package com.palavras.etiquetas.authservice.dto;

public class LoginResponse {
    private String token;
    private String email;
    private String profile;

    public LoginResponse() {
    }

    public LoginResponse(String token, String email, String profile) {
        this.token = token;
        this.email = email;
        this.profile = profile;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
