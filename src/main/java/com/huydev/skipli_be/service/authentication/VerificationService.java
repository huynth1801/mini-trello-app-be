package com.huydev.skipli_be.service.authentication;

import com.huydev.skipli_be.config.JwtUtils;
import com.huydev.skipli_be.dto.response.SignInResponse;
import com.huydev.skipli_be.dto.response.UserResponse;
import com.huydev.skipli_be.entity.Users;
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
    private final JwtUtils jwtUtils;

    public void sendVerificationCode(String email, VerificationType verificationType) {
        if(verificationType == VerificationType.EMAIL_VERIFICATION && firebaseService.userExists(email)) {
            throw new VerificationException("Email already exists");
        }

        if (verificationType == VerificationType.LOGIN_VERIFICATION && !firebaseService.userExists(email)) {
            throw new VerificationException("User not found");
        }

        firebaseService.deleteVerificationByEmail(email, verificationType);
        String token = generateVerificationToken();

        Verification verification = Verification.builder()
                .email(email)
                .token(token)
                .expiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
                .createdAt(Instant.now())
                .verificationType(verificationType)
                .build();

        firebaseService.saveVerification(verification);
        Map<String, Object> attributes = Map.of(
                "token", token
        );
        emailSenderService.sendVerificationToken(email, attributes);
    }

    public UserResponse signUp(String email, String verificationCode) {
        Verification verification = firebaseService.getVerificationByEmailAndToken(email, verificationCode, VerificationType.EMAIL_VERIFICATION);

        if(verification == null) {
            throw new VerificationException("Invalid verification code");
        }

        if(verification.getExpiresAt().isBefore(Instant.now())) {
            throw new VerificationException("Expired verification code");
        }

        if(firebaseService.userExists(email)) {
            throw new VerificationException("User " + email + " already exists");
        }

        Users user = Users.builder()
                .email(email)
                .verified(true)
                .build();

        String userId = firebaseService.saveUser(user);

        return UserResponse.builder()
                .id(userId)
                .email(email)
                .build();
    }

    public SignInResponse signIn(String email, String verificationCode) {
        Verification verification = firebaseService.getVerificationByEmailAndToken(email, verificationCode, VerificationType.LOGIN_VERIFICATION);
        log.info("Verification type: " + verification);
        if (verification == null) {
            throw new VerificationException("Invalid verification code");
        }

        if (verification.getExpiresAt().isBefore(Instant.now())) {
            throw new VerificationException("Expired verification code");
        }

        if (verification.getVerificationType() != VerificationType.LOGIN_VERIFICATION) {
            throw new VerificationException("Invalid verification type");
        }

        if (!firebaseService.userExists(email)) {
            throw new VerificationException("User not found");
        }

        Users user = firebaseService.getUserByEmail(email);

        if (user == null) {
            throw new VerificationException("User not found");
        }

        if (!user.isVerified()) {
            throw new VerificationException("User is not verified");
        }

        firebaseService.deleteVerificationByEmail(email, VerificationType.LOGIN_VERIFICATION);

        String accessToken = jwtUtils.generateToken(user);

        return SignInResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    private String generateVerificationToken() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}
