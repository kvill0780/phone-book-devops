package bf.kvill.spring_phone_book.repository;

import bf.kvill.spring_phone_book.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}