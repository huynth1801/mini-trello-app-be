package com.huydev.skipli_be.service.board;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.huydev.skipli_be.dto.request.BoardCreationRequest;
import com.huydev.skipli_be.dto.response.BoardCreationResponse;
import com.huydev.skipli_be.entity.Board;

import com.huydev.skipli_be.service.firebase.FirebaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {
    private final FirebaseService firebaseService;
    private static final String BOARD_COLLECTION = "boards";
    private static final String CARD_COLLECTION = "cards";

    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    public BoardCreationResponse createBoard(BoardCreationRequest boardCreationRequest, String userId) {
        try {
            Board board = new Board();
            board.setName(boardCreationRequest.getName());
            board.setDescription(boardCreationRequest.getDescription());

            String boardId = saveBoardToFireStore(board, userId);

            return BoardCreationResponse.builder()
                    .id(boardId)
                    .name(board.getName())
                    .description(board.getDescription())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create board" + e.getMessage());
        }
    }

    private String saveBoardToFireStore(Board board, String userId) {
        try {
            Firestore db = getFirestore();
            String boardId = UUID.randomUUID().toString();

            // Create list userId
            List<String> userIdsList = new ArrayList<>();
            userIdsList.add(userId);

            Map<String, Object> boardMap = new HashMap<>();
            boardMap.put("id", boardId);
            boardMap.put("name", board.getName());
            boardMap.put("description", board.getDescription());
            boardMap.put("userIds", userIdsList);
            boardMap.put("createdAt", Instant.now().toString());
            boardMap.put("updatedAt", Instant.now().toString());

            DocumentReference boardRef = db.collection(BOARD_COLLECTION).document(boardId);
            ApiFuture<WriteResult> boardResult = boardRef.set(boardMap);
            boardResult.get();

            List<String> defaultCardLists = Arrays.asList("Icebox", "Backlog", "On Going", "Waiting for Review", "Done");

            CollectionReference listRef = db.collection(CARD_COLLECTION);

            for (int i = 0; i < defaultCardLists.size(); i++) {
                Map<String, Object> listMap = new HashMap<>();
                listMap.put("id", UUID.randomUUID().toString());
                listMap.put("name", defaultCardLists.get(i));
                listMap.put("boardId", boardId);
                listMap.put("description", null);
                listMap.put("createdBy", userId);
                listMap.put("list_member", userIdsList);
                listMap.put("position", i);
                listMap.put("createdAt", Instant.now().toString());
                listMap.put("updatedAt", Instant.now().toString());
                listRef.add(listMap);
            }

//            db.collection(BOARD_COLLECTION).document(boardId).set(boardMap).get();
            return boardId;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save board" + e);
        }
    }

    // Get all boards
    public List<BoardCreationResponse> getAllBoardsByUserId(String userId) throws InterruptedException {
        List<Board> boards = getAllBoardsByUserIdFromFireStore(userId);
        return boards.stream()
                .map(board -> new BoardCreationResponse(board.getId(), board.getName(), board.getDescription()))
                .collect(Collectors.toList());
    }

    private List<Board> getAllBoardsByUserIdFromFireStore(String userId) throws InterruptedException {
        try {
            Firestore db = getFirestore();
            List<Board> boards = new ArrayList<>();

            ApiFuture<QuerySnapshot> future = db.collection(BOARD_COLLECTION)
                    .whereArrayContains("userIds", userId)
                    .get();

            QuerySnapshot querySnapshot = future.get();

            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                Board board = convertDocumentToBoard(document);
                boards.add(board);
            }

            return boards;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get boards for user: " + e.getMessage());
        }
    }

    // Get board by boardId
    public BoardCreationResponse getBoardById(String boardId, String userId) throws InterruptedException {
        Board board = getBoardByIdFromFireStore(boardId, userId);
        return BoardCreationResponse.builder()
                .id(boardId)
                .name(board.getName())
                .description(board.getDescription())
                .build();
    }

    private Board getBoardByIdFromFireStore(String boardId, String userId) throws InterruptedException {
        try {
            Firestore db = getFirestore();

            DocumentReference docRef = db.collection(BOARD_COLLECTION).document(boardId);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if(!document.exists()) {
                throw new RuntimeException("Board not found: " + boardId);
            }

            List<String> userIdsList = (List<String>) document.get("userIds");
            if(userIdsList == null || !userIdsList.contains(userId)) {
                throw new RuntimeException("Access denied");
            }

            return convertDocumentToBoard(document);
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to get boards by id from firestore" + e.getMessage());
        }
    }

    // Update board by Id
    public BoardCreationResponse updateBoardById(String boardId, BoardCreationRequest boardCreationRequest, String userId) throws InterruptedException {
        Board updatedBoard = updateBoardToFireStore(boardId, boardCreationRequest, userId);
        return BoardCreationResponse.builder()
                .id(updatedBoard.getId())
                .name(updatedBoard.getName())
                .description(updatedBoard.getDescription())
                .build();
    }

    private Board updateBoardToFireStore(String boardId, BoardCreationRequest request, String userId) throws InterruptedException {
        try {
            Firestore db = getFirestore();
            DocumentReference docRef = db.collection(BOARD_COLLECTION).document(boardId);

            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if(!document.exists()) {
                throw new RuntimeException("Board not found: " + boardId);
            }

            List<String> userIdsList = (List<String>) document.get("userIds");
            if(userIdsList == null || !userIdsList.contains(userId)) {
                throw new RuntimeException("Access denied");
            }

            Map<String, Object> updates = new HashMap<>();
            if(request.getName() != null && !request.getName().trim().isEmpty()) {
                updates.put("name", request.getName().trim());
            }
            if(request.getDescription() != null) {
                updates.put("description", request.getDescription().trim());
            }
            updates.put("updatedAt", Instant.now().toString());

            // Updating
            ApiFuture<WriteResult> updateFuture = docRef.update(updates);
            updateFuture.get();

            ApiFuture<DocumentSnapshot> updatedFuture = docRef.get();
            DocumentSnapshot updatedDocument = updatedFuture.get();

            return convertDocumentToBoard(updatedDocument);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update board" + e.getMessage());
        }
    }

    // Delete board by id
    public void deleteBoardById(String boardId, String userId) throws InterruptedException {
        deleteBoardFromFireStore(boardId, userId);
    }

    private void deleteBoardFromFireStore(String boardId, String userId) throws InterruptedException {
        try {
            Firestore db = getFirestore();
            DocumentReference docRef = db.collection(BOARD_COLLECTION).document(boardId);

            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if(!document.exists()) {
                throw new RuntimeException("Board not found: " + boardId);
            }

            List<String> userIdsList = (List<String>) document.get("userIds");
            if(userIdsList == null || !userIdsList.contains(userId)) {
                throw new RuntimeException("Access denied");
            }

            ApiFuture<WriteResult> deleteFuture = docRef.delete();
            deleteFuture.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete board" + e.getMessage());
        }
    }

    // Helper method convert document to object
    private Board convertDocumentToBoard(DocumentSnapshot document) {
        Board board = new Board();
        board.setId(document.getString("id"));
        board.setName(document.getString("name"));
        board.setDescription(document.getString("description"));

        List<String> userIds = (List<String>) document.get("userId");
        board.setUserIds(userIds);

        String createdAtStr = document.getString("createdAt");
        String updatedAtStr = document.getString("updatedAt");

        if(createdAtStr != null) {
            board.setCreatedAt(Instant.parse(createdAtStr));
        }
        if(updatedAtStr != null) {
            board.setUpdatedAt(Instant.parse(updatedAtStr));
        }
        return board;
    }
}
