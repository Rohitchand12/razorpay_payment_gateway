package com.rohit.razorpay.common.Idempotency;

import com.rohit.razorpay.common.exceptions.IdempotencyConflictException;
import com.rohit.razorpay.merchant.security.MerchantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.springframework.boot.logging.DeferredLog.replay;

@Component
@Slf4j
@RequiredArgsConstructor
public class IdempotencyFilter extends OncePerRequestFilter {

    private static final Set<String> GUARDED_METHODS = Set.of("POST","PUT","PATCH");
    private static final Duration lockTtl = Duration.ofSeconds(30);
    private static final Duration storeTtl = Duration.ofHours(24);
    private static final String SEPARATOR = "|";

    private final IdempotencyStore idempotencyStore;
    private final MerchantContext merchantContext;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(!GUARDED_METHODS.contains(request.getMethod())){
            filterChain.doFilter(request,response);
            return;
        }
        String rawKey = request.getHeader("X-Idempotency-Key");
        if(rawKey == null || rawKey.isBlank()){
            filterChain.doFilter(request,response);
            return;
        }

        UUID merchantId = merchantContext.getMerchantId();

        String idemKey = merchantId != null ? merchantId+rawKey : rawKey;

        //returns true if it sets -->means it is first thread that came
        //return false if it doesn't set, means it's already in progress for another thread
        boolean claimed = idempotencyStore.setIfAbsent(idemKey,lockTtl);

        if(!claimed){
            //means the key is already in use by another thread
            Optional<String> existing = idempotencyStore.get(idemKey);
            if(existing.isPresent() && !IdempotencyStore.IN_PROGRESS.equals(existing.get())){
                //means the key is not locked by some other thread and hence a response is present
                //return the same response using replay
                replay(request,response,existing.get());
            }
            else{
                //means the key is  locked by some other thread
                var ex = new IdempotencyConflictException("The key " + idemKey + " for request is currently in progress.");
                handlerExceptionResolver.resolveException(request,response,null,ex);
            }
            return;
        }
         //else this is the first thread entering allow the request to pass
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        try{
            filterChain.doFilter(request,responseWrapper);
        }finally {
            //cache the response before sending to the user.
            int status = responseWrapper.getStatus();
            byte[] bodyBytes = responseWrapper.getContentAsByteArray();
            String body = new String(bodyBytes, StandardCharsets.UTF_8);

            if(status < 400 && bodyBytes.length > 0){
                String stored = status + SEPARATOR + body;
                idempotencyStore.store(idemKey,stored,storeTtl);
                log.debug("IDEMPOTENCY FILTER: Idempotency key {} response = {}, status = {} stored",idemKey, body, status);
            }else{
                idempotencyStore.delete(idemKey);
                log.debug("IDEMPOTENCY FILTER: Idempotency key {} status = {} deleted",idemKey,status);
            }
            //flush the wrapper body to actual response
            //otherwise client received empty body response
            responseWrapper.copyBodyToResponse();
        }
    }

    private void replay(HttpServletRequest request, HttpServletResponse response, String s) throws IOException {
        int separatorIndex = s.indexOf(SEPARATOR);
        if(separatorIndex < 0){
            var ex = new IdempotencyConflictException("Error while replaying the response");
            handlerExceptionResolver.resolveException(request,response,null,ex);
        }

        int status = Integer.parseInt(s.substring(0,separatorIndex));
        String body = s.substring(separatorIndex+1);

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
    }
}
