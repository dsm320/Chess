package com.chess.engine.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;
import com.google.common.collect.ImmutableList;

/**A concrete class to represent a white player and that extends player. */
public class WhitePlayer extends Player {

    /**A constructor to create a WhitePlayer object. */
    public WhitePlayer(final Board board, final Collection<Move> whiteLegalMoves, final Collection<Move> blackLegalMoves) {
        super(board, whiteLegalMoves, blackLegalMoves);
    }

    /**A method that implements the abstract getActivePieces method from Player. */
    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }

    /**A method that implements the abstract getAlliance method from Player. */
    @Override
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    /**A method that implements the abstract getOpponent method from Player. */
    @Override
    public Player getOpponent() {
        return this.board.blackPlayer();
    }

    /**A method that implements the abstract calculateKingCastles method from Player. */
    @Override
    public Collection<Move> calculateKingCastles(final Collection<Move> playerLegal, final Collection<Move> opponentLegals) {

        final List<Move> kingCastles = new ArrayList<>();

        if(this.playerKing.isFirstMove() && !this.isInCheck()){
            //whites king side castle
            //checks to see if tile is empty
            if(!this.board.getTile(61).isTileOccupied() && !this.board.getTile(62).isTileOccupied()){
                final Tile rookTile = this.board.getTile(63);
                //checks to see if rook's first move
                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()){
                    //checks to make sure doesn't move through check
                    if(Player.calculateAttacksOnTile(61, opponentLegals).isEmpty() &&
                            Player.calculateAttacksOnTile(62, opponentLegals).isEmpty() &&
                            rookTile.getPiece().getPieceType().isRook()) {
                        kingCastles.add(new Move.KingSideCastleMove(this.board, this.playerKing,
                                                                   62, (Rook)rookTile.getPiece(),
                                                                    rookTile.getTileCoordinate(), 61));
                    }
                }
            }
            //white queen side castle
            if(!this.board.getTile(59).isTileOccupied() && !this.board.getTile(58).isTileOccupied() &&
                    !this.board.getTile(57).isTileOccupied()){
                final Tile rookTile = this.board.getTile(56);
                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()){
                    if(Player.calculateAttacksOnTile(59, opponentLegals).isEmpty() &&
                            Player.calculateAttacksOnTile(58, opponentLegals).isEmpty() &&
                            Player.calculateAttacksOnTile(57, opponentLegals).isEmpty() &&
                            rookTile.getPiece().getPieceType().isRook()) {
                        kingCastles.add(new Move.QueenSideCastleMove(this.board, this.playerKing,
                                                                    58, (Rook)rookTile.getPiece(),
                                                                     rookTile.getTileCoordinate(), 59));
                    }
                }
            }
        }
        return ImmutableList.copyOf(kingCastles);
    }

    /**A method that returns a simple String representing the color of the player. */
    @Override
    public String toString(){
        return "White";
    }

}
