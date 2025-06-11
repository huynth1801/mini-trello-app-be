package com.huydev.skipli_be.entity;


import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import java.time.Instant;

@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Verification {
    String id;
    String userId;
    String email;
    String token;
    Instant expiresAt;
    VerificationType verificationType;
    Instant createdAt;
}
