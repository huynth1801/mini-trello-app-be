package com.huydev.skipli_be.controller.authentication;


import com.huydev.skipli_be.dto.request.EmailRequest;
import com.huydev.skipli_be.dto.request.SignInRequest;
import com.huydev.skipli_be.dto.request.SignupRequest;
import com.huydev.skipli_be.dto.response.ApiResponse;
import com.huydev.skipli_be.dto.response.SignInResponse;
import com.huydev.skipli_be.dto.response.UserResponse;
import com.huydev.skipli_be.entity.VerificationType;
import com.huydev.skipli_be.service.authentication.VerificationService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    private final VerificationService verificationService;

    @PostMapping("/send-verification")
    public ResponseEntity<ApiResponse<Void>> sendVerification(@Valid @RequestBody EmailRequest emailRequest) {
        verificationService.sendVerificationCode(emailRequest.getEmail(), VerificationType.EMAIL_VERIFICATION);
        return ResponseEntity.ok(ApiResponse.success("Verification code sent to that email", null));
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        UserResponse userResponse = verificationService.signUp(signupRequest.getEmail(), signupRequest.getVerificationCode());
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @PostMapping("/signin/send-token")
        public ResponseEntity<ApiResponse<String>> sendSignInToken(@Valid @RequestBody SignInRequest signInRequest) {
            verificationService.sendVerificationCode(signInRequest.getEmail(), VerificationType.LOGIN_VERIFICATION);
            return ResponseEntity.ok(ApiResponse.success("Verification code sent to that email", null));
        }


    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest signInRequest) {
        try {
            SignInResponse response = verificationService.signIn(signInRequest.getEmail(), signInRequest.getVerificationCode());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (ValidationException e) {
               return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                       Map.of("error", "Invalid email or verification code")
               );
        }
    }
}
