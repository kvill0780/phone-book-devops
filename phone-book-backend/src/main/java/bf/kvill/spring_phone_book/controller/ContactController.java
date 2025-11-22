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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
@Validated
@Tag(name = "Contacts", description = "Gestion des contacts du répertoire téléphonique")
@SecurityRequirement(name = "bearerAuth")
public class ContactController {
    
    private final ContactService contactService;
    private final UserService userService;
    private final MetricsService metricsService;

    private User getCurrentUser(Authentication auth) {
        return userService.findByUsername(auth.getName());
    }

    @Operation(summary = "Créer un contact", description = "Ajoute un nouveau contact au répertoire de l'utilisateur")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Contact créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
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

    @Operation(summary = "Lister tous les contacts", description = "Récupère tous les contacts de l'utilisateur connecté")
    @ApiResponse(responseCode = "200", description = "Liste des contacts retournée")
    @GetMapping
    public List<Contact> getAllContacts(Authentication auth) {
        User user = getCurrentUser(auth);
        return contactService.getAllContactsForUser(user);
    }

    @Operation(summary = "Obtenir un contact", description = "Récupère les détails d'un contact par son ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contact trouvé"),
        @ApiResponse(responseCode = "404", description = "Contact non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContact(@PathVariable Long id, Authentication auth) {
        User user = getCurrentUser(auth);
        return ResponseEntity.ok(contactService.getContactById(id, user));
    }

    @Operation(summary = "Rechercher par numéro", description = "Recherche des contacts par numéro de téléphone")
    @ApiResponse(responseCode = "200", description = "Contacts trouvés")
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

    @Operation(summary = "Rechercher par prénom", description = "Recherche des contacts par prénom")
    @ApiResponse(responseCode = "200", description = "Contacts trouvés")
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

    @Operation(summary = "Rechercher par nom", description = "Recherche des contacts par nom de famille")
    @ApiResponse(responseCode = "200", description = "Contacts trouvés")
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

    @Operation(summary = "Contacts par groupe", description = "Récupère tous les contacts d'un groupe spécifique")
    @ApiResponse(responseCode = "200", description = "Contacts du groupe retournés")
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Contact>> getContactsByGroup(@PathVariable Long groupId, Authentication auth) {
        User user = getCurrentUser(auth);
        return ResponseEntity.ok(contactService.getContactsByGroup(groupId, user));
    }

    @Operation(summary = "Recherche globale", description = "Recherche des contacts par nom, prénom ou numéro")
    @ApiResponse(responseCode = "200", description = "Résultats de recherche retournés")
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

    @Operation(summary = "Modifier un contact", description = "Met à jour les informations d'un contact existant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contact modifié avec succès"),
        @ApiResponse(responseCode = "404", description = "Contact non trouvé")
    })
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

    @Operation(summary = "Supprimer un contact", description = "Supprime définitivement un contact du répertoire")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Contact supprimé avec succès"),
        @ApiResponse(responseCode = "404", description = "Contact non trouvé")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id, Authentication auth) {
        User user = getCurrentUser(auth);
        contactService.deleteContact(id, user);
        metricsService.incrementContactsDeleted();
        return ResponseEntity.noContent().build();
    }
}
