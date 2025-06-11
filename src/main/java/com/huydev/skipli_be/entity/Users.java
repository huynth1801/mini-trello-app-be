package com.huydev.skipli_be.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Users {
    String id;
    String email;
    boolean verified;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
