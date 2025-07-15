package music.echospere.security;

import lombok.AllArgsConstructor;
import music.echospere.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public UserDetailsService userDetailsService() {
        return userDetailsService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(registry -> {
                    // Cho phép truy cập vào trang đăng nhập, đăng ký và các tài nguyên tĩnh
                    registry.requestMatchers("/login", "/registration", "/forgot-password", "/change-password", "/css/**", "/js/**", "/images/**").permitAll();
                    // Tất cả các yêu cầu khác cần phải được xác thực
                    registry.anyRequest().authenticated();
                })
                .formLogin(httpForm -> {
                    // Cấu hình trang đăng nhập
                    httpForm.loginPage("/login"); // Đã có .permitAll() ở trên
                    // Trang chuyển hướng sau khi đăng nhập thành công
                    httpForm.defaultSuccessUrl("/home", true);
                })
                .build();
    }
}
