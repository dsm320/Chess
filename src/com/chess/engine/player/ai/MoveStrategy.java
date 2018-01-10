package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

/**An interface that represent multiply move strategies for the ai. */
public interface MoveStrategy{

    /**A method that executes a move. */
    Move execute(Board board);

    /**A method that returns the number of boards evaluated. */
    long getNumBoardsEvaluated();
}

