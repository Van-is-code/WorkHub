package gr3.workhub.config;

import gr3.workhub.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;


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
                                "/workhub/api/v1/admin/**",
                                "/workhub/api/v1/admin/jobs/**"
                        ).hasRole("ADMIN")
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
                                "/workhub/api/v1/interview-sessions/test-email",
                                "/workhub/api/v1/jobs",
                                "/workhub/api/v1/jobs/**",
                                "/error/**",
                                "/workhub/api/v1/login",
                                "/workhub/api/v1/companies",
                                "/workhub/api/v1/companies/**",
                                "/workhub/api/v1/service-packages",
                                "/workhub/api/v1/service-packages/**",
                                "/workhub/api/v1/payments/**",
                                "/workhub/api/v1/user-benefits/**",
                                "/workhub/api/v1/job-categories/**",
                                "/workhub/api/v1/job-types/**",
                                "/workhub/api/v1/job-positions/**",
                                "/workhub/api/v1/skills/**",
                                "/workhub/api/v1/skill/**",
                                "/workhub/api/v1/search/**",
                                "/workhub/api/v1/notifications/**",
                                "/workhub/api/v1/messages/**",
                                "/workhub/api/v1/reviews/**",
                                "/workhub/api/v1/resume-reviews/**",
                                "/workhub/api/v1/resume-views/**",
                                "/workhub/api/v1/saved-jobs/**",
                                "/workhub/api/v1/applications/**",
                                "/workhub/api/v1/resumes/**",
                                "/workhub/api/v1/interview-slots/**",
                                "/workhub/api/v1/interview-sessions/**",
                                "/workhub/api/v1/service-features/**",
                                "/workhub/api/v1/user-packages/**",
                                "/workhub/api/v1/user-package-histories/**",
                                "/workhub/api/v1/inspections/**",
                                "/workhub/api/v1/users/**",
                                "/workhub/api/v1/admins/**",
                                "/workhub/api/v1/register",
                                "/workhub/api/v1/register/**",
                                "/workhub/api/v1/activate/**",
                                "/workhub/api/v1/forgot-password/**",
                                "/workhub/api/v1/reset-password/**",
                                "/workhub/api/v1/health",
                                "/workhub/api/v1/health/**",
                                "/workhub/api/v1/debug/**",
                                "/workhub/api/v1/test/**",
                                "/actuator/**",
                                "/actuator/health",
                                "/actuator/info",
                                "/error",
                                "/favicon.ico",
                                "/",
                                "/index.html",
                                "/workhub/api/v1/payments/paypal/return"

                        ).permitAll()
                        .anyRequest().authenticated()
                );
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}