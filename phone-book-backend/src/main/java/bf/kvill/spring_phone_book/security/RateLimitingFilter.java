package bf.kvill.spring_phone_book.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String clientIp = getClientIp(request);
        String endpoint = request.getRequestURI();

        // Rate limiting plus strict pour les endpoints d'authentification
        Bucket bucket = getBucket(clientIp, endpoint);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.getWriter().write("{\"error\":\"Too many requests\"}");
        }
    }

    private Bucket getBucket(String clientIp, String endpoint) {
        String key = clientIp + ":" + endpoint;
        
        return buckets.computeIfAbsent(key, k -> {
            Bandwidth limit;
            
            if (endpoint.contains("/auth/")) {
                // 5 tentatives par minute pour l'authentification
                limit = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1)));
            } else {
                // 100 requêtes par minute pour les autres endpoints
                limit = Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
            }
            
            return Bucket.builder()
                    .addLimit(limit)
                    .build();
        });
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}