package com.huydev.skipli_be.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.huydev.skipli_be.entity.InviteStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BoardInviteRequest {
    @JsonProperty("invite_id")
    String inviteId;

    @JsonProperty("board_owner_id")
    String boardOwnerId;

    @JsonProperty("member_id")
    String memberId;

    @JsonProperty("email_member")
    String emailMember;

    @JsonProperty("status")
    String status = InviteStatus.PENDING.toString();
}
