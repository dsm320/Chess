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

/**A concrete class to represent a black player and that extends player. */
public class BlackPlayer extends Player {

    /**A constructor to create a BlackPlayer object. */
    public BlackPlayer(final Board board, final Collection<Move> blackLegalMoves, final Collection<Move> whiteLegalMoves) {
        super(board, blackLegalMoves, whiteLegalMoves);
    }

    /**A method that implements the abstract getActivePieces method from Player. */
    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }

    /**A method that implements the abstract getAlliance method from Player. */
    @Override
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    /**A method that implements the abstract getOpponent method from Player. */
    @Override
    public Player getOpponent() {
        return this.board.whitePlayer();
    }

    /**A method that implements the abstract calculateKingCastles method from Player. */
    @Override
    public Collection<Move> calculateKingCastles(final Collection<Move> playerLegal, final Collection<Move> opponentLegals) {
        final List<Move> kingCastles = new ArrayList<>();

        if(this.playerKing.isFirstMove() && !this.isInCheck()){
            //black king side castle
            //checks to see if tile is empty
            if(!this.board.getTile(5).isTileOccupied() && !this.board.getTile(6).isTileOccupied()){
                final Tile rookTile = this.board.getTile(7);
                //checks to see if rook's first move
                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()){
                    //checks to make sure doesn't move through check
                    if(Player.calculateAttacksOnTile(5, opponentLegals).isEmpty() &&
                            Player.calculateAttacksOnTile(6, opponentLegals).isEmpty() &&
                            rookTile.getPiece().getPieceType().isRook()) {
                        kingCastles.add(new Move.KingSideCastleMove(this.board, this.playerKing,
                                                                   6, (Rook)rookTile.getPiece(),
                                                                    rookTile.getTileCoordinate(), 5));
                    }
                }
            }
            //black queen side castle
            if(!this.board.getTile(1).isTileOccupied() && !this.board.getTile(2).isTileOccupied() &&
                    !this.board.getTile(3).isTileOccupied()){
                final Tile rookTile = this.board.getTile(0);
                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()){
                    if(Player.calculateAttacksOnTile(1, opponentLegals).isEmpty() &&
                            Player.calculateAttacksOnTile(2, opponentLegals).isEmpty() &&
                            Player.calculateAttacksOnTile(3, opponentLegals).isEmpty() &&
                            rookTile.getPiece().getPieceType().isRook()) {
                        kingCastles.add(new Move.QueenSideCastleMove(this.board, this.playerKing,
                                                                    2, (Rook)rookTile.getPiece(),
                                                                     rookTile.getTileCoordinate(), 3));
                    }
                }
            }
        }
        return ImmutableList.copyOf(kingCastles);
    }

    /**A method that returns a simple String representing the color of the player. */
    @Override
    public String toString(){
        return "Black";
    }

}
