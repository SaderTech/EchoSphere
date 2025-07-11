package music.echospere.service;

import lombok.AllArgsConstructor;
import music.echospere.entity.User;
import music.echospere.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class LoginService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public void checkLogin(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password cannot be null");
        } else if (username.isBlank() || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be blank or empty");
        } else if (password.length() < 8 || password.length() > 20) {
            throw new IllegalArgumentException("Password must be at least 8 characters and at most 20 characters long");
        }
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        if (!passwordEncoder.matches(password, user.get().getPasswordHash())) {
            throw new IllegalArgumentException("Incorrect password");
        }
        // User found and password matches, proceed with login
        System.out.println("Login successful for user: " + username);
    }
}
