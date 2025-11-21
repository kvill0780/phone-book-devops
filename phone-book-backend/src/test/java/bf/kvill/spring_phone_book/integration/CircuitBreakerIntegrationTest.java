package bf.kvill.spring_phone_book.integration;

import bf.kvill.spring_phone_book.service.CircuitBreakerService;
import bf.kvill.spring_phone_book.service.UserService;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CircuitBreakerIntegrationTest {

    @Autowired
    private CircuitBreakerService circuitBreakerService;

    @Autowired
    private UserService userService;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Test
    void shouldReturnFallbackWhenServiceFails() throws ExecutionException, InterruptedException {
        // Given - Invalid user ID that will cause service to fail
        Long invalidUserId = 999999L;
        
        // When - Call service multiple times to trigger circuit breaker
        CompletableFuture<Object> result = null;
        for (int i = 0; i < 5; i++) {
            try {
                result = circuitBreakerService.getContactsWithCircuitBreaker(invalidUserId);
                result.get(); // This will throw exception
            } catch (Exception e) {
                // Expected to fail
            }
        }
        
        // Then - Circuit breaker should be open and return fallback
        var circuitBreaker = circuitBreakerRegistry.circuitBreaker("contact-service");
        
        // Make one more call that should use fallback
        result = circuitBreakerService.getContactsWithCircuitBreaker(invalidUserId);
        Object fallbackResult = result.get();
        
        // Verify fallback response
        assertNotNull(fallbackResult);
        assertTrue(fallbackResult instanceof CircuitBreakerService.ServiceUnavailableResponse);
        
        CircuitBreakerService.ServiceUnavailableResponse response = 
            (CircuitBreakerService.ServiceUnavailableResponse) fallbackResult;
        assertEquals("Le service de contacts est temporairement indisponible", response.getMessage());
    }

    @Test
    void shouldReturnNormalResponseWhenServiceWorks() throws ExecutionException, InterruptedException {
        // Given - Valid user
        var user = userService.createUser("testuser", "password");
        
        // When - Call service
        CompletableFuture<Object> result = circuitBreakerService.getContactsWithCircuitBreaker(user.getId());
        Object response = result.get();
        
        // Then - Should return normal response (list of contacts)
        assertNotNull(response);
        assertFalse(response instanceof CircuitBreakerService.ServiceUnavailableResponse);
    }

    @Test
    void shouldReturnFallbackForSearchWhenServiceFails() {
        // Given - Invalid user ID
        Long invalidUserId = 999999L;
        String query = "test";
        
        // When - Call search multiple times to trigger circuit breaker
        Object result = null;
        for (int i = 0; i < 5; i++) {
            try {
                result = circuitBreakerService.searchWithCircuitBreaker(query, invalidUserId);
            } catch (Exception e) {
                // Expected to fail
            }
        }
        
        // Then - Should eventually return fallback
        result = circuitBreakerService.searchWithCircuitBreaker(query, invalidUserId);
        
        assertNotNull(result);
        assertTrue(result instanceof CircuitBreakerService.ServiceUnavailableResponse);
        
        CircuitBreakerService.ServiceUnavailableResponse response = 
            (CircuitBreakerService.ServiceUnavailableResponse) result;
        assertEquals("Le service de recherche est temporairement indisponible", response.getMessage());
    }
}