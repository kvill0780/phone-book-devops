package bf.kvill.spring_phone_book.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class StartupValidator implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(StartupValidator.class);

    private final Environment env;

    @Value("${jwt.secret:}")
    private String jwtSecret;

    public StartupValidator(Environment env) {
        this.env = env;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String[] active = env.getActiveProfiles();
        boolean prod = Arrays.stream(active).anyMatch(p -> p.equalsIgnoreCase("prod") || p.equalsIgnoreCase("production"));

        if (prod) {
            if (jwtSecret == null || jwtSecret.length() < 32) {
                throw new IllegalStateException("JWT secret is missing or too weak for production. Set 'jwt.secret' via environment or secrets management.");
            }
            log.info("Startup validation passed for production profile.");
        } else {
            if (jwtSecret == null || jwtSecret.isEmpty()) {
                log.warn("No JWT secret configured. Using insecure defaults for development. Set 'jwt.secret' for better security.");
            }
        }
    }
}
