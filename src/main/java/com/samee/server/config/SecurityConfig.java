package com.samee.server.config;

import com.samee.server.utils.filters.JwtAuthenticationFilter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtFilter;

    @Autowired
    public SecurityConfig(@Qualifier("myUserDetailsService") UserDetailsService userDetailsService,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtAuthenticationFilter;
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        // User endpoints
                        .requestMatchers("/api/v1/user/register", "/api/v1/user/login" ).permitAll()
                        // Company endpoints
                        .requestMatchers("/api/v1/company/register", "/api/v1/company/login").permitAll()
                        // Trainer endpoints
                        .requestMatchers("/api/v1/trainer/register", "/api/v1/trainer/login").permitAll()
                        // Course endpoints - publicly accessible
                        .requestMatchers("/api/v1/courses/all", "/api/v1/courses/{id}").permitAll()
                        .requestMatchers("/api/v1/courses/category/**", "/api/v1/courses/search").permitAll()
                        .requestMatchers("/api/v1/courses/trainer/{username}").permitAll()
                        // Health check
                        .requestMatchers("/api/v1/health").permitAll()
                        // Important: Allow OPTIONS requests for all routes (CORS preflight)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // File download endpoints - allow public access to specific download endpoint
                        .requestMatchers(HttpMethod.POST, "/api/v1/documents/download/**").permitAll()
                        // Application endpoints - add these rules
                        .requestMatchers("/api/v1/jobs/**").permitAll()
                        .requestMatchers("/api/v1/applications/**").hasAuthority("COMPANY")
                        // Role-based access
                        .requestMatchers("/api/v1/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/user/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/company/**").hasAnyAuthority("ADMIN", "COMPANY")
                        .requestMatchers("/api/v1/employer/**").hasAuthority("EMPLOYER")
                        // Trainer-specific endpoints requiring authentication
                        .requestMatchers("/api/v1/trainer/**").hasAuthority("TRAINER")
                        .requestMatchers("/api/v1/courses/create", "/api/v1/courses/update/**", "/api/v1/courses/delete/**").hasAuthority("TRAINER")
                        .requestMatchers("/api/v1/courses/trainer").hasAuthority("TRAINER")
                        .requestMatchers("/api/v1/seeker/**").hasAuthority("JOB_SEEKER")
                        .requestMatchers("/api/v1/documents/**").authenticated()
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .logout(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // Your React app origin
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));
        // Important: expose the Content-Disposition header for downloads
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Disposition"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}