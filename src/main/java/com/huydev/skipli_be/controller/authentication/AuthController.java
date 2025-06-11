package com.huydev.skipli_be.controller.authentication;


import com.huydev.skipli_be.dto.request.EmailRequest;
import com.huydev.skipli_be.dto.request.SignupRequest;
import com.huydev.skipli_be.dto.response.ApiResponse;
import com.huydev.skipli_be.dto.response.UserResponse;
import com.huydev.skipli_be.service.authentication.VerificationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    private final VerificationService verificationService;

    @PostMapping("/send-verification")
    public ResponseEntity<ApiResponse<Void>> sendVerification(@Valid @RequestBody EmailRequest emailRequest) {
        verificationService.sendVerificationCode(emailRequest.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Verification code sent to that email", null));
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        UserResponse userResponse = verificationService.signUp(signupRequest.getEmail(), signupRequest.getVerificationCode());
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }
}
