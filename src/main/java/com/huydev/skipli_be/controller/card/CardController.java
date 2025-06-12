package com.huydev.skipli_be.controller.card;

import com.huydev.skipli_be.config.JwtUtils;
import com.huydev.skipli_be.dto.request.CardCreationRequest;
import com.huydev.skipli_be.dto.request.CardUpdateRequest;
import com.huydev.skipli_be.dto.response.CardCreationResponse;
import com.huydev.skipli_be.dto.response.CardUserResponse;
import com.huydev.skipli_be.service.card.CardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boards")
@AllArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class CardController {
    private final CardService cardService;
    private final JwtUtils jwtUtils;

    @PostMapping("/{boardId}/cards")
    public ResponseEntity<CardCreationResponse> createCard(@PathVariable String boardId, @RequestBody CardCreationRequest cardCreationRequest
            , @RequestHeader("Authorization") String authorizationHeader) {
        String userId = getUserIdFromAuthorizationToken(authorizationHeader);
        CardCreationResponse cardCreationResponse = cardService.createCard(boardId, cardCreationRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(cardCreationResponse);
    }

    @GetMapping("/{boardId}/cards/{id}")
    public ResponseEntity<CardCreationResponse> retrieveCardDetailsById(@PathVariable String boardId, @PathVariable String id, @RequestHeader("Authorization") String authorizationHeader) {
        String userId = getUserIdFromAuthorizationToken(authorizationHeader);
        CardCreationResponse cardCreationResponse = cardService.retrieveCardById(boardId, id, userId);
        return ResponseEntity.status(HttpStatus.OK).body(cardCreationResponse);
    }

    @GetMapping("/{boardId}/cards/user/{userId}")
    public ResponseEntity<List<CardUserResponse>> getCardsByBoardAndUserId(@PathVariable String boardId, @PathVariable String userId) {
        List<CardUserResponse> cards = cardService.getCardsByBoardId(boardId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(cards);
    }

    @PutMapping("/{boardId}/cards/{id}")
    public ResponseEntity<CardCreationResponse> updateCard(@PathVariable String boardId, @PathVariable String id, @RequestBody CardUpdateRequest request, @RequestHeader("Authorization") String authorizationHeader) {
        String userId = getUserIdFromAuthorizationToken(authorizationHeader);
        CardCreationResponse cardCreationResponse = cardService.updateCardById(boardId, id, request, userId);
        return ResponseEntity.status(HttpStatus.OK).body(cardCreationResponse);
    }

    @DeleteMapping("/{boardId}/cards/{id}")
    public ResponseEntity<Void> deleteCardById(@PathVariable String boardId, @PathVariable String id, @RequestHeader("Authorization") String authorizationHeader) {
        String userId = getUserIdFromAuthorizationToken(authorizationHeader);
        cardService.deleteCardById(boardId, id, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private String getUserIdFromAuthorizationToken(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "").trim();
        return jwtUtils.extractUserId(token);
    }
}
