package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.MoveTransition;

/**A class that dictates the ai moves based on the minimax algorithm. */
public class MiniMax implements MoveStrategy{

    //fields
    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;
    private long boardsEvaluated;

    /**A constructor that creates a MiniMax object. */
    public MiniMax(final int searchDepth){
        this.boardEvaluator =  new StandardBoardEvaluate();
        this.searchDepth = searchDepth;
        this.boardsEvaluated = 0;
    }

    /**A method that returns the number of boards evaluated. */
    @Override
    public long getNumBoardsEvaluated() {
        return this.boardsEvaluated;
    }

    /**A method that returns a simple String designation for the class. */
    @Override
    public String toString(){
        return "MiniMax";
    }

    /**A method that overrides the execute method in MoveStrategy. */
    @Override
    public Move execute(Board board){

        final long startTime = System.currentTimeMillis();

        Move bestMove = null;
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;

        System.out.println(board.currentPlayer() + " THINKING with depth = " + searchDepth);
        int numMoves = board.currentPlayer().getLegalMoves().size();
        for(final Move move : board.currentPlayer().getLegalMoves()){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()){
                currentValue = board.currentPlayer().getAlliance().isWhite() ?
                        min(moveTransition.getToBoard(), searchDepth - 1) :
                        max(moveTransition.getToBoard(), searchDepth - 1);

                if(board.currentPlayer().getAlliance().isWhite() && currentValue >= highestSeenValue){
                    highestSeenValue = currentValue;
                    bestMove = move;
                } else if(board.currentPlayer().getAlliance().isBlack() && currentValue <= lowestSeenValue){
                    lowestSeenValue = currentValue;
                    bestMove = move;
                }
            }
        }

        final long executionTime = System.currentTimeMillis() - startTime;
        final long timeSec = executionTime/1000;
        final long timeMin = timeSec / 60;
        System.out.println("\tTime taken to execute: " + timeMin + ":" + timeSec + "\n");
        return bestMove;
    }

    /**A method that performs the minimizing process. */
    public int min(final Board board, final int depth){
        if(depth == 0 || isEndGameScenario(board)){
            this.boardsEvaluated++;
            return this.boardEvaluator.evaluate(board, depth);
        }
        int lowestSeenValue = Integer.MAX_VALUE;
        for(final Move move : board.currentPlayer().getLegalMoves()){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()){
                final int currentValue = max(moveTransition.getToBoard(), depth - 1);
                if(currentValue <= lowestSeenValue){
                    lowestSeenValue = currentValue;
                }
            }
        }
        return  lowestSeenValue;
    }

    /**A method that performs the maximizing process. */
    public int max(final Board board, final int depth){
        if(depth == 0 || isEndGameScenario(board)){
            this.boardsEvaluated++;
            return this.boardEvaluator.evaluate(board, depth);
        }
        int highestSeenValue = Integer.MIN_VALUE;
        for(final Move move : board.currentPlayer().getLegalMoves()){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()){
                final int currentValue = min(moveTransition.getToBoard(), depth - 1);
                if(currentValue >= highestSeenValue){
                    highestSeenValue = currentValue;
                }
            }
        }
        return  highestSeenValue;
    }

    /**A method that returns true if there is a potential game ending scenario. */
    private static boolean isEndGameScenario(final Board board){
        return board.currentPlayer().isInCheckmate() ||
                board.currentPlayer().isInStalemate();
    }

}
