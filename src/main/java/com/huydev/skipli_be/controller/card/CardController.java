package com.huydev.skipli_be.controller.card;

import com.huydev.skipli_be.config.JwtUtils;
import com.huydev.skipli_be.dto.request.CardCreationRequest;
import com.huydev.skipli_be.dto.response.CardCreationResponse;
import com.huydev.skipli_be.service.card.CardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        log.info("Card userId: " + userId);
        CardCreationResponse cardCreationResponse = cardService.createCard(boardId, cardCreationRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(cardCreationResponse);
    }

    private String getUserIdFromAuthorizationToken(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "").trim();
        return jwtUtils.extractUserId(token);
    }
}
