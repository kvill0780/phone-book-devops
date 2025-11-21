package bf.kvill.spring_phone_book.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
@Slf4j
public class SecurityAuditAspect {

    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY");

    @Before("execution(* bf.kvill.spring_phone_book.controller.AuthController.*(..))")
    public void logAuthAttempt(JoinPoint joinPoint) {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            String clientIp = getClientIp(request);
            String userAgent = request.getHeader("User-Agent");
            
            securityLogger.info("AUTH_ATTEMPT: method={}, ip={}, userAgent={}", 
                joinPoint.getSignature().getName(), clientIp, userAgent);
        }
    }

    @AfterReturning(pointcut = "execution(* bf.kvill.spring_phone_book.controller.AuthController.login(..))", returning = "result")
    public void logSuccessfulLogin(JoinPoint joinPoint, Object result) {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            String clientIp = getClientIp(request);
            securityLogger.info("LOGIN_SUCCESS: ip={}", clientIp);
        }
    }

    @AfterThrowing(pointcut = "execution(* bf.kvill.spring_phone_book.controller.AuthController.login(..))", throwing = "ex")
    public void logFailedLogin(JoinPoint joinPoint, Throwable ex) {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            String clientIp = getClientIp(request);
            securityLogger.warn("LOGIN_FAILED: ip={}, reason={}", clientIp, ex.getMessage());
        }
    }

    @AfterReturning(pointcut = "execution(* bf.kvill.spring_phone_book.controller.AuthController.register(..))")
    public void logUserRegistration(JoinPoint joinPoint) {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            String clientIp = getClientIp(request);
            securityLogger.info("USER_REGISTERED: ip={}", clientIp);
        }
    }

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
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