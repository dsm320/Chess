package com.chess.engine.player.ai;

import com.chess.engine.board.Board;

/**An interface that represents multiple ways to evaluate who is winning on a given chess board. */
public interface BoardEvaluator{

    /**A method that returns who is winning in on a given chess board. */
    int evaluate(Board board, int depth);

}
