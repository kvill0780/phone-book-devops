package bf.kvill.spring_phone_book.controller;

import bf.kvill.spring_phone_book.dto.ContactRequest;
import bf.kvill.spring_phone_book.exception.ContactNotFoundException;
import bf.kvill.spring_phone_book.model.Contact;
import bf.kvill.spring_phone_book.model.User;
import bf.kvill.spring_phone_book.service.ContactService;
import bf.kvill.spring_phone_book.service.MetricsService;
import bf.kvill.spring_phone_book.service.UserService;
import io.micrometer.core.instrument.Timer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
@Validated
public class ContactController {
    
    private final ContactService contactService;
    private final UserService userService;
    private final MetricsService metricsService;

    private User getCurrentUser(Authentication auth) {
        return userService.findByUsername(auth.getName());
    }

    @PostMapping
    public ResponseEntity<Contact> createContact(@Valid @RequestBody ContactRequest contactRequest, Authentication auth) {
        Timer.Sample sample = metricsService.startContactOperationTimer();
        try {
            User user = getCurrentUser(auth);
            Contact contact = contactService.createContactFromRequest(contactRequest, user);
            metricsService.incrementContactsCreated();
            return ResponseEntity.status(HttpStatus.CREATED).body(contact);
        } finally {
            metricsService.stopContactOperationTimer(sample);
        }
    }

    @GetMapping
    public List<Contact> getAllContacts(Authentication auth) {
        User user = getCurrentUser(auth);
        return contactService.getAllContactsForUser(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContact(@PathVariable Long id, Authentication auth) {
        User user = getCurrentUser(auth);
        return ResponseEntity.ok(contactService.getContactById(id, user));
    }

    @GetMapping("/search/phone")
    public ResponseEntity<List<Contact>> searchByPhoneNumber(
            @RequestParam @NotBlank @Pattern(regexp = "^[0-9+\\-\\s()]+$", message = "Format de téléphone invalide") String phoneNumber, 
            Authentication auth) {
        User user = getCurrentUser(auth);
        List<Contact> results = contactService.searchByPhoneNumber(phoneNumber, user);
        if (results.isEmpty()) {
            throw new ContactNotFoundException("Aucun contact trouvé");
        }
        return ResponseEntity.ok(results);
    }

    @GetMapping("/search/firstname")
    public ResponseEntity<List<Contact>> searchByFirstName(
            @RequestParam @NotBlank @Size(min = 2, max = 50) @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s-]+$", message = "Le prénom ne peut contenir que des lettres") String firstName, 
            Authentication auth) {
        User user = getCurrentUser(auth);
        List<Contact> results = contactService.searchByFirstName(firstName, user);
        if (results.isEmpty()) {
            throw new ContactNotFoundException("Aucun contact trouvé");
        }
        return ResponseEntity.ok(results);
    }

    @GetMapping("/search/lastname")
    public ResponseEntity<List<Contact>> searchByLastName(
            @RequestParam @NotBlank @Size(min = 2, max = 50) @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s-]+$", message = "Le nom ne peut contenir que des lettres") String lastName, 
            Authentication auth) {
        User user = getCurrentUser(auth);
        List<Contact> results = contactService.searchByLastName(lastName, user);
        if (results.isEmpty()) {
            throw new ContactNotFoundException("Aucun contact trouvé");
        }
        return ResponseEntity.ok(results);
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Contact>> getContactsByGroup(@PathVariable Long groupId, Authentication auth) {
        User user = getCurrentUser(auth);
        return ResponseEntity.ok(contactService.getContactsByGroup(groupId, user));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Contact>> searchContacts(
            @RequestParam @NotBlank @Size(min = 2, max = 50) String query, 
            Authentication auth) {
        metricsService.incrementSearchQueries();
        User user = getCurrentUser(auth);
        List<Contact> results = contactService.searchContacts(query, user);
        if (results.isEmpty()) {
            throw new ContactNotFoundException("Aucun contact trouvé");
        }
        return ResponseEntity.ok(results);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contact> updateContact(@PathVariable Long id, @Valid @RequestBody ContactRequest contactRequest, Authentication auth) {
        Timer.Sample sample = metricsService.startContactOperationTimer();
        try {
            User user = getCurrentUser(auth);
            Contact contact = contactService.updateContactFromRequest(id, contactRequest, user);
            return ResponseEntity.ok(contact);
        } finally {
            metricsService.stopContactOperationTimer(sample);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id, Authentication auth) {
        User user = getCurrentUser(auth);
        contactService.deleteContact(id, user);
        metricsService.incrementContactsDeleted();
        return ResponseEntity.noContent().build();
    }
}
