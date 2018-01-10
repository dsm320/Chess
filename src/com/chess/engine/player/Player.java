package com.chess.engine.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.MoveStatus;
import com.chess.engine.board.MoveTransition;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**An abstract class that represents a generic player.*/
public abstract class Player {

    //fields with information important to each player
    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    private final boolean isInCheck;

    /**A constructor to be called by concrete sub-classes to instantiate themselves. */
    Player(final Board board, final Collection<Move> legalMoves, final Collection<Move> opponentMoves){
        this.board = board;
        this.playerKing = establishKing();
        this.legalMoves = ImmutableList.copyOf(Iterables.concat(legalMoves, calculateKingCastles(legalMoves, opponentMoves)));
        //tests to see if any opponent moves are attacking the king
        this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(), opponentMoves).isEmpty();
    }

    /**A method that returns the player king. */
    public King getPlayerKing() {
        return playerKing;
    }

    /**A method that returns all legal moves a player can make. */
    public Collection<Move> getLegalMoves(){
        return legalMoves;
    }

    /**A method that calculates all possible attacking moves. */
    protected static Collection<Move> calculateAttacksOnTile(int piecePosition, Collection<Move> opponentMoves) {
        final List<Move> attackMoves = new ArrayList<>();
        for(final Move move : opponentMoves){
            if(piecePosition == move.getDestinationCoordinate()){
                attackMoves.add(move);
            }
        }
        return ImmutableList.copyOf(attackMoves);
    }

    /**A method that ensures a king is on the board. */
    private King establishKing() {
        for(final Piece piece : this.getActivePieces()){
            if(piece.getPieceType().isKing()){
                return (King) piece;
            }
        }
        throw new RuntimeException("Invalid board. " + this.getAlliance() + " has no king.");
    }

    /**A method that returns true if a move is legal. */
    public boolean isMoveLegal(final Move move){
        return !(move.isCastlingMove() && isInCheck()) && this.legalMoves.contains(move);
    }

    /**A method that returns true if the king is in check. */
    public boolean isInCheck(){
        return this.isInCheck;
    }

    /**A method that returns true if the king is in checkmate. */
    public boolean isInCheckmate(){
        return this.isInCheck && !hasEscapeMoves();
    }

    /**A method that returns true if the king is in stalemate. */
    public boolean isInStalemate(){
        return !this.isInCheck && !hasEscapeMoves();
    }

    /**A method that calculates if the king can escape check. */
    protected boolean hasEscapeMoves(){
        for(final Move move : this.legalMoves){
            final MoveTransition transition = makeMove(move);
            if(transition.getMoveStatus().isDone()){
                return true;
            }
        }
        return false;
    }

    /**A method that returns true if the player has already castled. */
    public boolean isCastled(){
        return this.playerKing.isCastled();
    }

    /**A method that returns true if the player is capable of performing a king side castle. */
    public boolean isKingSideCastleCapable(){
        return this.playerKing.isKingSideCastleCapable();
    }

    /**A method that returns true if the player is capable of performing a queen side castle. */
    public boolean isQueenSideCastleCapable(){
        return this.playerKing.isQueenSideCastleCapable();
    }

    /**A method that returns a MoveTransition after making a move. */
    public MoveTransition makeMove(final Move move){

        //if move is illegal returns same board
        if(!isMoveLegal(move)){
            return new MoveTransition(this.board, this.board, move, MoveStatus.ILLEGAL_MOVE);
        }

        final Board transitionBoard = move.execute();
        final Collection<Move> kingAttacks = Player.calculateAttacksOnTile(transitionBoard.currentPlayer().getOpponent().getPlayerKing().getPiecePosition(),
                transitionBoard.currentPlayer().getLegalMoves());

        //if move exposes king to check returns same board
        if(!kingAttacks.isEmpty()){
            return new MoveTransition(this.board, transitionBoard, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }

        //if can make move returns MoveTransition with new board after move has been executed
        return new MoveTransition(this.board, transitionBoard, move, MoveStatus.DONE);
    }

    /**A method that returns a Collection of all active pieces of a player. */
    public abstract Collection<Piece> getActivePieces();

    /**A method that returns the alliance of a player. */
    public abstract Alliance getAlliance();

    /**A method that returns the opponent player of a Player. */
    public abstract Player getOpponent();

    /**A method that returns all king castling moves based on player and opponent moves. */
    public abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegal, Collection<Move> opponentLegals);

}
