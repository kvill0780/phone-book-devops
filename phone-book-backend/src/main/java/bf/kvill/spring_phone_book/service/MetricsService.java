package bf.kvill.spring_phone_book.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MetricsService {

    private final MeterRegistry meterRegistry;

    // Compteurs de sécurité
    private Counter loginAttempts;
    private Counter loginSuccesses;
    private Counter loginFailures;
    private Counter registrations;

    // Compteurs métier
    private Counter contactsCreated;
    private Counter contactsDeleted;
    private Counter searchQueries;

    // Timers pour les performances
    private Timer authTimer;
    private Timer contactOperationTimer;

    @PostConstruct
    public void init() {
        loginAttempts = Counter.builder("auth.login.attempts")
                .description("Total login attempts")
                .register(meterRegistry);

        loginSuccesses = Counter.builder("auth.login.success")
                .description("Successful logins")
                .register(meterRegistry);

        loginFailures = Counter.builder("auth.login.failures")
                .description("Failed logins")
                .register(meterRegistry);

        registrations = Counter.builder("auth.registrations")
                .description("User registrations")
                .register(meterRegistry);

        contactsCreated = Counter.builder("contacts.created")
                .description("Contacts created")
                .register(meterRegistry);

        contactsDeleted = Counter.builder("contacts.deleted")
                .description("Contacts deleted")
                .register(meterRegistry);

        searchQueries = Counter.builder("contacts.searches")
                .description("Contact searches")
                .register(meterRegistry);

        authTimer = Timer.builder("auth.duration")
                .description("Authentication duration")
                .register(meterRegistry);

        contactOperationTimer = Timer.builder("contacts.operation.duration")
                .description("Contact operation duration")
                .register(meterRegistry);
    }

    // Méthodes pour incrémenter les compteurs
    public void incrementLoginAttempts() {
        loginAttempts.increment();
    }

    public void incrementLoginSuccesses() {
        loginSuccesses.increment();
    }

    public void incrementLoginFailures() {
        loginFailures.increment();
    }

    public void incrementRegistrations() {
        registrations.increment();
    }

    public void incrementContactsCreated() {
        contactsCreated.increment();
    }

    public void incrementContactsDeleted() {
        contactsDeleted.increment();
    }

    public void incrementSearchQueries() {
        searchQueries.increment();
    }

    // Méthodes pour les timers
    public Timer.Sample startAuthTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopAuthTimer(Timer.Sample sample) {
        sample.stop(authTimer);
    }

    public Timer.Sample startContactOperationTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopContactOperationTimer(Timer.Sample sample) {
        sample.stop(contactOperationTimer);
    }
}