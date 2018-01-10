package com.chess.engine.board;

import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

import static com.chess.engine.board.Board.*;

/**An abstract class to represent a generic move possibility. */
public abstract class Move {

    //fields with information on the piece being moved, the board around it, and its potential destination
    protected final Board board;
    protected final Piece movedPiece;
    protected final int destinationCoordinate;
    protected final boolean isFirstMove;

    public static final Move NULL_MOVE = new NullMove();

    /**A constructor to be called by concrete sub-classes when instantiating themselves. */
    protected Move(final Board board, final Piece movedPiece, final int destinationCoordinate){
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
        this.isFirstMove = movedPiece.isFirstMove();
    }

    private Move(final Board board, final int destinationCoordinate){
        this.board = board;
        this.destinationCoordinate = destinationCoordinate;
         this.movedPiece = null;
         this.isFirstMove = false;
    }

    /**A method that returns the board a move is being executed on. */
    public Board getBoard(){
        return this.board;
    }

    /**A method that returns the destination coordinate of a move. */
    public int getDestinationCoordinate(){
        return this.destinationCoordinate;
    }

    /**A method that returns the current coordinate of a piece being moved. */
    public int getCurrentCoordinate() {
        return this.movedPiece.getPiecePosition();
    }

    /**A method that returns the piece being moved. */
    public Piece getMovedPiece(){
        return this.movedPiece;
    }

    /**A method that defaults a base move as a non-attacking move. */
    public boolean isAttack(){
        return false;
    }

    /**A method that defaults a base move as a non-castling move. */
    public boolean isCastlingMove() {
        return false;
    }

    /**A method that defaults a base move piece getting attacked as null. */
    public Piece getAttackedPiece() {
        return null;
    }

    /**A method that returns the hash code of a Move object. */
    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;
        result = prime * result + this.destinationCoordinate;
        result = prime * result + this.movedPiece.hashCode();
        result = prime * result + this.movedPiece.getPiecePosition();
        return result;
    }

    /**A method that returns true if two moves are equivalent. */
    @Override
    public boolean equals(final Object obj){
        if(obj instanceof Move){
            Move otherMove = (Move) obj;
            return getCurrentCoordinate() == otherMove.getCurrentCoordinate() &&
                    getDestinationCoordinate() == otherMove.getDestinationCoordinate() &&
                    getMovedPiece().equals(otherMove.getMovedPiece());
        }
        return false;
    }

    /**A method that implements the abstract method execute in Move. */
    public Board execute(){

        //builds a new board
        final Builder builder = new Builder();
        //for all of pieces that aren't the moved piece place in same position on new board
        for(final Piece piece : this.board.currentPlayer().getActivePieces()){
            if(!this.movedPiece.equals(piece)){
                builder.setPiece(piece);
            }
        }
        //does the same thing for the opponent's pieces
        for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()){
            builder.setPiece(piece);
        }
        //set the piece for the moved piece (move the moved piece)
        builder.setPiece(this.movedPiece.movePiece(this));
        //set the move maker for the new board to the opponent
        builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
        //sets the new board
        builder.setMoveTransition(this);

        return builder.build();
    }

    /**A concrete sub-class of Move that represents a non-attacking move. */
    public static final class MajorMove extends Move{

        /**A constructor that creates a MajorMove object. */
        public MajorMove(final Board board,	final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public boolean equals(final Object obj){
            return this == obj || obj instanceof MajorMove && super.equals(obj);
        }

        @Override
        public String toString(){
            return movedPiece.getPieceType().toString() + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    /**A concrete sub-class of Move that represents an attacking move. */
    public static class AttackMove extends Move{

        //field with information on the piece being attacked
        final Piece attackedPiece;

        /**A constructor that creates an AttackMove object. */
        public AttackMove(final Board board, final Piece movedPiece, final int destinationCoordinate, final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }

        /**A method that overrides the isAttack method from move to be true. */
        @Override
        public boolean isAttack(){
            return true;
        }

        /**A method that returns the piece being attacked. */
        @Override
        public Piece getAttackedPiece(){
            return this.attackedPiece;
        }

        /**A method that returns an AttackPiece's hashcode. */
        @Override
        public int hashCode(){
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        /**A method that determines if two attack moves are equivalent. */
        @Override
        public boolean equals(final Object obj){
            if(obj instanceof AttackMove){
                AttackMove otherAttackMove = (AttackMove) obj;
                return super.equals(otherAttackMove) && getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
            }
            return false;
        }
    }

    /**A sub-class of AttackMove to represent attacking moves made by major pieces. */
    public static class MajorAttackMove extends AttackMove{

        /**A constructor that creates a MajorAttackMove object. */
        public MajorAttackMove(final Board board, final Piece pieceMoved, final int destinationCoordinate, final Piece pieceAttacked){
            super(board, pieceMoved, destinationCoordinate, pieceAttacked);
        }

        /**A method that determines if two major attack moves are equivalent. */
        @Override
        public boolean equals(final Object obj){
            return this == obj || obj instanceof MajorAttackMove && super.equals(obj);
        }

        /**A method that returns a simple PGN string representation of the move made. */
        @Override
        public String toString(){
            return movedPiece.getPieceType() + "x" + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }

    }

    /**A concrete sub-class of Move that represents a non-attacking pawn move. */
    public static final class PawnMove extends Move{

        /**A constructor that creates a PawnMove object. */
        public PawnMove(final Board board,	final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        /**A method that returns a simple String representation of a move. */
        @Override
        public String toString(){
            return BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }

        @Override
        public boolean equals(final Object obj){
            return this == obj || obj instanceof PawnMove && super.equals(obj);
        }

    }

    /**A concrete sub-class of Move that represents a special non-attacking jump pawn move. */
    public static final class PawnJump extends Move{

        /**A constructor that creates a PawnJump object. */
        public PawnJump(final Board board,	final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        /**A method that overrides the method execute in Move. */
        @Override
        public Board execute(){
            final Builder builder = new Builder();
            for(final Piece piece : this.board.currentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }
            final Pawn movedPawn = (Pawn) this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            builder.setMoveTransition(this);
            return builder.build();
        }

        /**A method that returns a simple String representation of a move. */
        @Override
        public String toString(){
            return BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }

    }

    /**A concrete sub-class of AttackMove that represents an attacking pawn move. */
    public static class PawnAttackMove extends AttackMove{

        /**A constructor that creates a PawnAttackMove object. */
        public PawnAttackMove(final Board board, final Piece movedPiece, final int destinationCoordinate, final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(final Object obj){
            return this == obj || obj instanceof PawnAttackMove && super.equals(obj);
        }

        @Override
        public String toString(){
            return BoardUtils.getPositionAtCoordinate(this.movedPiece.getPiecePosition()).substring(0,1) + "x" +
                    BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }

    }

    /**A concrete sub-class of PawnAttackMove that represents a special en passant attacking pawn move. */
    public static final class PawnEnPassantAttackMove extends PawnAttackMove{

        /**A constructor that creates a PawnEnPassantAttackMove object. */
        public PawnEnPassantAttackMove(final Board board, final Piece movedPiece, final int destinationCoordinate, final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }

        /**A method that determines if two pawn en passant attacks are equivalent. */
        @Override
        public boolean equals(final Object obj){
            return this == obj || obj instanceof PawnEnPassantAttackMove && super.equals(obj);
        }

        /**A method that overrides the execute method in move. */
        @Override
        public Board execute(){
            final Builder builder = new Builder();
            for(final Piece piece : this.board.currentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()){
                if(!piece.equals(this.getAttackedPiece())){
                    builder.setPiece(piece);
                }
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }

    /**A concrete sub-class of Move that represents a pawn promotion move. */
    public static class PawnPromotion extends Move{

        //fields
        final Move decoratedMove;
        final Pawn promotedPawn;

        /**A constructor that creates a PawnPromotion object. */
        public PawnPromotion(final Move decoratedMove){
            super(decoratedMove.getBoard(), decoratedMove.getMovedPiece(), decoratedMove.getDestinationCoordinate());
            this.decoratedMove = decoratedMove;
            this.promotedPawn = (Pawn)decoratedMove.getMovedPiece();
        }

        /**A method that overrides the execute method in Move. */
        @Override
        public Board execute(){
            final Board pawnMovedBoard = this.decoratedMove.execute();
            final Builder builder = new Builder();
            for(final Piece piece : pawnMovedBoard.currentPlayer().getActivePieces()){
                if(!this.promotedPawn.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : pawnMovedBoard.currentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }
            builder.setPiece(this.promotedPawn.getPromotionPiece().movePiece(this));
            builder.setMoveMaker(pawnMovedBoard.currentPlayer().getAlliance());
            builder.setMoveTransition(this);
            return builder.build();
        }

        /**A method that returns true if the pawn is attacking into a promotion. */
        @Override
        public boolean isAttack() {
            return this.decoratedMove.isAttack();
        }

        /**A method that returns the piece the pawn is attacking if available. */
        @Override
        public Piece getAttackedPiece(){
            return  this.decoratedMove.getAttackedPiece();
        }

        /**A method that returns a simple String representation of a pawn promotion move. */
        @Override
        public String toString(){
            return "";
        }

        /**A method that calculates the hashcode of a pawn promotion. */
        @Override
        public int hashCode(){
            return this.decoratedMove.hashCode() + (31 * promotedPawn.hashCode());
        }

        /**A method that determines if two pawn promotions are equivalent. */
        @Override
        public boolean equals(final Object obj){
            return this == obj || obj instanceof PawnPromotion && super.equals(obj);
        }

    }

    /**An abstract sub-class of Move that represents a castling move. */
    static abstract class CastleMove extends Move{

        protected final Rook castleRook;
        protected final int castleRookStartCoordinate;
        protected final int castleRookDestinationCoordinate;

        /**A constructor that creates a CastleMove object. */
        public CastleMove(final Board board, final Piece movedPiece, final int destinationCoordinate, final Rook castleRook,
                          final int castleRookStartCoordinate, final int castleRookDestinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
            this.castleRook = castleRook;
            this.castleRookStartCoordinate = castleRookStartCoordinate;
            this.castleRookDestinationCoordinate = castleRookDestinationCoordinate;
        }

        /**A method that returns the castling rook. */
        public Rook getCastleRook(){
            return this.castleRook;
        }

        /**A method that overrides the isCastlingMove method in Move to return true. */
        @Override
        public boolean isCastlingMove(){
            return true;
        }

        /**A method that overrides the method execute in Move. */
        @Override
        public Board execute(){

            final Builder builder = new Builder();
            for(final Piece piece : this.board.currentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece) && !this.castleRook.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setPiece(new Rook(this.castleRookDestinationCoordinate, this.castleRook.getPieceAlliance()));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            builder.setMoveTransition(this);
            return builder.build();
        }

        /**A method that calculates the hashcode for a castle move. */
        @Override
        public int hashCode(){
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + this.castleRook.hashCode();
            result = prime * result + this.castleRookDestinationCoordinate;
            return result;
        }

        /**A method that determines if two castle moves are equivalent. */
        @Override
        public boolean equals(final Object obj){
            if(obj instanceof CastleMove){
                CastleMove otherMove = (CastleMove) obj;
                return super.equals(otherMove) && this.castleRook.equals(otherMove.getCastleRook());
            }
            return false;
        }

    }

    /**A concrete sub-class of CastleMove that represents a king side castling move. */
    public static final class KingSideCastleMove extends CastleMove{

        /**A constructor that creates a KingSideCastleMove object. */
        public KingSideCastleMove(final Board board, final Piece movedPiece, final int destinationCoordinate, final Rook castleRook,
                                  final int castleRookStartCoordinate, final int castleRookDestinationCoordinate) {
            super(board, movedPiece, destinationCoordinate, castleRook, castleRookStartCoordinate, castleRookDestinationCoordinate);
        }

        /**A method that returns the PGN convention for a king side castle. */
        @Override
        public String toString(){
            return "O-O";
        }

        /**A method that determines if two king side castle moves are equal. */
        @Override
        public boolean equals(final Object obj){
            return this == obj || obj instanceof KingSideCastleMove && super.equals(obj);
        }

    }

    /**A concrete sub-class of CastleMove that represents a queen side castling move. */
    public static final class QueenSideCastleMove extends CastleMove{

        /**A constructor that creates a QueenSideCastleMove object. */
        public QueenSideCastleMove(final Board board, final Piece movedPiece, final int destinationCoordinate, final Rook castleRook,
                                   final int castleRookStartCoordinate, final int castleRookDestinationCoordinate) {
            super(board, movedPiece, destinationCoordinate, castleRook, castleRookStartCoordinate, castleRookDestinationCoordinate);
        }

        /*A method that returns the PGN convention for a queen side castle.*/
        @Override
        public String toString(){
            return "O-O-O";
        }

        /**A method that determines if two queen side castle moves are equal. */
        @Override
        public boolean equals(final Object obj){
            return this == obj || obj instanceof KingSideCastleMove && super.equals(obj);
        }
    }

    /**A concrete sub-class of Move that represents a null move. */
    public static final class NullMove extends Move{

        /**A constructor that creates a NullMove object. */
        public NullMove() {
            super(null, 65);
        }

        /**A method that overrides the execute method to throw a runtime exception. */
        @Override
        public Board execute() {
            throw new RuntimeException("Cannot execute the null move.");
        }

        @Override
        public int getCurrentCoordinate(){
            return -1;
        }
    }

    /**A class to help create new Move objects. */
    public static class MoveFactory {

        /**A constructor to create a new MoveFactory object but throws a runtime exception because the class cannot be instantiated. */
        private MoveFactory(){
            throw new RuntimeException("You cannot instantiate this class!");
        }

        /**A method that creates a new Move given a board, a start coordinate, and a destination coordinate. */
        public static Move createMove(final Board board, final int currentCoordinate, final int destinationCoordinate){
            for(final Move move : board.getAllLegalMoves()){
                if(move.getCurrentCoordinate() == currentCoordinate &&
                        move.getDestinationCoordinate() == destinationCoordinate){
                    return move;
                }
            }
            return NULL_MOVE;
        }
    }

}
