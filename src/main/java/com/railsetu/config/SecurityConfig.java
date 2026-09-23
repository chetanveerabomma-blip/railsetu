package com.railsetu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserDetailsServiceImpl userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configure(http))
                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // For H2 console
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/h2-console/**",
                                "/api/auth/**",
                                "/api/public/**",
                                "/api/fare/calculate"
                        ).permitAll()
                        .requestMatchers("/api/admin/fares/**", "/api/admin/fare-rules/**")
                        .hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_FARE_ADMIN")
                        .requestMatchers("/api/admin/trains/**", "/api/admin/routes/**", "/api/admin/schedules/**", "/api/admin/coaches/**")
                        .hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_OPERATIONS_ADMIN")
                        .requestMatchers("/api/admin/bookings/**", "/api/admin/cancellations/**", "/api/admin/rac/**", "/api/admin/waitlist/**")
                        .hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_BOOKING_ADMIN")
                        .requestMatchers("/api/admin/verification/**")
                        .hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_VERIFIER")
                        .requestMatchers("/api/admin/revenue/**", "/api/admin/analytics/**")
                        .hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_FARE_ADMIN")
                        .requestMatchers("/api/admin/audit-logs/**", "/api/admin/system/**", "/api/admin/users/**")
                        .hasAuthority("ROLE_SUPER_ADMIN")
                        .requestMatchers("/api/admin/**").authenticated()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
