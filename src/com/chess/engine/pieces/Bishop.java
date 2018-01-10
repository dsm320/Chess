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

/**A concrete class to represent a bishop that extends Piece. */
public class Bishop extends Piece{

    //field with information on potential destinations
    private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES = {-9, -7, 7 ,9};

    /**A constructor that creates a Bishop object. */
    public Bishop(final int piecePosition, final Alliance pieceAlliance) { super(PieceType.BISHOP, piecePosition, pieceAlliance, true); }

    /**A constructor that creates a Bishop object. */
    public Bishop(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) { super(PieceType.BISHOP, piecePosition, pieceAlliance, isFirstMove); }

    /**A method that implements the abstract calculateLegalMoves method from Piece. */
    @Override
    public Collection<Move> calculateLegalMoves(final Board board){

        //a list of all legal moves as determined by the method
        final List<Move> legalMoves = new ArrayList<>();

        //loops through each potential move until move is not valid
        for(final int candidateCoordinateOffset: CANDIDATE_MOVE_VECTOR_COORDINATES){

            //sets initial potential destination to current position
            int candidateDestinationCoordinate = this.piecePosition;

            //loops for as long as the potential destination coordinate is on the board
            while(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {

                //checks if special consideration must be given to the piece based on its column location
                if (isFirstColumnExclusion(candidateDestinationCoordinate, candidateCoordinateOffset) ||
                        isEighthColumnExclusion(candidateDestinationCoordinate, candidateCoordinateOffset)){
                    break;
                }

                //determines destination coordinate based on current position and piece specific move algorithm
                candidateDestinationCoordinate += candidateCoordinateOffset;

                //checks if the destination coordinate is on the board
                if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){

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
                        break;
                    }

                }

            }

        }

        //returns list of possible moves
        return ImmutableList.copyOf(legalMoves);
    }

    /**A method that determines if special consideration must be given to a piece in the first column. */
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.FIRST_FILE.get(currentPosition) && (candidateOffset == -9 || candidateOffset == 7);
    }

    /**A method that determines if special consideration must be given to a piece in the eighth column. */
    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.EIGHTH_FILE.get(currentPosition)&& (candidateOffset == -7 || candidateOffset == 9);
    }

    /**A method that returns a letter representing the piece type. */
    @Override
    public String toString(){
        return PieceType.BISHOP.toString();
    }

    /**A method that implements the abstract movePiece method in Piece. */
    @Override
    public Bishop movePiece(final Move move) {
        return new Bishop(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }

}
