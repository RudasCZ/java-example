package com.homework.morosystems.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SecurityUtil {

    private final PasswordEncoder passwordEncoder;


    /**
     * Checks if the provided username matches the currently authenticated user's username.
     *
     * @param username the username to check
     * @return true if the provided username matches the authenticated user's username, false otherwise
     */
    public boolean isCurrentAuthenticatedUsername(String username) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getName().equals(username);
    }

    public String encodePassword(CharSequence password) {
        return passwordEncoder.encode(password);
    }

}
