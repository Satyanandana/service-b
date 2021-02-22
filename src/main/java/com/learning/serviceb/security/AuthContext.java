package com.learning.serviceb.security;

public class AuthContext {

    private static final ThreadLocal<Boolean> isAuthenticated = new ThreadLocal<>();
    private static final ThreadLocal<String> requestId = new ThreadLocal<>();

    public static Boolean getIsAuthenticated() {
        return isAuthenticated.get();
    }

    public static void setIsAuthenticated(Boolean isAuthenticated) {
        AuthContext.isAuthenticated.set(isAuthenticated);
    }

    public static String getRequestId() {
        return requestId.get();
    }

    public static void setRequestId(String requestId) {
        AuthContext.requestId.set(requestId);
    }



}
