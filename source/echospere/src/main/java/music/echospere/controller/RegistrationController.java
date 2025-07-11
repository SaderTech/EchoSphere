package music.echospere.controller;

import lombok.AllArgsConstructor;
import music.echospere.entity.User;
import music.echospere.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
public class RegistrationController {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @GetMapping("/registration")
    public String showRegistrationForm() {
        return "createAccount"; // This should return the registration form view
    }
    @PostMapping("/registration")
    @ResponseBody
    public User createUser(@RequestBody User user){
        if (user.getUsername() == null || user.getPasswordHash() == null) {
            throw new IllegalArgumentException("Username and password cannot be null");
        } else if (user.getUsername().isBlank() || user.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be blank or empty");
        } else if (user.getPasswordHash().length() < 8 || user.getPasswordHash().length() > 20) {
            throw new IllegalArgumentException("Password must be at least 8 characters and at most 20 characters long");
        }
        user.setFirstName(user.getUsername());
        user.setLastName(user.getUsername());
        user.setUsername(user.getUsername());
        // Encode the password before saving
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setEmail(user.getUsername());
        // Here you would typically save the user to the database
        // For now, we just return the user object
        return userRepository.save(user);
    }
}
