
package com.chess.engine.pieces;

import java.util.Collection;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

/**An abstract class that represents a generic chess piece. */
public abstract class Piece {

    //fields to define information about the Piece object
    protected final PieceType pieceType;
    protected final int piecePosition;
    protected final Alliance pieceAlliance;
    protected final boolean isFirstMove;
    private final int cashedHashCode;

    /**A constructor to be called by concrete sub-classes to instantiate themselves. */
    Piece(final PieceType pieceType, final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove){
        this.pieceType = pieceType;
        this.pieceAlliance = pieceAlliance;
        this.piecePosition = piecePosition;
        //TODO -- figure out how to initialize isFirstMove
        this.isFirstMove = isFirstMove;
        this.cashedHashCode = computeHashCode();
    }

    /**A method that returns the Alliance of the Piece object. */
    public Alliance getPieceAlliance(){
        return this.pieceAlliance;
    }

    public PieceType getPieceType() {
        return this.pieceType;
    }

    /**A method that returns isFirstMove. */
    public boolean isFirstMove() {
        return this.isFirstMove;
    }

    /**A method that returns piecePosition. */
    public int getPiecePosition(){
        return this.piecePosition;
    }

    /**A method that returns the hash code of a Piece object. */
    @Override
    public int hashCode(){
        return this.cashedHashCode;
    }

    /**A method that returns true if two pieces are equivalent. */
    @Override
    public boolean equals(final Object obj){
        if(this == obj){
            return true;
        }
        if(obj instanceof Piece){
            final Piece otherPiece = (Piece) obj;
            return piecePosition == otherPiece.getPiecePosition() && pieceType == otherPiece.getPieceType() &&
                    pieceAlliance == otherPiece.getPieceAlliance() && isFirstMove == otherPiece.isFirstMove();
        }
        return false;
    }

    //look up logic
    /**A method that computes the hashcode of a Piece object. */
    private int computeHashCode() {
        int result = pieceType.hashCode();
        result = 31 * result + pieceAlliance.hashCode();
        result  = 31 * result + piecePosition;
        result = 31 * result + (isFirstMove ? 1 : 0);
        return result;
    }

    public int getPieceValue(){
        return this.pieceType.getPieceValue();
    }

    //abstract methods to be implemented in concrete sub-classes
    /**A method that determines all the potential legal moves for a given Piece object. */
    public abstract Collection<Move> calculateLegalMoves(final Board board);

    /**A method that applies a move to the current piece and returns a copy of the piece in the new position. */
    public abstract Piece movePiece(final Move move);

    /**An enum to store the possible types of pieces. */
    public enum PieceType {

        PAWN("P", 100) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        KNIGHT("N", 300) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        BISHOP("B", 300) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        ROOK("R", 500) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return true;
            }
        },
        QUEEN("Q", 900) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        KING("K", 10000) {
            @Override
            public boolean isKing() {
                return true;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        };

        //fields with information about a pieceType
        private final String pieceName;
        private final int pieceValue;

        /**A method that returns true if the piece is a king. */
        public abstract boolean isKing();

        /**A method that return true if the piece is a rook. */
        public abstract boolean isRook();

        /**A constructor that creates a PieceType object. */
        PieceType(final String pieceName, final int pieceValue){
            this.pieceName = pieceName;
            this.pieceValue = pieceValue;
        }

        public int getPieceValue(){
            return this.pieceValue;
        }

        /**A method that returns a string representing the given piece type. */
        @Override
        public String toString(){
            return this.pieceName;
        }

    }

}
