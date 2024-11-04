package com.backend.thesis.helper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class RequestInterceptor implements HandlerInterceptor {
    @CrossOrigin
    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object object) {
        final String cookie = request.getHeader(Constants.SESSION_COOKIE_NAME);
        if (cookie == null || cookie.isEmpty()) {
            response.setHeader(Constants.SESSION_COOKIE_NAME, Helper.getUniqueID());
        } else {
            response.setHeader(Constants.SESSION_COOKIE_NAME, cookie);
        }

        return true;
    }

    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response, Object object, final ModelAndView model) {
    }

    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response, final Object object, final Exception exception) {
    }
}
