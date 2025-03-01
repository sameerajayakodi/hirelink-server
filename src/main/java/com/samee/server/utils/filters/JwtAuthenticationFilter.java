package com.samee.server.utils.filters;

import com.samee.server.service.auth.JWTService;
import com.samee.server.service.impl.CompanyDetailsServiceImpl;
import com.samee.server.service.impl.MyUserDetailsService;
import com.samee.server.service.impl.TrainerDetailsServiceImpl;
import com.samee.server.utils.UserRoles;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final ApplicationContext applicationContext;

    @Autowired
    public JwtAuthenticationFilter(JWTService jwtService, ApplicationContext applicationContext) {
        this.jwtService = jwtService;
        this.applicationContext = applicationContext;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String jwtToken = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
            username = jwtService.extractUserName(jwtToken);

            // Add more detailed debugging
            System.out.println("Extracted JWT Token: " + jwtToken);
            System.out.println("Extracted Username: " + username);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Extract the role from the token to determine which UserDetailsService to use
            UserRoles userRole = jwtService.extractRole(jwtToken);
            System.out.println("Extracted Role: " + userRole);

            UserDetails userDetails;

            // Use appropriate UserDetailsService based on the role
            try {
                if (userRole == UserRoles.COMPANY) {
                    userDetails = applicationContext.getBean(CompanyDetailsServiceImpl.class).loadUserByUsername(username);
                } else if (userRole == UserRoles.TRAINER) {
                    userDetails = applicationContext.getBean(TrainerDetailsServiceImpl.class).loadUserByUsername(username);
                } else {
                    userDetails = applicationContext.getBean(MyUserDetailsService.class).loadUserByUsername(username);
                }

                if (jwtService.validateToken(jwtToken, userDetails)) {
                    System.out.println("UserDetails authorities: " + userDetails.getAuthorities());

                    UsernamePasswordAuthenticationToken token =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(token);

                    // Add debug logging for the created token
                    System.out.println("Created authentication token - Principal: " + token.getPrincipal());
                    System.out.println("Created authentication token - Authorities: " + token.getAuthorities());
                }
            } catch (Exception e) {
                System.out.println("Authentication error: " + e.getMessage());
                // Don't throw exception here, let the request continue to be processed by the filter chain
                // The security context will remain null, which will result in a 401 response
            }
        }

        filterChain.doFilter(request, response);
    }
}