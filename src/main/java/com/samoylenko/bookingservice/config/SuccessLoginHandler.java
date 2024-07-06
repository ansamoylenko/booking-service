package com.samoylenko.bookingservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
public class SuccessLoginHandler implements AuthenticationSuccessHandler {
    private final ObjectMapper objectMapper;
    private final UserDetailsService userService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        log.info("User {} has successfully logged in", authentication.getName());
        log.info("Session id: {}", request.getSession().getId());
        log.info("Authentication object: {}", authentication);

        var user = userService.loadUserByUsername(authentication.getName());
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(authentication));
    }
}

