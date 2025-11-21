package bf.kvill.spring_phone_book.integration;

import bf.kvill.spring_phone_book.service.CircuitBreakerService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CircuitBreakerIntegrationTest {

    private CircuitBreakerRegistry circuitBreakerRegistry;
    private CircuitBreaker circuitBreaker;

    @BeforeEach
    void setUp() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowSize(5)
                .minimumNumberOfCalls(3)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(5))
                .build();
        
        circuitBreakerRegistry = CircuitBreakerRegistry.of(config);
        circuitBreaker = circuitBreakerRegistry.circuitBreaker("contact-service");
    }

    @Test
    void shouldOpenCircuitBreakerAfterFailures() {
        // Given - A circuit breaker
        assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState());
        
        // When - Multiple failures occur
        for (int i = 0; i < 5; i++) {
            try {
                circuitBreaker.executeSupplier(() -> {
                    throw new RuntimeException("Service failure");
                });
            } catch (Exception e) {
                // Expected
            }
        }
        
        // Then - Circuit breaker should be open
        assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());
    }

    @Test
    void shouldReturnNormalResponseWhenServiceWorks() {
        // Given - A working service
        String expectedResult = "Success";
        
        // When - Call through circuit breaker
        String result = circuitBreaker.executeSupplier(() -> expectedResult);
        
        // Then - Should return normal response
        assertEquals(expectedResult, result);
        assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState());
    }

    @Test
    void shouldUseFallbackWhenCircuitIsOpen() {
        // Given - Circuit breaker is open
        for (int i = 0; i < 5; i++) {
            try {
                circuitBreaker.executeSupplier(() -> {
                    throw new RuntimeException("Service failure");
                });
            } catch (Exception e) {
                // Expected
            }
        }
        
        assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());
        
        // When - Try to call service
        String fallbackResult = null;
        try {
            circuitBreaker.executeSupplier(() -> "Normal response");
        } catch (Exception e) {
            fallbackResult = "Fallback response";
        }
        
        // Then - Should use fallback
        assertNotNull(fallbackResult);
        assertEquals("Fallback response", fallbackResult);
    }
}