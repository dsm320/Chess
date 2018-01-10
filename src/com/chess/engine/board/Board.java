package com.chess.engine.board;

import java.util.*;

import com.chess.engine.Alliance;
import com.chess.engine.pieces.*;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**A class to represent a chess board. */
public class Board {

    //fields with information important to a chess board object
    private final List<Tile> gameBoard;
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;

    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;

    private final Pawn enPassantPawn;

    /**A constructor to create a Board object. */
    private Board(final Builder builder){
        this.gameBoard = createGameBoard(builder);
        this.whitePieces = calculateActivePieces(this.gameBoard, Alliance.WHITE);
        this.blackPieces = calculateActivePieces(this.gameBoard, Alliance.BLACK);

        this.enPassantPawn = builder.enPassantPawn;

        final Collection<Move> whiteStandardLegalMoves = calculateLegalMoves(this.whitePieces);
        final Collection<Move> blackStandardLegalMoves = calculateLegalMoves(this.blackPieces);

        this.whitePlayer = new WhitePlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
        this.blackPlayer = new BlackPlayer(this, blackStandardLegalMoves, whiteStandardLegalMoves);
        this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.whitePlayer, this.blackPlayer);
    }

    /**A method to return the Tile object at a given coordinate. */
    public Tile getTile(final int tileCoordinate){
        return gameBoard.get(tileCoordinate);
    }

    /**A method that returns black pieces on the board. */
    public Collection<Piece> getBlackPieces(){
        return this.blackPieces;
    }

    /**A method that returns white pieces on the board. */
    public Collection<Piece> getWhitePieces(){
        return this.whitePieces;
    }

    /**A method that returns all active pieces on the board. */
    public Iterable<Piece> getAllPieces() {
        return Iterables.unmodifiableIterable(Iterables.concat(this.whitePieces, this.blackPieces));
    }

    /**A method that returns the white player on the board. */
    public Player whitePlayer() {
        return this.whitePlayer;
    }

    /**A method that returns the black player on the board. */
    public Player blackPlayer() {
        return this.blackPlayer;
    }

    /**A method that returns the current player on the board. */
    public Player currentPlayer() {
        return this.currentPlayer;
    }

    /**A method that returns the en passant pawn on a board if there is one. */
    public Pawn getEnPassantPawn(){
        return this.enPassantPawn;
    }

    /**A method that returns the game board. */
    public List<Tile> getGameBoard() {
        return this.gameBoard;
    }

    /**A method that returns all legal moves on the board. */
    Iterable<Move> getAllLegalMoves(){
        return Iterables.unmodifiableIterable(Iterables.concat(this.whitePlayer.getLegalMoves(), this.blackPlayer.getLegalMoves()));
    }

    /**A method that tracks all active pieces of a given alliance. */
    private static Collection<Piece> calculateActivePieces(final List<Tile> gameBoard, final Alliance alliance){

        final List<Piece> activePieces = new ArrayList<>();

        //loops through all tiles in gameBoard
        for(final Tile tile : gameBoard){
            if(tile.isTileOccupied()){
                final Piece piece = tile.getPiece();
                //if tile is the same color as the parameter adds it to the list of active pieces
                if(piece.getPieceAlliance() == alliance){
                    activePieces.add(piece);
                }
            }
        }
        return ImmutableList.copyOf(activePieces);
    }

    /**A method that calculates all legal moves for a given alliance. */
    private Collection<Move> calculateLegalMoves(Collection<Piece> pieces){

        final List<Move> legalMoves = new ArrayList<>();

        //loops through each piece and adds legal moves to legalMoves list
        for(final Piece piece : pieces){
            legalMoves.addAll(piece.calculateLegalMoves(this));
        }
        return ImmutableList.copyOf(legalMoves);
    }

    /**A method that returns a user friendly string representing a board object. */
    @Override
    public String toString(){

        final StringBuilder builder = new StringBuilder();

        for(int i=0; i<BoardUtils.NUM_TILES; i++){
            final String tileText = this.gameBoard.get(i).toString();
            builder.append(String.format("%3s", tileText));
            if((i+1) % BoardUtils.NUM_TILES_PER_ROW == 0){
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    /**A method that creates a list of 64 tiles to represent all tiles on a chess board. */
    private static List<Tile> createGameBoard(final Builder builder){
        final Tile[] tiles = new Tile[BoardUtils.NUM_TILES];
        for(int i=0; i<BoardUtils.NUM_TILES; i++){
            tiles[i] = Tile.createTile(i, builder.boardConfig.get(i));
        }
        return ImmutableList.copyOf(tiles);
    }

    /**A method that creates the initial board. */
    public static Board createStandardBoard(){
        final Builder builder = new Builder();
        //black layout
        builder.setPiece(new Rook(0, Alliance.BLACK));
        builder.setPiece(new Knight(1, Alliance.BLACK));
        builder.setPiece(new Bishop(2, Alliance.BLACK));
        builder.setPiece(new Queen(3, Alliance.BLACK));
        builder.setPiece(new King(4, Alliance.BLACK, true, true));
        builder.setPiece(new Bishop(5, Alliance.BLACK));
        builder.setPiece(new Knight(6, Alliance.BLACK));
        builder.setPiece(new Rook(7, Alliance.BLACK));
        builder.setPiece(new Pawn(8, Alliance.BLACK));
        builder.setPiece(new Pawn(9, Alliance.BLACK));
        builder.setPiece(new Pawn(10, Alliance.BLACK));
        builder.setPiece(new Pawn(11, Alliance.BLACK));
        builder.setPiece(new Pawn(12, Alliance.BLACK));
        builder.setPiece(new Pawn(13, Alliance.BLACK));
        builder.setPiece(new Pawn(14, Alliance.BLACK));
        builder.setPiece(new Pawn(15, Alliance.BLACK));
        //white layout
        builder.setPiece(new Pawn(48, Alliance.WHITE));
        builder.setPiece(new Pawn(49, Alliance.WHITE));
        builder.setPiece(new Pawn(50, Alliance.WHITE));
        builder.setPiece(new Pawn(51, Alliance.WHITE));
        builder.setPiece(new Pawn(52, Alliance.WHITE));
        builder.setPiece(new Pawn(53, Alliance.WHITE));
        builder.setPiece(new Pawn(54, Alliance.WHITE));
        builder.setPiece(new Pawn(55, Alliance.WHITE));
        builder.setPiece(new Rook(56, Alliance.WHITE));
        builder.setPiece(new Knight(57, Alliance.WHITE));
        builder.setPiece(new Bishop(58, Alliance.WHITE));
        builder.setPiece(new Queen(59, Alliance.WHITE));
        builder.setPiece(new King(60, Alliance.WHITE, true, true));
        builder.setPiece(new Bishop(61, Alliance.WHITE));
        builder.setPiece(new Knight(62, Alliance.WHITE));
        builder.setPiece(new Rook(63, Alliance.WHITE));
        //white to move first
        builder.setMoveMaker(Alliance.WHITE);

        return builder.build();
    }

    /**An inner class to help build a board object. */
    public static class Builder {

        //fields with information about the Builder class
        //associates a coordinate with a Piece
        final Map<Integer, Piece> boardConfig;
        //keeps track of whose turn it is to move
        Alliance nextMoveMaker;
        private Move transitionMove;
        private Pawn enPassantPawn;

        /**A constructor to create a Builder object. */
        public Builder(){
            this.boardConfig = new HashMap<>();
        }

        /**A method that builds a new Board object. */
        public Board build(){
            return new Board(this);
        }

        /**A method that adds a key-value pair to the map. */
        public Builder setPiece(final Piece piece){
            this.boardConfig.put(piece.getPiecePosition(), piece);
            return this;
        }

        /**A method that sets whose turn it is to move. */
        public Builder setMoveMaker(final Alliance nextMoveMaker){
            this.nextMoveMaker = nextMoveMaker;
            return this;
        }

        /**A method that sets a move transition. */
        Builder setMoveTransition(final Move move){
            this.transitionMove = move;
            return this;
        }

        /**A method that sets a pawn as an en passant pawn. */
        Builder setEnPassantPawn(Pawn enPassantPawn) {
            this.enPassantPawn = enPassantPawn;
            return this;
        }

    }

}
