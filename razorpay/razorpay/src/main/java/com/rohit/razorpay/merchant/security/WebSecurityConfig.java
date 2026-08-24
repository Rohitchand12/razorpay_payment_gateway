package com.rohit.razorpay.merchant.security;

import com.rohit.razorpay.common.Idempotency.IdempotencyFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private static final String[] JWT_ROUTES = {
            "/api/v1/auth/**",
            "/api/v1/merchant/**",
            "/api/v1/admin/**",
            "/api/v1/actuator/**"
    };
    private static final String[] API_KEY_ROUTES = {
            "/api/v1/orders/**",
            "/api/v1/payments/**",
            "/api/v1/vault/**",
    };

    private final JwtMerchantFilter jwtMerchantFilter;
    private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;
    private final IdempotencyFilter idempotencyFilter;

    @Bean
    @Order(1)
    public SecurityFilterChain jwtChain(HttpSecurity httpSecurity){
        return httpSecurity
                .securityMatcher(JWT_ROUTES)
                .csrf((csrfConf)->csrfConf.disable())
                .sessionManagement((session)->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin((form)->form.disable())
                .authorizeHttpRequests(auth->{
                    auth
                            .requestMatchers("/api/v1/auth/login","/api/v1/auth/signup").permitAll()
                            .anyRequest().authenticated();
                })
                .addFilterBefore(jwtMerchantFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(idempotencyFilter,JwtMerchantFilter.class)
                .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain apiKeyChain(HttpSecurity httpSecurity){
        return httpSecurity
                .securityMatcher(API_KEY_ROUTES)
                .csrf((csrfConf)->csrfConf.disable())
                .sessionManagement((session)->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin((form)->form.disable())
                .authorizeHttpRequests(auth->{
                    auth
                            .anyRequest().authenticated();
                })
                .addFilterBefore(apiKeyAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(idempotencyFilter,ApiKeyAuthenticationFilter.class)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(MerchantUserDetailsService merchantUserDetailsService,
                                                PasswordEncoder passwordEncoder){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(merchantUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }
}
