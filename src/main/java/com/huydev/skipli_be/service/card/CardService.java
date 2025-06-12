package com.huydev.skipli_be.service.card;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.huydev.skipli_be.dto.request.CardCreationRequest;
import com.huydev.skipli_be.dto.response.CardCreationResponse;
import com.huydev.skipli_be.entity.Card;
import com.huydev.skipli_be.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardService {
    private static final String CARD_COLLECTION = "cards";
    private static final String BOARD_COLLECTION = "boards";
    private final BoardService boardService;

    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    // Create new card
    public CardCreationResponse createCard(String boardId, CardCreationRequest cardCreationRequest, String userId) {
        Card card = new Card();
        card.setName(cardCreationRequest.getName());
        card.setDescription(cardCreationRequest.getDescription());
        card.setCreatedAt(Instant.now());
        Card saveCard = saveCardToFirestore(boardId, card, userId);
        return CardCreationResponse.builder()
                .id(saveCard.getId())
                .name(saveCard.getName())
                .description(saveCard.getDescription())
                .build();
    }

    private Card saveCardToFirestore(String boardId, Card card, String userId) {
        try {
            if(!hasAccessToBoard(boardId, userId)) {
                throw new RuntimeException("Access denied");
            }

            Firestore db = getFirestore();
            String cardId = UUID.randomUUID().toString();

            List<String> userIdsList = new ArrayList<>();
            userIdsList.add(userId);

            Map<String, Object> cardMap = new HashMap<>();
            cardMap.put("id", cardId);
            cardMap.put("name", card.getName());
            cardMap.put("description", card.getDescription());
            cardMap.put("boardId", boardId);
            cardMap.put("createdBy", userId);
            cardMap.put("list_member", card.getList_member() != null ? card.getList_member() : userIdsList);
            cardMap.put("createdAt", Instant.now().toString());
            cardMap.put("updatedAt", Instant.now().toString());

            ApiFuture<WriteResult> future = db.collection(CARD_COLLECTION)
                    .document(cardId)
                    .set(cardMap);
            future.get();

            Card createdCard = new Card();
            createdCard.setId(cardId);
            createdCard.setName(card.getName());
            createdCard.setDescription(card.getDescription());
            createdCard.setBoardId(boardId);
            createdCard.setCreatedBy(userId);
            createdCard.setList_member(card.getList_member() != null ? card.getList_member() : new ArrayList<>());
            createdCard.setCreatedAt(Instant.now());
            createdCard.setUpdatedAt(Instant.now());
            createdCard.setTaskCount(0);
            return createdCard;
        }  catch (Exception e) {
            throw new RuntimeException("Failed to save card" + e);
        }

    }

    // Check if user can access to the board
    private boolean hasAccessToBoard(String boardId, String userId) {
        try {
            Firestore db = getFirestore();
            DocumentReference boardRef = db.collection(BOARD_COLLECTION).document(boardId);
            ApiFuture<DocumentSnapshot> future = boardRef.get();
            DocumentSnapshot document = future.get();

            if(!document.exists()) {
                return false;
            }

            List<String> userIds = (List<String>) document.get("userIds");
            log.info("User IDs: " + userIds);
            return userIds != null && userIds.contains(userId);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isCardRelatedToUser(DocumentSnapshot documentSnapshot, String userId) {
        String createdBy = documentSnapshot.getString("createdBy");
        if(userId.equals(createdBy)) {
            return true;
        }

        List<String> listMember = (List<String>) documentSnapshot.get("list_member");
        return listMember != null && listMember.contains(userId);
    }
}
