package bf.kvill.spring_phone_book.service;

import bf.kvill.spring_phone_book.model.Group;
import bf.kvill.spring_phone_book.model.User;
import bf.kvill.spring_phone_book.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;

    @Transactional
    public Group createGroup(Group group, User user) {
        group.setUser(user);
        return groupRepository.save(group);
    }

    public List<Group> getAllGroupsForUser(User user) {
        return groupRepository.findByUser(user);
    }

    public Group getGroupById(Long id, User user) {
        return groupRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Group not found"));
    }

    @Transactional
    public Group updateGroup(Long id, Group updatedGroup, User user) {
        Group group = getGroupById(id, user);
        group.setName(updatedGroup.getName());
        group.setDescription(updatedGroup.getDescription());
        return groupRepository.save(group);
    }

    @Transactional
    public void deleteGroup(Long id, User user) {
        Group group = getGroupById(id, user);
        groupRepository.delete(group);
    }
}
