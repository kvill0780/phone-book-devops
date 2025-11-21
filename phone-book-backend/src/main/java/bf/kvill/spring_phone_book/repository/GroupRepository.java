package bf.kvill.spring_phone_book.repository;

import bf.kvill.spring_phone_book.model.Group;
import bf.kvill.spring_phone_book.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByName(String name);
    List<Group> findByUser(User user);
    Optional<Group> findByIdAndUser(Long id, User user);
}
