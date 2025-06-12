package com.huydev.skipli_be.service.board;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.huydev.skipli_be.dto.request.BoardInviteRequest;
import com.huydev.skipli_be.entity.BoardInvitation;
import com.huydev.skipli_be.entity.InviteStatus;
import com.huydev.skipli_be.service.mail.EmailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardInvitationService {
    private final EmailSenderService emailSenderService;
    private static final String USERS_COLLECTION = "users";
    private static final String BOARD_COLLECTION = "boards";
    private static final String INVITE_COLLECTION = "board_invitations";

    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    public void inviteToBoard(String boardId, BoardInviteRequest request, String currentUserId) throws InterruptedException, ExecutionException {
        if (!canInviteToBoard(boardId, currentUserId)) {
            throw new RuntimeException("Access denied: You don't have permission to access this board.");
        }

        if (invitationExists(boardId, request.getMemberId())) {
            throw new RuntimeException("Invitation already exists.");
        }

        String boardName = getBoardName(boardId);
        String ownerName = getUserName(currentUserId);

        String inviteId = request.getInviteId();
        if (inviteId == null || inviteId.trim().isEmpty()) {
            inviteId = UUID.randomUUID().toString();
        }

        BoardInvitation invitation = BoardInvitation.builder()
                .inviteId(inviteId)
                .boardId(boardId)
                .boardOwnerId(currentUserId)
                .memberId(request.getMemberId())
                .emailMember(request.getEmailMember())
                .status(InviteStatus.valueOf(request.getStatus()))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        saveInvitationToFirestore(invitation);

        sendInvitationEmail(invitation, boardName, ownerName);

        log.info("Board invitation sent successfully: inviteId={}, boardId={}, memberId={}",
                inviteId, boardId, request.getMemberId());
    }

    private void saveInvitationToFirestore(BoardInvitation invitation)
            throws InterruptedException, ExecutionException {

        Firestore db = getFirestore();
        Map<String, Object> invitationMap = new HashMap<>();
        invitationMap.put("inviteId", invitation.getInviteId());
        invitationMap.put("boardId", invitation.getBoardId());
        invitationMap.put("boardOwnerId", invitation.getBoardOwnerId());
        invitationMap.put("memberId", invitation.getMemberId());
        invitationMap.put("emailMember", invitation.getEmailMember());
        invitationMap.put("status", invitation.getStatus().toString());
        invitationMap.put("createdAt", invitation.getCreatedAt().toString());
        invitationMap.put("updatedAt", invitation.getUpdatedAt().toString());

        db.collection(INVITE_COLLECTION)
                .document(invitation.getInviteId())
                .set(invitationMap)
                .get();

        log.info("Invitation saved: {}", invitation.getInviteId());
    }

    private void sendInvitationEmail(BoardInvitation invitation, String boardName, String ownerName) {
        try {
            if (invitation.getEmailMember() == null || invitation.getEmailMember().trim().isEmpty()) {
                log.info("No email provided for invitation: {}", invitation.getInviteId());
                return;
            }

            Map<String, Object> emailData = new HashMap<>();
            emailData.put("boardName", boardName);
            emailData.put("ownerName", ownerName);
            emailData.put("inviteId", invitation.getInviteId());
            emailData.put("boardId", invitation.getBoardId());

            String invitationLink = String.format("https://your-app.com/boards/invitations/%s",
                    invitation.getBoardId());
            emailData.put("invitationLink", invitationLink);

            emailSenderService.sendBoardInvitationEmail(invitation.getEmailMember(), emailData);

            log.info("Board invitation email sent to: {}", invitation.getEmailMember());

        } catch (Exception e) {
            log.error("Failed to send invitation email: {}", e.getMessage(), e);
        }
    }

    public boolean canInviteToBoard(String boardId, String userId) {
        try {
            log.debug("Checking permission for user {} on board {}", userId, boardId);
            Firestore db = getFirestore();
            DocumentReference boardRef = db.collection(BOARD_COLLECTION).document(boardId);
            ApiFuture<DocumentSnapshot> future = boardRef.get();
            DocumentSnapshot document = future.get();

            if (!document.exists()) {
                return false;
            }

            List<String> userIds = (List<String>) document.get("userIds");
            log.debug("User {} access to board {}", userId, boardId);
            return userIds != null && userIds.contains(userId);

        } catch (Exception e) {
            log.error("Error checking board access: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean invitationExists(String boardId, String memberId)
            throws InterruptedException, ExecutionException {
        Firestore db = getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(INVITE_COLLECTION)
                .whereEqualTo("boardId", boardId)
                .whereEqualTo("memberId", memberId)
                .whereEqualTo("status", InviteStatus.PENDING.toString())
                .get();
        QuerySnapshot querySnapshot = future.get();
        return !querySnapshot.isEmpty();
    }

    // Helper function
    private String getBoardName(String boardId) {
        try {
            Firestore db = getFirestore();
            DocumentReference boardRef = db.collection(BOARD_COLLECTION).document(boardId);
            ApiFuture<DocumentSnapshot> future = boardRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                return document.getString("name");
            }
            return "Unknown Board";
        } catch (Exception e) {
            log.error("Error getting board name: {}", e.getMessage());
            return "Unknown Board";
        }
    }

    private String getUserName(String userId) {
        try {
            Firestore db = getFirestore();
            DocumentReference userRef = db.collection(USERS_COLLECTION).document(userId);
            ApiFuture<DocumentSnapshot> future = userRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                return document.getString("email");
            }
            return "Unknown User";
        } catch (Exception e) {
            log.error("Error getting user name: {}", e.getMessage());
            return "Unknown User";
        }
    }
}
