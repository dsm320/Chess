package com.chess.engine.pieces;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.google.common.collect.ImmutableList;

import static com.chess.engine.board.Move.*;

/**A class to represent a pawn that extends Piece. */
public class Pawn extends Piece{

    //field with information on potential destinations
    private static final int[] CANDIDATE_MOVE_COORDINATES = {7, 8, 9, 16};

    /**A constructor that creates a Pawn object. */
    public Pawn(final int piecePosition, final Alliance pieceAlliance) { super(PieceType.PAWN, piecePosition, pieceAlliance, true); }

    /**A constructor that creates a Pawn object. */
    public Pawn(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) { super(PieceType.PAWN, piecePosition, pieceAlliance, isFirstMove); }

    /**A method that implements the abstract calculateLegalMoves method from Piece. */
    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {

        //a list of all legal moves as determined by the method
        final List<Move> legalMoves = new ArrayList<>();

        //loops through each potential move until move is not valid
        for(final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES){

            //determines destination coordinate based on current position and piece specific move algorithm
            int candidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * currentCandidateOffset);

            //checks to see if destination coordinate is a valid coordinate
            if(!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                continue;
            }

            //moves if tile is empty and moving forward one space
            if((currentCandidateOffset == 8) && (!board.getTile(candidateDestinationCoordinate).isTileOccupied())){
                if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)){
                    legalMoves.add(new PawnPromotion(new PawnMove(board, this, candidateDestinationCoordinate)));
                } else{
                    legalMoves.add(new PawnMove(board, this, candidateDestinationCoordinate));
                }
            //moves if jumping two spaces and both spaces are empty and is first move
            } else if(currentCandidateOffset == 16 && this.isFirstMove &&
                    ((BoardUtils.SEVENTH_RANK.get(this.piecePosition) && this.getPieceAlliance().isBlack()) ||
                    (BoardUtils.SECOND_RANK.get(this.piecePosition) && this.getPieceAlliance().isWhite()))){
                final int behindCandidateDestinationCoordinate = this.piecePosition + (this.getPieceAlliance().getDirection() * 8);
                if(!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied() &&
                        !board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                    legalMoves.add(new PawnJump(board, this, candidateDestinationCoordinate));
                }
            //moves if attacking and handles edge cases
            } else if(currentCandidateOffset == 7 &&
                    !((BoardUtils.EIGHTH_FILE.get(piecePosition) && this.pieceAlliance.isWhite()) ||
                    (BoardUtils.FIRST_FILE.get(piecePosition) && this.pieceAlliance.isBlack()))){
                if(board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if(this.pieceAlliance != pieceOnCandidate.getPieceAlliance()){
                        if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)){
                            legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate)));
                        } else{
                            legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                } else if(board.getEnPassantPawn() != null && board.getEnPassantPawn().getPiecePosition() ==
                        (this.piecePosition + (this.getPieceAlliance().getOppositeDirection()))){
                    if(board.getEnPassantPawn().getPiecePosition() == (this.getPiecePosition() + (this.getPieceAlliance().getOppositeDirection()))){
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        if(this.pieceAlliance != pieceOnCandidate.getPieceAlliance()){
                            if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)){
                                legalMoves.add(new PawnPromotion(new PawnEnPassantAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate)));
                            } else{
                                legalMoves.add(new PawnEnPassantAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                            }
                        }
                    }
                }
             //moves if attacking and handles edge cases
            } else if(currentCandidateOffset == 9 &&
                    !((BoardUtils.EIGHTH_FILE.get(piecePosition) && this.pieceAlliance.isBlack()) ||
                    (BoardUtils.FIRST_FILE.get(piecePosition) && this.pieceAlliance.isWhite()))){
                if(board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if(this.pieceAlliance != pieceOnCandidate.getPieceAlliance()){
                        if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)){
                            legalMoves.add(new PawnPromotion(new PawnEnPassantAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate)));
                        } else{
                            legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                } else if(board.getEnPassantPawn() != null){
                    if (board.getEnPassantPawn().getPiecePosition() == (this.getPiecePosition() - (this.getPieceAlliance().getOppositeDirection()))){
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()){
                            if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)){
                                legalMoves.add(new PawnPromotion(new PawnEnPassantAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate)));
                            } else{
                                legalMoves.add(new PawnEnPassantAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                            }
                        }
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    public Piece getPromotionPiece(){
        //TODO -- change to promote to something other than queen
        return new Queen(this.piecePosition, this.getPieceAlliance(), false);
    }

    /**A method that returns a letter representing the piece type. */
    @Override
    public String toString(){
        return PieceType.PAWN.toString();
    }

    /**A method that implements the abstract movePiece method in Piece. */
    @Override
    public Pawn movePiece(final Move move) {
        return new Pawn(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }

}
