package fr.eletutour.chaosmonkeyapplication.controllers.api;

import fr.eletutour.chaosmonkeyapplication.models.User;
import fr.eletutour.chaosmonkeyapplication.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Utilisateurs", description = "Gestion des utilisateurs et abonnements")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Liste tous les utilisateurs", description = "Récupère la liste complète de tous les utilisateurs")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détails d'un utilisateur", description = "Récupère les détails d'un utilisateur par son identifiant")
    public ResponseEntity<User> getUserById(
            @Parameter(description = "Identifiant de l'utilisateur") @PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Recherche par email", description = "Récupère un utilisateur par son adresse email")
    public ResponseEntity<User> getUserByEmail(
            @Parameter(description = "Adresse email de l'utilisateur") @PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Créer un utilisateur", description = "Crée un nouvel utilisateur dans le système")
    public User createUser(
            @Parameter(description = "Données de l'utilisateur à créer") @RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping("/{id}/subscription")
    @Operation(summary = "Informations d'abonnement", description = "Récupère les informations d'abonnement d'un utilisateur (type, accès HD, accès Premium)")
    public ResponseEntity<Map<String, Object>> getSubscriptionInfo(
            @Parameter(description = "Identifiant de l'utilisateur") @PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> {
                    Map<String, Object> info = new HashMap<>();
                    info.put("userId", user.getId());
                    info.put("subscriptionType", user.getSubscriptionType());
                    info.put("canAccessHD", userService.canAccessHDContent(id));
                    info.put("canAccessPremium", userService.canAccessPremiumContent(id));
                    return ResponseEntity.ok(info);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
