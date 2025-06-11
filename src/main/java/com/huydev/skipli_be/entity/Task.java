package com.huydev.skipli_be.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Task {
    String id;
    String name;
    String description;
    TaskStatus status;
    String assignedTo;
    String cardId;
    Instant createdAt;
    Instant updatedAt;
}
