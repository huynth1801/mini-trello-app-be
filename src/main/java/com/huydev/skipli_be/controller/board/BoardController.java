package com.huydev.skipli_be.controller.board;

import com.huydev.skipli_be.dto.request.BoardCreationRequest;
import com.huydev.skipli_be.dto.response.BoardCreationResponse;
import com.huydev.skipli_be.entity.Board;
import com.huydev.skipli_be.service.board.BoardService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/boards")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class BoardController {
    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<BoardCreationResponse> createBoard(@RequestBody @Valid BoardCreationRequest boardCreationRequest) {
        BoardCreationResponse boardCreationResponse = boardService.createBoard(boardCreationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(boardCreationResponse);
    }
}
