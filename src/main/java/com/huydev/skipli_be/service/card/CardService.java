package com.huydev.skipli_be.service.card;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.huydev.skipli_be.dto.request.CardCreationRequest;
import com.huydev.skipli_be.dto.request.CardUpdateRequest;
import com.huydev.skipli_be.dto.response.CardCreationResponse;
import com.huydev.skipli_be.dto.response.CardUserResponse;
import com.huydev.skipli_be.entity.Card;
import com.huydev.skipli_be.entity.Task;
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
    private static final String TASK_COLLECTION = "tasks";
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
            DocumentReference cardRef = db.collection(CARD_COLLECTION).document();
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

            if (card.getTasks() != null && !card.getTasks().isEmpty()) {
                CollectionReference taskCollection = cardRef.collection(TASK_COLLECTION);
                for (Task task : card.getTasks()) {
                    DocumentReference taskRef = taskCollection.document();
                    task.setId(taskRef.getId());
                    taskRef.set(task).get();
                }
            }

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

    // Retrieve card detail
    public CardCreationResponse retrieveCardById(String boardId, String cardId, String userId) {
        Card card = getCardFromFirestoreById(boardId, cardId, userId);
        return CardCreationResponse.builder()
                .id(card.getId())
                .name(card.getName())
                .description(card.getDescription())
                .build();
    }

    private Card getCardFromFirestoreById(String boardId, String cardId, String userId) {
        try {
            if(!hasAccessToBoard(boardId, userId)) {
                throw new RuntimeException("Access denied");
            }

            Firestore db = getFirestore();
            DocumentReference docRef = db.collection(CARD_COLLECTION).document(cardId);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if(!document.exists()) {
                throw new RuntimeException("Card not found");
            }

            String cardBoardId = document.getString("boardId");
            if (!boardId.equals(cardBoardId)) {
                throw new RuntimeException("Card does not belong to this board");
            }
            return convertDocumentToCard(document);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get card from firestore" + e);
        }
    }

    // Retrieve cards by User
    public List<CardUserResponse> getCardsByBoardId(String boardId, String userId) {
        List<Card> cards = getCardFromFirestoreByName(boardId, userId);
        List<CardUserResponse> responses = new ArrayList<>();

        for (Card card : cards) {
            int tasksCount = card.getTasks() != null ? card.getTasks().size() : 0;

            CardUserResponse response = CardUserResponse.builder()
                    .id(card.getId())
                    .name(card.getName())
                    .description(card.getDescription())
                    .tasks_count(tasksCount)
                    .list_member(card.getList_member())
                    .createdAt(card.getCreatedAt())
                    .build();

            responses.add(response);
        }

        return responses;
    }

    // Update card
    public CardCreationResponse updateCardById(String boardId, String cardId, CardUpdateRequest request,String userId) {
        Card updatedCard = updateCardFromFireStore(boardId, cardId, request, userId);
        return  CardCreationResponse.builder()
                .id(updatedCard.getId())
                .name(updatedCard.getName())
                .description(updatedCard.getDescription())
                .build();
    }

    private Card updateCardFromFireStore(String boardId, String cardId, CardUpdateRequest request , String userId) {
        try {
            if(!hasAccessToBoard(boardId, userId)) {
                throw new RuntimeException("Access denied");
            }

            Firestore db = getFirestore();
            DocumentReference cardRef = db.collection(CARD_COLLECTION).document(cardId);

            ApiFuture<DocumentSnapshot> future = cardRef.get();
            DocumentSnapshot document = future.get();

            if(!document.exists()) {
                throw new RuntimeException("Card not found");
            }

            String cardBoardId = document.getString("boardId");
            if (!boardId.equals(cardBoardId)) {
                throw new RuntimeException("Card does not belong to this board");
            }

            Map<String, Object> updates = new HashMap<>();
            if(request.getName() != null && !request.getName().trim().isEmpty()) {
                updates.put("name", request.getName().trim());
            }
            if(request.getDescription() != null) {
                updates.put("description", request.getDescription().trim());
            }
            if (request.getParams() != null) {
                updates.put("params", request.getParams());
            }

            ApiFuture<WriteResult> updateFuture = cardRef.update(updates);
            updateFuture.get();

            ApiFuture<DocumentSnapshot> updatedFuture = cardRef.get();
            DocumentSnapshot updatedDocument = updatedFuture.get();
            return convertDocumentToCard(updatedDocument);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update card" + e);
        }
    }

    public void deleteCardById(String boardId, String cardId, String userId) {
        deleteCardFromFireStore(boardId, cardId, userId);
    }

    private void deleteCardFromFireStore(String boardId, String cardId, String userId) {
        try {
            if(!hasAccessToBoard(boardId, userId)) {
                throw new RuntimeException("Access denied. You can not delete this card");
            }

            Firestore db = getFirestore();
            ApiFuture<QuerySnapshot> future = db.collection(CARD_COLLECTION)
                    .whereEqualTo("boardId", boardId)
                    .whereEqualTo("cardId", cardId)
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            for (QueryDocumentSnapshot document : documents) {
                document.getReference().delete();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error deleting card from Firestore", e);
        }
    }

    private List<Card> getCardFromFirestoreByName(String boardId, String userId) {
        try {
            if(!hasAccessToBoard(boardId, userId)) {
                throw new RuntimeException("Access denied");
            }

            Firestore db = getFirestore();
            List<Card> cards = new ArrayList<>();

            ApiFuture<QuerySnapshot> future = db.collection(CARD_COLLECTION)
                    .whereEqualTo("boardId", boardId)
                    .whereArrayContains("list_member", userId)
                    .get();

            QuerySnapshot querySnapshot = future.get();

            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                Card card = convertDocumentToCard(document);
                cards.add(card);
            }

            return cards;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get cards: " + e.getMessage());        }
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

    private Card convertDocumentToCard(DocumentSnapshot document) {
        Card card = new Card();
        card.setId(document.getString("id"));
        card.setName(document.getString("name"));
        card.setDescription(document.getString("description"));
        card.setBoardId(document.getString("boardId"));
        card.setCreatedBy(document.getString("createdBy"));
        card.setList_member((List<String>) document.get("list_member"));
        // Parse timestamps
        String createdAtStr = document.getString("createdAt");
        String updatedAtStr = document.getString("updatedAt");

        if (createdAtStr != null) {
            card.setCreatedAt(Instant.parse(createdAtStr));
        }
        if (updatedAtStr != null) {
            card.setUpdatedAt(Instant.parse(updatedAtStr));
        }

        return card;
    }
}
