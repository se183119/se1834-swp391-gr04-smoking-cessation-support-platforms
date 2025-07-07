package com.team04.smoking_cessation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.name}")
    private String appName;

    public void sendVerificationEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Email Verification - " + appName);
        message.setText("Please click the following link to verify your email: " +
                "http://localhost:8080/api/auth/verify-email?token=" + token);

        try {
            mailSender.send(message);
        } catch (Exception e) {
            // Log error but don't fail registration
            System.err.println("Failed to send verification email: " + e.getMessage());
        }
    }

    public void sendMotivationalMessage(String to, String message) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(to);
        mail.setSubject("Stay Strong - " + appName);
        mail.setText(message);

        try {
            mailSender.send(mail);
        } catch (Exception e) {
            System.err.println("Failed to send motivational email: " + e.getMessage());
        }
    }

    public void sendAchievementNotification(String to, String achievementName) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(to);
        mail.setSubject("Congratulations! New Achievement Unlocked - " + appName);
        mail.setText("Congratulations! You've earned the '" + achievementName + "' achievement. " +
                "Keep up the great work on your quit smoking journey!");

        try {
            mailSender.send(mail);
        } catch (Exception e) {
            System.err.println("Failed to send achievement email: " + e.getMessage());
        }
    }

    public void sendContactMessage(String fromEmail, String message) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo("admin@smokingcessation.com"); // Admin email
        mail.setSubject("Contact Message from Platform - " + appName);
        mail.setText("Contact message from: " + fromEmail + "\n\n" + message);

        try {
            mailSender.send(mail);
        } catch (Exception e) {
            System.err.println("Failed to send contact message: " + e.getMessage());
        }
    }
}
