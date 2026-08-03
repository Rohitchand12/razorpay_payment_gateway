package com.rohit.razorpay.merchant.security;


import com.rohit.razorpay.common.exceptions.ResourceNotFoundException;
import com.rohit.razorpay.merchant.entity.ApiKeyEntity;
import com.rohit.razorpay.merchant.repository.ApiKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final String API_KEY_PREF = "Basic ";
    private final BCryptPasswordEncoder BCRYPT = new BCryptPasswordEncoder();
    private final ApiKeyRepository apiKeyRepository;
    private final MerchantContext merchantContext;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Incoming request {}", request.getRequestURI());
        try{

            String authHeader = request.getHeader("Authorization");

            //Authorization : Basic key:secret;
            if(authHeader == null || !authHeader.startsWith(API_KEY_PREF)){
                filterChain.doFilter(request,response);
                return;
            }

            //The api key will be base 64 decoded
            String[] decoded = decode(authHeader);
            if(decoded == null){
                throw new BadRequestException("Malfunctioned or invalid api key");
            }

            String keyId = decoded[0];
            String keySecret = decoded[1];


            ApiKeyEntity apiKey = apiKeyRepository.findByKeyId(keyId)
                    .orElseThrow(()-> new ResourceNotFoundException("ApiKey",keyId));

            //check if the api key is disabled or is not valid
            if(!apiKey.getEnabled() || !isValidSecret(apiKey,keySecret)){
                throw new BadRequestException("Disabled or invalid/expired api key");
            }

            var auth = new UsernamePasswordAuthenticationToken(keyId,
                    null,
                    List.of(new SimpleGrantedAuthority("API_KEY_ROLE"))
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
            merchantContext.setMerchantId(apiKey.getMerchant().getId());
            merchantContext.setKeyId(apiKey.getKeyId());

            filterChain.doFilter(request,response);
        }catch (Exception e){
            handlerExceptionResolver.resolveException(request,response,null,e);
        }
    }

    private boolean isValidSecret(ApiKeyEntity apiKey, String rawSecret){
        boolean matches = BCRYPT.matches(rawSecret,apiKey.getKeySecretHash());
        if(matches) return true;
        return apiKey.getGracePeriodExpiresAt() != null
                && apiKey.getPrevKeySecretHash() != null
                && LocalDateTime.now().isBefore(apiKey.getGracePeriodExpiresAt());
    }

    private String[] decode(String header){
        String encodedKey = header.substring(API_KEY_PREF.length());
        String decodedKey = new String(Base64.getDecoder().decode(encodedKey),StandardCharsets.UTF_8);
        int colon = decodedKey.indexOf(":");
        if(colon < 1) return null;
        String key = decodedKey.substring(0,colon);
        String keySecret = decodedKey.substring(colon+1);
        return new String[] {key,keySecret};
    }
}
