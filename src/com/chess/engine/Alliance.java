package com.chess.engine;

import com.chess.engine.board.BoardUtils;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;

/**An enum to store the two possible colors of a chess piece. */
public enum Alliance {
    WHITE {
        @Override
        public int getDirection() {
            return -1;
        }

        @Override
        public int getOppositeDirection(){ return 1; }

        @Override
        public boolean isWhite() {
            return true;
        }

        @Override
        public boolean isBlack() {
            return false;
        }

        @Override
        public boolean isPawnPromotionSquare(int position){
            return BoardUtils.EIGHTH_RANK.get(position);
        }

        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
            return whitePlayer;
        }
    },
    BLACK {
        @Override
        public int getDirection() {
            return 1;
        }

        @Override
        public int getOppositeDirection(){ return -1; }

        @Override
        public boolean isWhite() {
            return false;
        }

        @Override
        public boolean isBlack() {
            return true;
        }

        @Override
        public boolean isPawnPromotionSquare(int position){
            return BoardUtils.FIRST_RANK.get(position);
        }

        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
            return blackPlayer;
        }
    };

    /**A method that determines direction a Piece can move based on its alliance. */
    public abstract int getDirection();

    /**A method that determines the opposite direction of the directionality of a piece. */
    public abstract int getOppositeDirection();

    /**A method that returns true if Alliance is white. */
    public abstract boolean isWhite();

    /**A method that returns true if Alliance is black. */
    public abstract boolean isBlack();

    /**A method that determines if a specific tile is tile that is eligible for pawn promotion. */
    public abstract boolean isPawnPromotionSquare(int position);

    /**A method that chooses whose turn it is to move. */
    public abstract Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer);

}
