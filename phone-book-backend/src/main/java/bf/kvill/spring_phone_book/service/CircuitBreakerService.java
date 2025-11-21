package bf.kvill.spring_phone_book.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CircuitBreakerService {

    private final ContactService contactService;
    private final UserService userService;

    @CircuitBreaker(name = "contact-service", fallbackMethod = "fallbackGetContacts")
    @Retry(name = "contact-service")
    @TimeLimiter(name = "contact-service")
    public CompletableFuture<Object> getContactsWithCircuitBreaker(Long userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                var user = userService.findById(userId);
                return contactService.getAllContactsForUser(user);
            } catch (Exception e) {
                log.error("Error getting contacts for user {}", userId, e);
                throw new RuntimeException("Service temporarily unavailable", e);
            }
        });
    }

    @CircuitBreaker(name = "search-service", fallbackMethod = "fallbackSearch")
    @Retry(name = "search-service")
    public Object searchWithCircuitBreaker(String query, Long userId) {
        try {
            var user = userService.findById(userId);
            return contactService.searchContacts(query, user);
        } catch (Exception e) {
            log.error("Error searching contacts for user {} with query {}", userId, query, e);
            throw new RuntimeException("Search service temporarily unavailable", e);
        }
    }

    // Fallback methods
    public CompletableFuture<Object> fallbackGetContacts(Long userId, Exception ex) {
        log.warn("Circuit breaker activated for contacts service, user: {}", userId);
        return CompletableFuture.completedFuture(
            new ServiceUnavailableResponse("Le service de contacts est temporairement indisponible")
        );
    }

    public Object fallbackSearch(String query, Long userId, Exception ex) {
        log.warn("Circuit breaker activated for search service, user: {}, query: {}", userId, query);
        return new ServiceUnavailableResponse("Le service de recherche est temporairement indisponible");
    }

    public static class ServiceUnavailableResponse {
        private final String message;
        private final long timestamp;

        public ServiceUnavailableResponse(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }
    }
}