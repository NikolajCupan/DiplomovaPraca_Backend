package com.backend.thesis.service;

import com.backend.thesis.domain.entity.UserEntity;
import com.backend.thesis.domain.repository.UserRepository;
import com.backend.thesis.utility.Constants;
import com.backend.thesis.utility.Helper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void processRequest(final HttpServletRequest request, final HttpServletResponse response) {
        final String requestCookie = request.getHeader(Constants.SESSION_COOKIE_NAME);
        String usedCookie = "";

        if (requestCookie != null && !requestCookie.isEmpty()) {
            final UserEntity userEntity = this.userRepository.findByCookie(requestCookie);

            if (userEntity != null) {
                usedCookie = userEntity.getCookie();
            }
        }

        if (usedCookie.isEmpty()) {
            usedCookie = Helper.getUniqueID();
        }

        response.setHeader(Constants.SESSION_COOKIE_NAME, usedCookie);
        this.refreshUser(usedCookie);

        if (requestCookie == null || !requestCookie.isEmpty()) {
            request.setAttribute(Constants.SESSION_COOKIE_NAME, usedCookie);
        }
    }

    private void refreshUser(final String cookie) {
        UserEntity foundUser = this.userRepository.findByCookie(cookie);

        if (foundUser == null) {
            UserEntity newUser = new UserEntity(cookie, Helper.currentDateTime(), Helper.currentDateTime());
            this.userRepository.save(newUser);
        } else {
            foundUser.setLastAccessedAt(Helper.currentDateTime());
            this.userRepository.save(foundUser);
        }
    }
}
