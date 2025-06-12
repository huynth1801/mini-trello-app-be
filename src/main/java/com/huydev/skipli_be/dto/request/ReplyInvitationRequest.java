package com.huydev.skipli_be.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.huydev.skipli_be.entity.InviteStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReplyInvitationRequest {
    @JsonProperty("invite_id")
    String inviteId;

    @JsonProperty("card_id")
    String cardId;

    @JsonProperty("member_id")
    String memberId;

    @JsonProperty("status")
    String status;
}
