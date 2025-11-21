package bf.kvill.spring_phone_book.controller;

import bf.kvill.spring_phone_book.dto.AuthRequest;
import bf.kvill.spring_phone_book.dto.AuthResponse;
import bf.kvill.spring_phone_book.model.User;
import bf.kvill.spring_phone_book.security.JwtUtil;
import bf.kvill.spring_phone_book.service.CustomUserDetailsService;
import bf.kvill.spring_phone_book.service.MetricsService;
import bf.kvill.spring_phone_book.service.UserService;
import io.micrometer.core.instrument.Timer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final MetricsService metricsService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        Timer.Sample sample = metricsService.startAuthTimer();
        metricsService.incrementLoginAttempts();
        
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            String token = jwtUtil.generateToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);
            
            AuthResponse response = new AuthResponse(
                token,
                refreshToken,
                request.getUsername(),
                86400000L
            );
            
            metricsService.incrementLoginSuccesses();
            log.info("Successful login for user: {}", request.getUsername());
            return ResponseEntity.ok(response);
            
        } catch (BadCredentialsException e) {
            metricsService.incrementLoginFailures();
            log.warn("Failed login attempt for user: {}", request.getUsername());
            return ResponseEntity.badRequest().body(Map.of("error", "Identifiants invalides"));
        } catch (Exception e) {
            metricsService.incrementLoginFailures();
            log.error("Login error for user: {}", request.getUsername(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "Erreur de connexion"));
        } finally {
            metricsService.stopAuthTimer(sample);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequest request) {
        try {
            if (userService.existsByUsername(request.getUsername())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Ce nom d'utilisateur existe déjà"));
            }
            
            User user = userService.createUser(request.getUsername(), request.getPassword());
            
            metricsService.incrementRegistrations();
            log.info("New user registered: {}", request.getUsername());
            return ResponseEntity.ok(Map.of(
                "message", "Utilisateur créé avec succès",
                "username", user.getUsername()
            ));
            
        } catch (Exception e) {
            log.error("Registration error for user: {}", request.getUsername(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "Erreur lors de la création du compte"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");
            
            if (refreshToken == null || !jwtUtil.isRefreshToken(refreshToken)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Token de rafraîchissement invalide"));
            }
            
            String username = jwtUtil.extractUsername(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            if (jwtUtil.validateToken(refreshToken, userDetails)) {
                String newToken = jwtUtil.generateToken(userDetails);
                String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);
                
                AuthResponse response = new AuthResponse(
                    newToken,
                    newRefreshToken,
                    username,
                    86400000L
                );
                
                return ResponseEntity.ok(response);
            }
            
            return ResponseEntity.badRequest().body(Map.of("error", "Token expiré"));
            
        } catch (Exception e) {
            log.error("Token refresh error", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Erreur de rafraîchissement"));
        }
    }
}