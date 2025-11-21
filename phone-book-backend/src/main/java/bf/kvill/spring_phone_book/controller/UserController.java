package bf.kvill.spring_phone_book.controller;

import bf.kvill.spring_phone_book.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Endpoints utilisateur si nécessaire
}
