package com.palavras.etiquetas.authservice.dto;

public class ValidateResponse {
    private boolean valid;
    private String email;
    private String profile;

    public ValidateResponse() {
    }

    public ValidateResponse(boolean valid, String email, String profile) {
        this.valid = valid;
        this.email = email;
        this.profile = profile;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
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
