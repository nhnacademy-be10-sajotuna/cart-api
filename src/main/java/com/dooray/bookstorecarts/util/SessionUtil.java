package com.dooray.bookstorecarts.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SessionUtil {
    public static HttpSession getSession(HttpServletRequest request) {
        return request.getSession();
    }
}
