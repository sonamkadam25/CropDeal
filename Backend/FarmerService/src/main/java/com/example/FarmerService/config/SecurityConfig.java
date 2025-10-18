package com.example.FarmerService.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private HeaderAuthFilter headerAuthFilter;
    

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
            		.requestMatchers("/farmers/register", "/farmers/login","/farmers/crops/allForHomePage", "/farmers/send-otp", "/farmers/reset-password").permitAll()
            		.requestMatchers("/farmers/all","farmers/*/status","/farmers/crops/delete/*").hasRole("ADMIN")
            		.requestMatchers("/farmers/crops/subscribe","/name/*").hasRole("DEALER")
            		.requestMatchers("/farmers/*/update","/name/*").hasAnyRole("FARMER","ADMIN") 		
            		.requestMatchers("/farmers/*/receipts","/farmers/*/bank","/farmers/crops/*/post").hasRole("FARMER") 
            		.requestMatchers("/farmers/crops/all").hasAnyRole("FARMER","DEALER","ADMIN") 		
            		.anyRequest().authenticated()
            )
        
            .addFilterBefore(headerAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
