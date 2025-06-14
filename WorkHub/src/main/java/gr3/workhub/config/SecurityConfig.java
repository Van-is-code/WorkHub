package gr3.workhub.config;

import gr3.workhub.security.JwtAuthenticationFilter;
import gr3.workhub.security.ApiKeyFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ApiKeyFilter apiKeyFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/workhub/api/v1/recruiter/login",
                                "/workhub/api/v1/candidate/login",
                                "/workhub/api/v1/admin/login",
                                "/workhub/api/v1/recruiter/register",
                                "/workhub/api/v1/candidate/register",
                                "/workhub/api/v1/activate",
                                "/workhub/api/v1/reset-password",
                                "/swagger-ui/**",
                                "/v3/**",
                                "/swagger-ui.html",
                                "/workhub/api/v1/forgot-password",
                                "/workhub/api/v1/join/interview/**",
                                "/workhub/api/v1/interview-sessions/join/**",
                                "/error/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                );
        http.addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}