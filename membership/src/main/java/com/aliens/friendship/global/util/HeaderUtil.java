package com.aliens.friendship.global.util;

import javax.servlet.http.HttpServletRequest;

public class HeaderUtil {

    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";

    public static String getAccessToken(HttpServletRequest request) {
        String headerValue = request.getHeader(HEADER_AUTHORIZATION);

        if (headerValue == null) {
            return null;
        }

        if (headerValue.startsWith(TOKEN_PREFIX)) {
            return headerValue.substring(TOKEN_PREFIX.length());
        }

        return null;
    }

    public static String getRefreshToken(HttpServletRequest request) {
        String headerValue = request.getHeader("RefreshToken");

        if (headerValue == null) {
            return null;
        }

        return headerValue;
    }

    public static String getFcmToken(HttpServletRequest request) {
        String headerValue = request.getHeader("FcmToken");

        if (headerValue == null) {
            return null;
        }

        return headerValue;
    }
}