package com.resresa.authentication.payload;

public class JwtAuthenticationResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private String role;
    private Integer organizationId;
    private Long userId;

    public JwtAuthenticationResponse(String accessToken, String role, Integer organizationId, Long userId) {
        this.accessToken = accessToken;
        this.role = role;
        this.organizationId = organizationId;
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getRole() { return role; }

    public void setRole(String role) { this.role = role; }

    public Integer getOrganizationId() { return organizationId; }

    public void setOrganizationId(Integer organizationId) { this.organizationId = organizationId; }

    public Long getUserId() { return userId; }

    public void setUserId(Long userId) { this.userId = userId; }
}
