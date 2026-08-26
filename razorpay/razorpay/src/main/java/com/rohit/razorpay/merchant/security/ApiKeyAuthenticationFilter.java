package com.rohit.razorpay.merchant.security;


import com.rohit.razorpay.common.exceptions.RateLimitException;
import com.rohit.razorpay.common.exceptions.ResourceNotFoundException;
import com.rohit.razorpay.common.ratelimit.FixedWindowRateLimiter;
import com.rohit.razorpay.common.ratelimit.RateLimitResult;
import com.rohit.razorpay.merchant.cache.ApiKeyCache;
import com.rohit.razorpay.merchant.cache.ApiKeyCacheEntry;
import com.rohit.razorpay.merchant.entity.ApiKeyEntity;
import com.rohit.razorpay.merchant.repository.ApiKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
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
    private final ApiKeyCache apiKeyCache;
    private final FixedWindowRateLimiter rateLimiter;

    @Value("${app.rate-limit.use-case.api-key.requests-per-minute}")
    private Integer requestsPerMinute;

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


            ApiKeyCacheEntry apiKey = apiKeyCache.get(keyId)
                    .orElseGet(()->loadAndCache(keyId));

            log.info("Api key = {}", apiKey.keyId());

            //check if the api key is disabled or is not valid
            if(apiKey == null || !apiKey.enabled() || !isValidSecret(apiKey,keySecret)){
                throw new BadRequestException("Disabled or invalid/expired api key");
            }

            RateLimitResult rateLimitResult = rateLimiter.check(
                    "apikey:"+keyId
                    ,requestsPerMinute
                    ,60
            );


            if(!rateLimitResult.isAllowed()){
                throw new RateLimitException(
                        "Too many requests"
                        ,rateLimitResult.retryAfterSeconds()
                );
            }

            var auth = new UsernamePasswordAuthenticationToken(keyId,
                    null,
                    List.of(new SimpleGrantedAuthority("API_KEY_ROLE"))
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
            merchantContext.setMerchantId(apiKey.merchantId());
            merchantContext.setKeyId(apiKey.keyId());

            log.info("Merchant context id {}",merchantContext.getMerchantId());
            filterChain.doFilter(request,response);
        }catch (Exception e){
            log.info("Exception in api key filter chain, {}",e.toString());
            handlerExceptionResolver.resolveException(request,response,null,e);
        }
    }

    private ApiKeyCacheEntry loadAndCache(String keyId){
        ApiKeyEntity apiKey = apiKeyRepository.findByKeyId(keyId).orElse(null);
        if(apiKey == null) return null;
        ApiKeyCacheEntry apiKeyCacheEntry = new ApiKeyCacheEntry(
                keyId,
                apiKey.getKeySecretHash(),
                apiKey.getPrevKeySecretHash(),
                apiKey.getGracePeriodExpiresAt(),
                apiKey.getMerchant().getId(),
                apiKey.getEnvironment(),
                apiKey.getEnabled()
        );
        apiKeyCache.put(keyId,apiKeyCacheEntry);
        return apiKeyCacheEntry;
    }

    private boolean isValidSecret(ApiKeyCacheEntry apiKey, String rawSecret){
        boolean matches = BCRYPT.matches(rawSecret,apiKey.keySecretHash());
        if(matches) return true;
        return apiKey.gracePeriodExpiresAt() != null
                && apiKey.previousKeySecretHash()!= null
                && LocalDateTime.now().isBefore(apiKey.gracePeriodExpiresAt());
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
