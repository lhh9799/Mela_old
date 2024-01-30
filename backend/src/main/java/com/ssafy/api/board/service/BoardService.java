package com.ssafy.api.board.service;

import com.ssafy.api.board.request.BoardUpdatePutReq;
import com.ssafy.api.board.request.BoardRegisterPostReq;
import com.ssafy.db.entity.Board;
import com.ssafy.db.entity.User;

import java.util.List;

public interface BoardService {
    void registBoard(BoardRegisterPostReq registInfo, User user);
    void updateBoard(BoardUpdatePutReq updateInfo, Long boardIdx, Long userIdx);
    void deleteBoard(Long boardIdx);
    Board getBoard(Long boardIdx) throws Exception;
    List<Board> getBoardList();
}
