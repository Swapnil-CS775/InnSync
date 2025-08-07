package com.innsync.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	@Autowired
    private JavaMailSender mailSender;
	
	@Value("${spring.mail.username}")
	private String fromEmail;

    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        // Construct the password reset link
        String resetUrl = "http://localhost:3000/reset-password?token=" + resetToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail); // Use the same email from your properties
        message.setTo(toEmail);
        message.setSubject("Innsync - Password Reset Request");
        message.setText("To reset your password, please click the link below:\n\n"
                        + resetUrl + "\n\n"
                        + "If you did not request a password reset, please ignore this email.");

        // Send the email
        mailSender.send(message);
    }
}
