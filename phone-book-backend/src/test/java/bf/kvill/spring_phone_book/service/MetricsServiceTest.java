package bf.kvill.spring_phone_book.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MetricsServiceTest {

    private MetricsService metricsService;
    private MeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        metricsService = new MetricsService(meterRegistry);
    }

    @Test
    void shouldIncrementLoginAttempts() {
        metricsService.incrementLoginAttempts();
        
        assertEquals(1.0, meterRegistry.counter("auth.login.attempts").count());
    }

    @Test
    void shouldIncrementLoginSuccesses() {
        metricsService.incrementLoginSuccesses();
        
        assertEquals(1.0, meterRegistry.counter("auth.login.success").count());
    }

    @Test
    void shouldIncrementLoginFailures() {
        metricsService.incrementLoginFailures();
        
        assertEquals(1.0, meterRegistry.counter("auth.login.failures").count());
    }

    @Test
    void shouldIncrementRegistrations() {
        metricsService.incrementRegistrations();
        
        assertEquals(1.0, meterRegistry.counter("auth.registrations").count());
    }

    @Test
    void shouldIncrementContactsCreated() {
        metricsService.incrementContactsCreated();
        
        assertEquals(1.0, meterRegistry.counter("contacts.created").count());
    }

    @Test
    void shouldIncrementSearchQueries() {
        metricsService.incrementSearchQueries();
        
        assertEquals(1.0, meterRegistry.counter("contacts.searches").count());
    }

    @Test
    void shouldMeasureAuthDuration() {
        var sample = metricsService.startAuthTimer();
        
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        metricsService.stopAuthTimer(sample);
        
        assertTrue(meterRegistry.timer("auth.duration").count() > 0);
        assertTrue(meterRegistry.timer("auth.duration").totalTime(java.util.concurrent.TimeUnit.NANOSECONDS) > 0);
    }
}