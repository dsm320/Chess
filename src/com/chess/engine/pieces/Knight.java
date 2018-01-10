package com.chess.engine.pieces;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Tile;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.MajorMove;
import com.google.common.collect.ImmutableList;

import static com.chess.engine.board.Move.*;

/**A concrete class to represent a knight that extends Piece*/
public class Knight extends Piece {

    //field with information on potential destinations
    private final static int[] CANDIDATE_MOVE_COORDINATES = {-17, -15, -10, -6, 6, 10, 15, 17};

    /**A constructor that creates a Knight object. */
    public Knight(final int piecePosition, final Alliance pieceAlliance) { super(PieceType.KNIGHT, piecePosition, pieceAlliance, true); }

    /**A constructor that creates a Knight object. */
    public Knight(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) { super(PieceType.KNIGHT, piecePosition, pieceAlliance, isFirstMove); }

    /**A method that implements the abstract calculateLegalMoves method from Piece. */
    @Override
    public Collection<Move> calculateLegalMoves(final Board board){

        //a list of all legal moves as determined by the method
        final List<Move> legalMoves = new ArrayList<>();

        //loops through each potential move
        for(final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES){

            //determines destination coordinate based on current position and piece specific move algorithm
            final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;

            //checks if the destination coordinate is on the board
            if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){

                //checks if special consideration must be given to the piece based on its column location
                if(isFirstColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                        isSecondColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                        isSeventhColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                        isEighthColumnExclusion(this.piecePosition, currentCandidateOffset)){
                    continue;
                }

                //determines if the move will be attacking or non-attacking
                final Tile candidateTile = board.getTile(candidateDestinationCoordinate);
                if(!candidateTile.isTileOccupied()){
                    legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                } else {
                    final Piece pieceAtDestination = candidateTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
                    if(this.pieceAlliance != pieceAlliance){
                        legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                    }
                }

            }
        }

        //returns list of possible moves
        return ImmutableList.copyOf(legalMoves);
    }

    /**A method that determines if special consideration must be given to a piece in the first column. */
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.FIRST_FILE.get(currentPosition) && (candidateOffset == -17 || candidateOffset == -10 ||
                candidateOffset == 6 || candidateOffset == 15);
    }

    /**A method that determines if special consideration must be given to a piece in the second column. */
    private static boolean isSecondColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.SECOND_FILE.get(currentPosition) && (candidateOffset == -10 || candidateOffset == 6);
    }

    /**A method that determines if special consideration must be given to a piece in the seventh column. */
    private static boolean isSeventhColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.SEVENTH_FILE.get(currentPosition) && (candidateOffset == -6 || candidateOffset == 10);
    }

    /**A method that determines if special consideration must be given to a piece in the eighth column. */
    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.EIGHTH_FILE.get(currentPosition) && (candidateOffset == -15 || candidateOffset == -6 ||
                candidateOffset == 10 || candidateOffset == 17);
    }

    /**A method that returns a letter representing the piece type. */
    @Override
    public String toString(){
        return PieceType.KNIGHT.toString();
    }

    /**A method that implements the abstract movePiece method in Piece. */
    @Override
    public Knight movePiece(final Move move) {
        return new Knight(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }

}
