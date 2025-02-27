package com.example.chat.config;

import jakarta.servlet.http.Cookie;

public class CookieUtil {

    private CookieUtil() {
    }

    public static Cookie createCookie(String key, String value, int maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(true);
        cookie.setHttpOnly(false);
        cookie.setPath("/");
        return cookie;
    }

    public static Cookie createDefaultCookie(String key, String value) {
        return createCookie(key, value, 24 * 60 * 60); // 1일 유지
    }
}

