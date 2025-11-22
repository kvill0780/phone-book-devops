package bf.kvill.spring_phone_book.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class SecurityHeadersConfig {

    @Bean
    public OncePerRequestFilter securityHeadersFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                
                String path = request.getRequestURI();
                
                // Skip strict CSP for Swagger UI
                if (path.startsWith("/swagger-ui") || path.startsWith("/api-docs") || 
                    path.startsWith("/v3/api-docs") || path.equals("/swagger-ui.html")) {
                    // Relaxed CSP for Swagger
                    response.setHeader("Content-Security-Policy", 
                        "default-src 'self'; " +
                        "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                        "style-src 'self' 'unsafe-inline'; " +
                        "img-src 'self' data: https:; " +
                        "connect-src 'self'; " +
                        "font-src 'self' data:;"
                    );
                    filterChain.doFilter(request, response);
                    return;
                }
                
                // Security Headers
                response.setHeader("X-Content-Type-Options", "nosniff");
                response.setHeader("X-Frame-Options", "DENY");
                response.setHeader("X-XSS-Protection", "1; mode=block");
                response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
                response.setHeader("Permissions-Policy", "geolocation=(), microphone=(), camera=()");
                
                // HSTS (uniquement en HTTPS)
                if (request.isSecure()) {
                    response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
                }
                
                // CSP
                response.setHeader("Content-Security-Policy", 
                    "default-src 'self'; " +
                    "script-src 'self' 'unsafe-inline'; " +
                    "style-src 'self' 'unsafe-inline'; " +
                    "img-src 'self' data:; " +
                    "connect-src 'self'; " +
                    "font-src 'self'; " +
                    "object-src 'none'; " +
                    "media-src 'self'; " +
                    "frame-src 'none';"
                );
                
                filterChain.doFilter(request, response);
            }
        };
    }
}