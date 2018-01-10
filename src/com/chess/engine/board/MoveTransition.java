package com.chess.engine.board;

/**A class to represent the transition from one board to another after a move is made. */
public class MoveTransition {

    //fields with information about a move transition
    private final Board fromBoard;
    private final Board toBoard;
    private final Move move;
    private final MoveStatus moveStatus;

    /**A constructor that creates a MoveTransition object. */
    public MoveTransition(final Board fromBoard, final Board toBoard, final Move move, final MoveStatus moveStatus){
        this.fromBoard = fromBoard;
        this.toBoard = toBoard;
        this.move = move;
        this.moveStatus = moveStatus;
    }

    /**A method that returns moveStatus. */
    public MoveStatus getMoveStatus(){
        return this.moveStatus;
    }

    /**A method that returns the board before the move. */
    public Board getFromBoard() {
        return this.fromBoard;
    }

    /**A method that returns the board after the move. */
    public Board getToBoard() {
        return this.toBoard;
    }

}
