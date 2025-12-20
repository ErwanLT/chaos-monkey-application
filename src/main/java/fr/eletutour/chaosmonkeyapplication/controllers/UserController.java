package fr.eletutour.chaosmonkeyapplication.controllers;

import fr.eletutour.chaosmonkeyapplication.models.User;
import fr.eletutour.chaosmonkeyapplication.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping("/{id}/subscription")
    public ResponseEntity<Map<String, Object>> getSubscriptionInfo(@PathVariable Long id) {
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
