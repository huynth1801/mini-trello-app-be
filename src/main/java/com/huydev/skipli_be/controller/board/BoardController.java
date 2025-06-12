package com.huydev.skipli_be.controller.board;

import com.huydev.skipli_be.config.JwtUtils;
import com.huydev.skipli_be.dto.request.BoardCreationRequest;
import com.huydev.skipli_be.dto.response.BoardCreationResponse;
import com.huydev.skipli_be.service.board.BoardService;
import jakarta.validation.Valid;
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
public class BoardController {
    private final BoardService boardService;
    private final JwtUtils jwtUtils;

    @PostMapping
    public ResponseEntity<BoardCreationResponse> createBoard(@RequestBody @Valid BoardCreationRequest boardCreationRequest, @RequestHeader("Authorization") String authorizationHeader) {
        String userId = getUserIdFromAuthorizationToken(authorizationHeader);
        BoardCreationResponse boardCreationResponse = boardService.createBoard(boardCreationRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(boardCreationResponse);
    }

    @GetMapping
    public ResponseEntity<List<BoardCreationResponse>> getAllBoards(@RequestHeader("Authorization") String authorizationHeader) throws InterruptedException {
        String userId = getUserIdFromAuthorizationToken(authorizationHeader);
        List<BoardCreationResponse> boards = boardService.getAllBoardsByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(boards);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardCreationResponse> getBoardById(@PathVariable("id") String id, @RequestHeader("Authorization") String authorizationHeader) throws InterruptedException {
        String userId = getUserIdFromAuthorizationToken(authorizationHeader);
        BoardCreationResponse board = boardService.getBoardById(id, userId);
        return ResponseEntity.status(HttpStatus.OK).body(board);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoardCreationResponse> updateBoardById(@PathVariable("id") String id, @RequestBody @Valid BoardCreationRequest boardCreationRequest, @RequestHeader("Authorization") String authorizationHeader) throws InterruptedException {
        String userId = getUserIdFromAuthorizationToken(authorizationHeader);
        BoardCreationResponse boardCreationResponse = boardService.updateBoardById(id, boardCreationRequest, userId);
        return ResponseEntity.status(HttpStatus.OK).body(boardCreationResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoardById(@PathVariable("id") String id, @RequestHeader("Authorization") String authorizationHeader) throws InterruptedException {
        String userId = getUserIdFromAuthorizationToken(authorizationHeader);
        boardService.deleteBoardById(id, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private String getUserIdFromAuthorizationToken(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "").trim();
        return jwtUtils.extractUserId(token);
    }
}
