package com.huydev.skipli_be.controller.board;

import com.huydev.skipli_be.config.JwtUtils;
import com.huydev.skipli_be.dto.request.BoardInviteRequest;
import com.huydev.skipli_be.dto.request.ReplyInvitationRequest;
import com.huydev.skipli_be.dto.response.BoardInviteResponse;
import com.huydev.skipli_be.service.board.BoardInvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
@Slf4j
public class BoardInvitationController {
    private final BoardInvitationService boardInvitationService;
    private final JwtUtils jwtUtils;

    @PostMapping("/{boardId}/invite")
    public ResponseEntity<BoardInviteResponse> inviteToBoard(@PathVariable String boardId, @RequestBody BoardInviteRequest boardInviteRequest,
                                                             @RequestHeader("Authorization") String authorizationHeader) throws ExecutionException, InterruptedException {
        String currentUserId = getUserIdFromAuthorizationToken(authorizationHeader);
        log.info("Current user: {}", currentUserId);
        log.info("Board Id: {}", boardId);
        log.info("Board Invite Request: {}", boardInviteRequest);

        if (boardId == null || boardId.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(BoardInviteResponse.builder()
                            .success(false)
                            .build());
        }

        if (boardInviteRequest.getMemberId() == null || boardInviteRequest.getMemberId().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(BoardInviteResponse.builder()
                            .success(false)
                            .build());
        }

        if (currentUserId.equals(boardInviteRequest.getMemberId())) {
            return ResponseEntity.badRequest()
                    .body(BoardInviteResponse.builder()
                            .success(false)
                            .build());
        }

        boardInvitationService.inviteToBoard(boardId, boardInviteRequest, currentUserId);

        return ResponseEntity.ok(
                BoardInviteResponse.builder()
                        .success(true)
                        .build()
        );
    }

    // Reply request accepted/declined
    @PostMapping("/{boardId}/cards/{id}/invite/accept")
    public ResponseEntity<BoardInviteResponse> acceptInvitation(@PathVariable String boardId, @PathVariable String id,
                                                                @RequestBody ReplyInvitationRequest replyInvitationRequest,
                                                                @RequestHeader("Authorization") String authorizationHeader) throws ExecutionException, InterruptedException {
        String currentUserId = getUserIdFromAuthorizationToken(authorizationHeader);

        if(!Arrays.asList("ACCEPTED", "DECLINED").contains(replyInvitationRequest.getStatus())) {
           return ResponseEntity.badRequest()
                   .body(BoardInviteResponse.builder()
                           .success(false)
                           .message("Invalid status. Must be 'ACCEPTED' or 'DECLINED' ")
                           .build());
        }

        boardInvitationService.updateInvitationStatus(replyInvitationRequest.getInviteId(), boardId , replyInvitationRequest.getStatus(), currentUserId);

        return ResponseEntity.ok(
                BoardInviteResponse.builder()
                        .success(true)
                        .message("Invitation " + replyInvitationRequest.getStatus() + " successfully")
                        .build()
        );
    }

    private String getUserIdFromAuthorizationToken(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "").trim();
        return jwtUtils.extractUserId(token);
    }
}
