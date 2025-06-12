package com.huydev.skipli_be.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardUserResponse {
    String id;
    String name;
    String description;
    Integer tasks_count;
    List<String> list_member;
    Instant createdAt;
}
