package com.backend.thesis.service;

import com.backend.thesis.domain.entity.UserEntity;
import com.backend.thesis.domain.repository.IUserRepository;
import com.backend.thesis.utility.Constants;
import com.backend.thesis.utility.Helper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final IUserRepository userRepository;

    public UserService(final IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void processRequest(final HttpServletRequest request, final HttpServletResponse response) {
        final String requestCookie = request.getHeader(Constants.SESSION_COOKIE_NAME);
        final String currentCookie = (requestCookie == null || requestCookie.isEmpty()) ? Helper.getUniqueID() : requestCookie;

        response.setHeader(Constants.SESSION_COOKIE_NAME, currentCookie);
        this.refreshUser(currentCookie);
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
