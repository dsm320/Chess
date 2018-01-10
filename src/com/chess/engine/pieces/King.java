package com.chess.engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;

import static com.chess.engine.board.Move.*;

/**A class to represent a king that extends Piece. */
public class King extends Piece {

    //field with information on potential destinations
    private static final int[] CANDIDATE_MOVE_COORDINATE = {-9, -8, -7, -1, 1, 7 , 8, 9};

    private final boolean isCastled;
    private final boolean kingSideCastleCapable;
    private final boolean queenSideCastleCapable;

    /**A constructor that creates a King object. */
    public King(final int piecePosition, final Alliance pieceAlliance, final boolean kingSideCastleCapable, final boolean queenSideCastleCapable) {
        super(PieceType.KING, piecePosition, pieceAlliance, true);
        this.isCastled = false;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
    }

    /**A constructor that creates a King object. */
    public King(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove, final boolean isCastled, final boolean kingSideCastleCapable, final boolean queenSideCastleCapable){
        super(PieceType.KING, piecePosition, pieceAlliance, isFirstMove);
        this.isCastled = isCastled;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
    }

    /**A method that returns true if the king has been castled. */
    public boolean isCastled(){
        return this.isCastled;
    }

    /**A method that returns true if is king side castle capable. */
    public boolean isKingSideCastleCapable(){
        return this.kingSideCastleCapable;
    }

    /**A method that returns true if the king is queen side castle capable. */
    public boolean isQueenSideCastleCapable(){
        return this.queenSideCastleCapable;
    }

    /**A method that implements the abstract calculateLegalMoves method from Piece. */
    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {

        //a list of all legal moves as determined by the method
        final List<Move> legalMoves = new ArrayList<>();

        //loops through each potential move
        for(final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATE){

            //determines destination coordinate based on current position and piece specific move algorithm
            final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;

            //checks if special consideration must be given to the piece based on its column location
            if(isFirstColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                    isEighthColumnExclusion(this.piecePosition, currentCandidateOffset)){
                continue;
            }

            //checks destination coordinate is on the board
            if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                final Tile candidateTile = board.getTile(candidateDestinationCoordinate);
                //determines if move is attacking or non-attacking
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
        return ImmutableList.copyOf(legalMoves);
    }

    /**A method that determines if special consideration must be given to a piece in the first column. */
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.FIRST_FILE.get(currentPosition) && (candidateOffset == -9 || candidateOffset == -1 ||
                candidateOffset == 7);
    }

    /**A method that determines if special consideration must be given to a piece in the eighth column. */
    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.EIGHTH_FILE.get(currentPosition) && (candidateOffset == -7 || candidateOffset == 1 ||
                candidateOffset == 9);
    }

    /**A method that returns a letter representing the piece type. */
    @Override
    public String toString(){
        return PieceType.KING.toString();
    }

    /**A method that implements the abstract movePiece method in Piece. */
    @Override
    public King movePiece(final Move move) {
        return new King(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance(), false, move.isCastlingMove(), false, false);
    }

}
