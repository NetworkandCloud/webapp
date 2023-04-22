package com.example.assignment1.config;

import com.example.assignment1.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;



@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    CustomUserDetailsService userService;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests((authz) -> authz.requestMatchers(HttpMethod.POST, "/v2/user/*").permitAll()
                .requestMatchers(new AntPathRequestMatcher("/healthz/*", "GET")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/v2/product/*", "GET")).permitAll()
                .anyRequest()
                .authenticated());
        http.csrf((csrf) -> csrf.disable());
        http.httpBasic();
        return http.build();
    }
}
