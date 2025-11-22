package bf.kvill.spring_phone_book.service;

import bf.kvill.spring_phone_book.dto.ContactRequest;
import bf.kvill.spring_phone_book.exception.ContactNotFoundException;
import bf.kvill.spring_phone_book.model.Contact;
import bf.kvill.spring_phone_book.model.Group;
import bf.kvill.spring_phone_book.model.User;
import bf.kvill.spring_phone_book.repository.ContactRepository;
import bf.kvill.spring_phone_book.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public Contact createContact(Contact contact, User user) {
        contact.setUser(user);
        return contactRepository.save(contact);
    }

    // @Cacheable(value = "contacts", key = "'user:' + #user.id") // Désactivé temporairement - problème sérialisation Redis
    public List<Contact> getAllContactsForUser(User user) {
        return contactRepository.findAll().stream()
                .filter(c -> c.getUser().getId().equals(user.getId()))
                .toList();
    }

    public Contact getContactById(Long id, User user) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ContactNotFoundException("Contact not found"));
        if (!contact.getUser().getId().equals(user.getId())) {
            throw new ContactNotFoundException("Contact not found");
        }
        return contact;
    }

    @Transactional
    public Contact updateContact(Long id, Contact updatedContact, User user) {
        Contact contact = getContactById(id, user);
        contact.setFirstName(updatedContact.getFirstName());
        contact.setLastName(updatedContact.getLastName());
        contact.setPhoneNumber(updatedContact.getPhoneNumber());
        if (updatedContact.getGroup() != null) {
            contact.setGroup(updatedContact.getGroup());
        }
        return contactRepository.save(contact);
    }

    @Transactional
    @CacheEvict(value = "contacts", key = "'user:' + #user.id")
    public void deleteContact(Long id, User user) {
        Contact contact = getContactById(id, user);
        contactRepository.delete(contact);
    }

    public List<Contact> searchByPhoneNumber(String phoneNumber, User user) {
        return contactRepository.findByPhoneNumberContaining(phoneNumber).stream()
                .filter(c -> c.getUser().getId().equals(user.getId()))
                .toList();
    }

    public List<Contact> searchByFirstName(String firstName, User user) {
        return contactRepository.findByFirstNameContainingIgnoreCase(firstName).stream()
                .filter(c -> c.getUser().getId().equals(user.getId()))
                .toList();
    }

    public List<Contact> searchByLastName(String lastName, User user) {
        return contactRepository.findByLastNameContainingIgnoreCase(lastName).stream()
                .filter(c -> c.getUser().getId().equals(user.getId()))
                .toList();
    }

    public List<Contact> getContactsByGroup(Long groupId, User user) {
        return contactRepository.findByGroupId(groupId).stream()
                .filter(c -> c.getUser().getId().equals(user.getId()))
                .toList();
    }

    @Cacheable(value = "searches", key = "'user:' + #user.id + ':query:' + #query")
    public List<Contact> searchContacts(String query, User user) {
        return contactRepository.findAll().stream()
                .filter(c -> c.getUser().getId().equals(user.getId()))
                .filter(c -> c.getFirstName().toLowerCase().contains(query.toLowerCase()) ||
                           c.getLastName().toLowerCase().contains(query.toLowerCase()) ||
                           c.getPhoneNumber().contains(query))
                .toList();
    }

    @Transactional
    @CacheEvict(value = "contacts", key = "'user:' + #user.id")
    public Contact createContactFromRequest(ContactRequest request, User user) {
        Contact contact = new Contact();
        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setPhoneNumber(request.getPhoneNumber());
        contact.setUser(user);
        
        if (request.getGroupId() != null) {
            Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("Groupe non trouvé"));
            contact.setGroup(group);
        }
        
        return contactRepository.save(contact);
    }

    @Transactional
    @CacheEvict(value = "contacts", key = "'user:' + #user.id")
    public Contact updateContactFromRequest(Long id, ContactRequest request, User user) {
        Contact contact = getContactById(id, user);
        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setPhoneNumber(request.getPhoneNumber());
        
        if (request.getGroupId() != null) {
            Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("Groupe non trouvé"));
            contact.setGroup(group);
        } else {
            contact.setGroup(null);
        }
        
        return contactRepository.save(contact);
    }
}
