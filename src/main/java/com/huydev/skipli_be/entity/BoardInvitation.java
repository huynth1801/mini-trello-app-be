package com.huydev.skipli_be.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BoardInvitation {
    String inviteId;
    String boardId;
    String boardOwnerId;
    String memberId;
    String emailMember;
    InviteStatus status;
    Instant createdAt;
    Instant updatedAt;
}
