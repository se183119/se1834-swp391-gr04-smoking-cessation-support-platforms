package com.smokingcessation.platform.dto.response;

public class AuthResponse {

    private String token;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserResponse user;

    // Constructors
    public AuthResponse() {}

    public AuthResponse(String token, Long expiresIn, UserResponse user) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.user = user;
    }

    // Static factory methods
    public static AuthResponse success(String token, Long expiresIn, UserResponse user) {
        return new AuthResponse(token, expiresIn, user);
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "token='[PROTECTED]'" +
                ", tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", user=" + user +
                '}';
    }
}
