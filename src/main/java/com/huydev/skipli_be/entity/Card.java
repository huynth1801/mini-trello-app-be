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
public class Card {
    String id;
    String name;
    String description;
    Integer position;
    List<String> list_member;
    Instant createdAt;
    Instant updatedAt;
    String boardId;
    String createdBy;
    Integer taskCount;
    List<Task> tasks;
}
