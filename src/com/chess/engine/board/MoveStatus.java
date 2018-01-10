package com.chess.engine.board;

/**An enum to store the possible move statuses. */
public enum MoveStatus {

    DONE {
        @Override
        public boolean isDone() {
            return true;
        }
    },
    ILLEGAL_MOVE {
        @Override
        public boolean isDone() {
            return false;
        }
    },
    LEAVES_PLAYER_IN_CHECK {
        @Override
        public boolean isDone() {
            return false;
        }
    };

    /**A method that returns true if the move status is done. */
    public abstract boolean isDone();

}
