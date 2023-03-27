package com.miras.cclearner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
                .authorizeHttpRequests()

                .requestMatchers("/api/home", "/api/registration").permitAll()
                .requestMatchers("/content/**", "/requests/**").permitAll()
                .requestMatchers("/api/characters/user/**", "/api/category/user/**").permitAll()

                .requestMatchers("/api/characters/admin/**").hasAuthority("ADMIN")
                .requestMatchers("/api/admin/users/**").hasAuthority("ADMIN")

                .requestMatchers("/api/characters/adult/**").hasAuthority("ADULT")

                .requestMatchers("/api/characters/request/**").authenticated()
                .requestMatchers("/api/characters/mine").authenticated()

                .requestMatchers("/api/feedback", "/api/feedback/reply/**", "/api/feedback/add").authenticated()
                .requestMatchers("/api/feedback/admin/**").hasAuthority("ADMIN")

                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/api/login")
                .defaultSuccessUrl("/api/home", true)
                .permitAll()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/api/logout", "GET"))
                .logoutSuccessUrl("/api/home")

                .and()
                .exceptionHandling()
                .accessDeniedPage("/access-denied");

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}