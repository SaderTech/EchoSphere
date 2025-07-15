package music.echospere.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@AllArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    public void sendAccountForgotPassword(String username, String email, String randomPassword, String token) {
        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom("haon22239@gmail.com", "Hệ thống EchoSphere");
            helper.setTo(email);
            helper.setSubject("Yêu cầu đặt lại mật khẩu EchoSphere");
            String htmlContent = String.format("""
                <html>
            <body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">
                <div style=\"max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;\">
                    <h2 style=\"color: #4CAF50; text-align: center;\">Đặt Lại Mật Khẩu</h2>
                    <p>Xin chào %s,</p>
                    <p>Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản EchoSphere của bạn. Dưới đây là mật khẩu tạm thời mới của bạn.</p>
                    <div style=\"background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 20px 0;\">
                        <h3 style=\"margin-top: 0; color: #333;\">Thông tin đăng nhập:</h3>
                        <p><strong>Tên đăng nhập:</strong> %s</p>
                        <p><strong>Mật khẩu tạm thời mới:</strong> <code style=\"background: #e9ecef; padding: 2px 6px; border-radius: 3px;\">%s</code></p>
                    </div>
                    <div style=\"background-color: #fff3cd; padding: 15px; border-radius: 5px; border-left: 4px solid #ffc107;\">
                        <p style=\"margin: 0;\"><strong>⚠️ Lưu ý bảo mật:</strong> Vui lòng đăng nhập và đổi mật khẩu ngay lập tức để bảo vệ tài khoản của bạn.</p>
                    </div>
                    <hr style=\"margin: 30px 0; border: none; border-top: 1px solid #eee;\">
                    <p style=\"font-size: 12px; color: #666; text-align: center;\">
                        Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.
                        <br>Email này được gửi tự động từ hệ thống EchoSphere. Vui lòng không trả lời.
                        <br>© 2025 EchoSphere
                    </p>
                </div>
            </body>
            </html>
                """,
                username, username, randomPassword
            );
            helper.setText(htmlContent, true);
            mimeMessage.setHeader("X-Priority", "1");
            mimeMessage.setHeader("X-Mailer", "EchoSphere Mail Service");
            mailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}