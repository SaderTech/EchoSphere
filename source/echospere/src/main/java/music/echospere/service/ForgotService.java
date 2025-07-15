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
        System.out.println("Forgot password request for: " + email);
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            System.out.println("Email not found: " + email);
            return false; // User not found
        }

        User user = userOptional.get();
        String randomPassword = RandomStringUtils.randomAlphanumeric(8);
        user.setPasswordHash(passwordEncoder.encode(randomPassword));
        userRepository.save(user);

        // Không tạo token, chỉ gửi mật khẩu mới qua email
        System.out.println("Sending mail to: " + user.getEmail());
        mailService.sendAccountForgotPassword(user.getUsername(), user.getEmail(), randomPassword, null);
        System.out.println("Mail sent (or attempted) to: " + user.getEmail());
        return true;
    }

}