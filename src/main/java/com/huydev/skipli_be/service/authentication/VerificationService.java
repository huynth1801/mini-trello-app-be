package com.huydev.skipli_be.service.authentication;

import com.huydev.skipli_be.entity.Verification;
import com.huydev.skipli_be.entity.VerificationType;
import com.huydev.skipli_be.exception.VerificationException;
import com.huydev.skipli_be.repository.UserRepository;
import com.huydev.skipli_be.repository.VerificationRepository;
import com.huydev.skipli_be.service.firebase.FirebaseService;
import com.huydev.skipli_be.service.mail.EmailSenderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class VerificationService {
    private final FirebaseService firebaseService;
    private final EmailSenderService emailSenderService;

    public void sendVerificationCode(String email) {
        // Check if user already exists
        if(firebaseService.userExists(email)) {
            throw new VerificationException("User " + email + " already exists");
        }

        firebaseService.deleteVerificationByEmail(email);
        String token = generateVerificationToken();

        Verification verification = Verification.builder()
                .email(email)
                .token(token)
                .expiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
                .verificationType(VerificationType.EMAIL_VERIFICATION)
                .build();

        firebaseService.saveVerification(verification);
        Map<String, Object> attributes = Map.of(
                "token", token
        );
        emailSenderService.sendVerificationToken(email, attributes);
    }

    private String generateVerificationToken() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}
