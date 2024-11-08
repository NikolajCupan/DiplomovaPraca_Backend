package com.backend.thesis.utility.other;

import com.backend.thesis.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class RequestInterceptor implements HandlerInterceptor {
    private final UserService userService;

    public RequestInterceptor(final UserService userService) {
        this.userService = userService;
    }

    @CrossOrigin
    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object object) {
        this.userService.processRequest(request, response);
        return true;
    }

    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response, Object object, final ModelAndView model) {
    }

    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response, final Object object, final Exception exception) {
    }
}
