package bf.kvill.spring_phone_book.config;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class LoggingConfig {

    @PostConstruct
    public void configureLogging() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        
        // Security Logger
        configureSecurityLogger(context);
        
        // Business Logger
        configureBusinessLogger(context);
        
        // Error Logger
        configureErrorLogger(context);
    }

    private void configureSecurityLogger(LoggerContext context) {
        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        appender.setContext(context);
        appender.setName("SECURITY");
        appender.setFile("logs/security.log");

        TimeBasedRollingPolicy<ILoggingEvent> policy = new TimeBasedRollingPolicy<>();
        policy.setContext(context);
        policy.setParent(appender);
        policy.setFileNamePattern("logs/security.%d{yyyy-MM-dd}.%i.gz");
        policy.setMaxHistory(30);
        policy.start();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n");
        encoder.start();

        appender.setRollingPolicy(policy);
        appender.setEncoder(encoder);
        appender.start();

        Logger securityLogger = context.getLogger("SECURITY");
        securityLogger.addAppender(appender);
        securityLogger.setAdditive(false);
    }

    private void configureBusinessLogger(LoggerContext context) {
        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        appender.setContext(context);
        appender.setName("BUSINESS");
        appender.setFile("logs/business.log");

        TimeBasedRollingPolicy<ILoggingEvent> policy = new TimeBasedRollingPolicy<>();
        policy.setContext(context);
        policy.setParent(appender);
        policy.setFileNamePattern("logs/business.%d{yyyy-MM-dd}.%i.gz");
        policy.setMaxHistory(90);
        policy.start();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n");
        encoder.start();

        appender.setRollingPolicy(policy);
        appender.setEncoder(encoder);
        appender.start();

        Logger businessLogger = context.getLogger("BUSINESS");
        businessLogger.addAppender(appender);
        businessLogger.setAdditive(false);
    }

    private void configureErrorLogger(LoggerContext context) {
        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        appender.setContext(context);
        appender.setName("ERROR");
        appender.setFile("logs/error.log");

        TimeBasedRollingPolicy<ILoggingEvent> policy = new TimeBasedRollingPolicy<>();
        policy.setContext(context);
        policy.setParent(appender);
        policy.setFileNamePattern("logs/error.%d{yyyy-MM-dd}.%i.gz");
        policy.setMaxHistory(365);
        policy.start();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n");
        encoder.start();

        appender.setRollingPolicy(policy);
        appender.setEncoder(encoder);
        appender.start();

        Logger errorLogger = context.getLogger("ERROR");
        errorLogger.addAppender(appender);
        errorLogger.setAdditive(false);
    }
}