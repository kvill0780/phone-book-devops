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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@Tag(name = "Groupes", description = "Gestion des groupes de contacts")
@SecurityRequirement(name = "bearerAuth")
public class GroupController {

    private final GroupService groupService;
    private final UserService userService;

    private User getCurrentUser(Authentication auth) {
        return userService.findByUsername(auth.getName());
    }

    @Operation(summary = "Créer un groupe", description = "Crée un nouveau groupe pour organiser les contacts")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Groupe créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    public ResponseEntity<Group> createGroup(@Valid @RequestBody Group group, Authentication auth) {
        User user = getCurrentUser(auth);
        return ResponseEntity.status(HttpStatus.CREATED).body(groupService.createGroup(group, user));
    }

    @Operation(summary = "Lister tous les groupes", description = "Récupère tous les groupes de l'utilisateur")
    @ApiResponse(responseCode = "200", description = "Liste des groupes retournée")
    @GetMapping
    public List<Group> getAllGroups(Authentication auth) {
        User user = getCurrentUser(auth);
        return groupService.getAllGroupsForUser(user);
    }

    @Operation(summary = "Obtenir un groupe", description = "Récupère les détails d'un groupe par son ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Groupe trouvé"),
        @ApiResponse(responseCode = "404", description = "Groupe non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Group> getGroup(@PathVariable Long id, Authentication auth) {
        User user = getCurrentUser(auth);
        return ResponseEntity.ok(groupService.getGroupById(id, user));
    }

    @Operation(summary = "Modifier un groupe", description = "Met à jour les informations d'un groupe existant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Groupe modifié avec succès"),
        @ApiResponse(responseCode = "404", description = "Groupe non trouvé")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Group> updateGroup(@PathVariable Long id, @Valid @RequestBody Group group, Authentication auth) {
        User user = getCurrentUser(auth);
        return ResponseEntity.ok(groupService.updateGroup(id, group, user));
    }

    @Operation(summary = "Supprimer un groupe", description = "Supprime un groupe (les contacts ne sont pas supprimés)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Groupe supprimé avec succès"),
        @ApiResponse(responseCode = "404", description = "Groupe non trouvé")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id, Authentication auth) {
        User user = getCurrentUser(auth);
        groupService.deleteGroup(id, user);
        return ResponseEntity.noContent().build();
    }
}
