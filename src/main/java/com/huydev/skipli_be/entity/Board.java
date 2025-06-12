package com.huydev.skipli_be.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Board {
    String id;
    String name;
    String description;
    List<String> userIds;
    Instant createdAt;
    Instant updatedAt;
}
