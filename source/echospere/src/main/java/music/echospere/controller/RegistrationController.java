package music.echospere.controller;

import lombok.AllArgsConstructor;
import music.echospere.entity.Role;
import music.echospere.entity.User;
import music.echospere.entity.UserRole;
import music.echospere.entity.UserRoleId;
import music.echospere.repository.RoleRepository;
import music.echospere.repository.UserRepository;
import music.echospere.repository.UserRoleRepository;
import music.echospere.service.MailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;

@Controller
@RequestMapping("/registration")
@AllArgsConstructor
public class RegistrationController {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private MailService mailService;
    private UserRoleRepository userRoleRepository;
    private RoleRepository roleRepository;

    @GetMapping
    public String showRegistrationForm() {
        return "createAccount";
    }

    @PostMapping
    public String createUser(@RequestParam("firstName") String firstName,
                           @RequestParam("lastName") String lastName,
                           @RequestParam("username") String username,
                           @RequestParam("email") String email,
                           @RequestParam("passwordHash") String passwordHash,
                           Model model) {
        try {
            // Validate firstName
            if (firstName == null || firstName.isBlank() || firstName.isEmpty()) {
                model.addAttribute("error", "First name cannot be blank or empty");
                return "createAccount";
            }
            
            // Validate lastName
            if (lastName == null || lastName.isBlank() || lastName.isEmpty()) {
                model.addAttribute("error", "Last name cannot be blank or empty");
                return "createAccount";
            }
            
            // Validate username
            if (username == null || username.isBlank() || username.isEmpty()) {
                model.addAttribute("error", "Username cannot be blank or empty");
                return "createAccount";
            }
            
            // Validate email
            if (email == null || email.isBlank() || email.isEmpty()) {
                model.addAttribute("error", "Email cannot be blank or empty");
                return "createAccount";
            }
            
            // Validate password
            if (passwordHash == null || passwordHash.length() < 8 || passwordHash.length() > 20) {
                model.addAttribute("error", "Password must be at least 8 characters and at most 20 characters long");
                return "createAccount";
            }
            
            // Check if username already exists
            if (userRepository.existsByUsername(username)) {
                model.addAttribute("error", "Username already exists");
                return "createAccount";
            }
            
            // Check if email already exists
            if (userRepository.existsByEmail(email)) {
                model.addAttribute("error", "Email already exists");
                return "createAccount";
            }
            
            // Create new user
            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUsername(username);
            user.setEmail(email);
            user.setPasswordHash(passwordEncoder.encode(passwordHash));
            user.setRegistrationDate(Instant.now());
            
            // Save the user first
            User savedUser = userRepository.save(user);
            
            // Create UserRole with default role "User" (id = 2)
            Role userRole = roleRepository.findById(2).orElseThrow(() ->
                new RuntimeException("Default user role not found"));
            
            UserRole newUserRole = new UserRole();
            UserRoleId userRoleId = new UserRoleId();
            userRoleId.setUserId(savedUser.getId());
            userRoleId.setRoleId(2);
            
            newUserRole.setId(userRoleId);
            newUserRole.setRole(userRole);
            
            userRoleRepository.save(newUserRole);
            

            return "redirect:/login?success";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Registration failed. Please try again.");
            return "createAccount";
        }
    }
}