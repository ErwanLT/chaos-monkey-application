package fr.eletutour.chaosmonkeyapplication.services;

import fr.eletutour.chaosmonkeyapplication.exception.UserException;
import fr.eletutour.chaosmonkeyapplication.models.User;
import fr.eletutour.chaosmonkeyapplication.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public boolean canAccessPremiumContent(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.map(user -> user.getSubscriptionType() == User.SubscriptionType.PREMIUM).orElse(false);
    }

    public boolean canAccessHDContent(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.map(user -> user.getSubscriptionType() == User.SubscriptionType.STANDARD ||
                user.getSubscriptionType() == User.SubscriptionType.PREMIUM).orElse(false);
    }

    public User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserException(UserException.UserError.USER_NOT_FOUND, "id=" + id));
    }
}
