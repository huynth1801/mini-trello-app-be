package com.huydev.skipli_be.service.board;

import com.huydev.skipli_be.dto.request.BoardCreationRequest;
import com.huydev.skipli_be.dto.response.BoardCreationResponse;
import com.huydev.skipli_be.entity.Board;

import com.huydev.skipli_be.service.firebase.FirebaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {
    private final FirebaseService firebaseService;

    public BoardCreationResponse createBoard(BoardCreationRequest boardCreationRequest) {
        try {
            log.info("Create board name {} and {}", boardCreationRequest.getName(), boardCreationRequest.getDescription());
            Board board = new Board();
            board.setName(boardCreationRequest.getName());
            board.setDescription(boardCreationRequest.getDescription());

            String boardId = firebaseService.saveBoard(board);

            return BoardCreationResponse.builder()
                    .id(boardId)
                    .name(board.getName())
                    .description(board.getDescription())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create board" + e.getMessage());
        }
    }
}
