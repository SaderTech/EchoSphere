package music.echospere.service;

import lombok.AllArgsConstructor;
import music.echospere.entity.User;
import music.echospere.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LoginService {
    private final UserRepository userRepository;
    public void checkLogin(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (username==null || password==null){
            throw new IllegalArgumentException("Username and password cannot be null");
        }  else if (username.isBlank() || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be blank or empty");
        } else if (password.length() < 8 || password.length() > 20) {
            throw new IllegalArgumentException("Password must be at least 8 characters and at most 20 characters long");
        }
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        if (!user.getPasswordHash().equals(password)) {
            throw new IllegalArgumentException("Incorrect password");
        }
        if (username.equals(user.getUsername()) && password.equals(user.getPasswordHash())){
            // User found and password matches, proceed with login
            System.out.println("Login successful for user: " + username);
        } else {
            // If the username or password does not match, throw an exception
            throw new IllegalArgumentException("Invalid username or password");
        }

    }
}
