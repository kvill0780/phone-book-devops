package bf.kvill.spring_phone_book.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "myVerySecureSecretKeyThatIsAtLeast256BitsLongForHS256Algorithm");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
        ReflectionTestUtils.setField(jwtUtil, "refreshExpiration", 604800000L);
        
        userDetails = new User("testuser", "password", new ArrayList<>());
    }

    @Test
    void shouldGenerateToken() {
        String token = jwtUtil.generateToken(userDetails);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void shouldExtractUsername() {
        String token = jwtUtil.generateToken(userDetails);
        String username = jwtUtil.extractUsername(token);
        
        assertEquals("testuser", username);
    }

    @Test
    void shouldValidateToken() {
        String token = jwtUtil.generateToken(userDetails);
        
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void shouldGenerateRefreshToken() {
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        
        assertNotNull(refreshToken);
        assertTrue(jwtUtil.isRefreshToken(refreshToken));
    }

    @Test
    void shouldNotValidateExpiredToken() {
        String token = jwtUtil.generateToken(userDetails);
        assertFalse(jwtUtil.isTokenExpired(token));
    }
}