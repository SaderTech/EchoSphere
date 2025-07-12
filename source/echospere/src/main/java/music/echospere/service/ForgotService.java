package music.echospere.service;

import lombok.AllArgsConstructor;
import music.echospere.entity.User;
import music.echospere.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ForgotService {
    private final UserRepository userRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    public boolean forgotPass(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return false; // User not found
        }

        User user = userOptional.get();
        String randomPassword = RandomStringUtils.randomAlphanumeric(8);
        user.setPasswordHash(passwordEncoder.encode(randomPassword));
        userRepository.save(user);

        mailService.sendAccountForgotPassword(user.getUsername(), user.getEmail(), randomPassword);
        return true;
    }

}