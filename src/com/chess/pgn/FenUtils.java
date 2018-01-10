package com.chess.pgn;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.pieces.Pawn;

/**A class for dealing with FEN notation. */
public class FenUtils {

    /**A constructor to create a new FenUtils object. */
    private FenUtils(){
        throw new RuntimeException("Cannot instantiate this class!");
    }

    /**A method that creates a board given a String representing a FEN notation. */
    public static Board createGameFromFEN(final String fenString){
        return null;
    }

    /**A method that creates a FEN notation string from a board. */
    public static String createFENFromBoard(final Board board){
        return cacluateBoardText(board) + " " +
                calculateCurrentPlayerText(board) + " " +
                calculateCastleText(board) + " " +
                calculateEnPassantSquare(board) + " " +
                "0 1";
    }

    /**A method that determines the FEN text representation of a board. */
    private static String cacluateBoardText(final Board board) {
        final StringBuilder builder = new StringBuilder();
        for(int i=0; i <BoardUtils.NUM_TILES; i++){
            final String tileText = board.getTile(i).toString();
            builder.append(tileText);
        }
        builder.insert(8, "/");
        builder.insert(17, "/");
        builder.insert(26, "/");
        builder.insert(35, "/");
        builder.insert(44, "/");
        builder.insert(53, "/");
        builder.insert(62, "/");

        return builder.toString().replaceAll("--------", "8")
                .replaceAll("-------", "7")
                .replaceAll("------", "6")
                .replaceAll("-----", "5")
                .replaceAll("----", "4")
                .replaceAll("---", "3")
                .replaceAll("--", "2")
                .replaceAll("-", "1");
    }

    /**A method that determines the FEN text representation of the current player. */
    private static String calculateCurrentPlayerText(final Board board) {
        return board.currentPlayer().toString().substring(0 ,1).toLowerCase();
    }

    /**A method that determines the FEN text representation of available castling moves. */
    private static String calculateCastleText(final Board board){
        final StringBuilder builder = new StringBuilder();
        if(board.whitePlayer().isKingSideCastleCapable()){
            builder.append("K");
        }
        if(board.whitePlayer().isQueenSideCastleCapable()){
            builder.append("Q");
        }
        if(board.blackPlayer().isKingSideCastleCapable()){
            builder.append("k");
        }
        if(board.blackPlayer().isQueenSideCastleCapable()){
            builder.append("q");
        }
        final String result = builder.toString();
        return result.isEmpty() ? "-" : result;
    }

    /**A method that determines the FEN text representation of an en passant pawn on a board. */
    private static String calculateEnPassantSquare(final Board board) {

        final Pawn enPassantPawn = board.getEnPassantPawn();

        if(enPassantPawn != null){
            return BoardUtils.getPositionAtCoordinate(enPassantPawn.getPiecePosition() + (8) * enPassantPawn.getPieceAlliance().getOppositeDirection());
        }

        return "-";

    }

}
