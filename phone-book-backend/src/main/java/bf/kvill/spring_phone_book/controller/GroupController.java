package bf.kvill.spring_phone_book.controller;

import bf.kvill.spring_phone_book.model.Group;
import bf.kvill.spring_phone_book.model.User;
import bf.kvill.spring_phone_book.service.GroupService;
import bf.kvill.spring_phone_book.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final UserService userService;

    private User getCurrentUser(Authentication auth) {
        return userService.findByUsername(auth.getName());
    }

    @PostMapping
    public ResponseEntity<Group> createGroup(@Valid @RequestBody Group group, Authentication auth) {
        User user = getCurrentUser(auth);
        return ResponseEntity.status(HttpStatus.CREATED).body(groupService.createGroup(group, user));
    }

    @GetMapping
    public List<Group> getAllGroups(Authentication auth) {
        User user = getCurrentUser(auth);
        return groupService.getAllGroupsForUser(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Group> getGroup(@PathVariable Long id, Authentication auth) {
        User user = getCurrentUser(auth);
        return ResponseEntity.ok(groupService.getGroupById(id, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Group> updateGroup(@PathVariable Long id, @Valid @RequestBody Group group, Authentication auth) {
        User user = getCurrentUser(auth);
        return ResponseEntity.ok(groupService.updateGroup(id, group, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id, Authentication auth) {
        User user = getCurrentUser(auth);
        groupService.deleteGroup(id, user);
        return ResponseEntity.noContent().build();
    }
}
