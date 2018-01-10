package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.Player;

/**A class that represents a standard board evaluator. */
public final class StandardBoardEvaluate implements BoardEvaluator{

    //fields
    private static final int CHECK_BONUS = 50;
    private static final int CHECKMATE_BONUS = 10000;
    private static final int DEPTH_BONUS = 100;
    private static final int CASTLED_BONUS = 60 ;

    /**A method that implements the evaluate method in BoardEvaluator. */
    @Override
    public int evaluate(final Board board, final int depth){
        return scorePlayer(board, board.whitePlayer(), depth) -
                scorePlayer(board, board.blackPlayer(), depth);
    }

    /**A method that returns a number representing the score of a player. */
    private int scorePlayer(final Board board, final Player player, final int depth){
        return pieceValue(player) + mobility(player) + check(player) + checkmate(player, depth) + castled(player);
    }

    /**A method that returns a number representing the value of all of a player's active pieces. */
    private static int pieceValue(final Player player){
        int pieceValueScore = 0;
        for(final Piece piece : player.getActivePieces()){
            pieceValueScore += piece.getPieceValue();
        }
        return pieceValueScore;
    }

    /**A method that returns a number representing the mobility of a player. */
    private static int mobility(final Player player){
        return player.getLegalMoves().size();
    }

    /**A method that returns a number representing a check bonus if the player discovers check on their opponent. */
    private static int check(final Player player){
        return player.getOpponent().isInCheck() ? CHECK_BONUS : 0;
    }

    /**A method that returns a number representing a check bonus if the player discovers check on their opponent. */
    private static int checkmate(final Player player, final int depth){
        return player.getOpponent().isInCheckmate() ? CHECKMATE_BONUS * depthBonus(depth) : 0;
    }

    /**A method that returns a number representing a bonus dependant on how early checkmate is discovered. */
    private static int depthBonus(final int depth){
        return depth == 0 ? 1 : DEPTH_BONUS * depth;
    }

    /**A method that returns a number representing a bonus dependant on if a player is castled. */
    private static int castled(final Player player) {
        return player.isCastled() ? CASTLED_BONUS : 0;
    }

}
