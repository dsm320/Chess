package com.chess.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import static com.chess.gui.Table.*;

/**A class that keeps track of the past moves. */
public class GameHistoryPanel extends JPanel{

    //fields
    private final DataModel model;
    private final JScrollPane scrollPane;
    private final static Dimension HISTORY_PANEL_DIMENSION = new Dimension(100,400);

    /**A constructor that creates a GameHistoryPanel. */
    GameHistoryPanel(){
        this.setLayout(new BorderLayout());
        this.model = new DataModel();
        final JTable table = new JTable(model);
        table.setRowHeight(15);
        this.scrollPane = new JScrollPane(table);
        scrollPane.setColumnHeaderView(table.getTableHeader());
        scrollPane.setPreferredSize(HISTORY_PANEL_DIMENSION);
        this.add(scrollPane, BorderLayout.CENTER);
        this.setVisible(true);
    }

    /**A method that recreates the GameHistoryPanel after every move. */
    void redo(final Board board, final MoveLog moveLog){

        int currentRow = 0;
        this.model.clear();
        for(final Move move : moveLog.getMoves()){
            final String moveText = move.toString();
            if(move.getMovedPiece().getPieceAlliance().isWhite()){
                this.model.setValueAt(moveText, currentRow, 0);
            } else if(move.getMovedPiece().getPieceAlliance().isBlack()){
                this.model.setValueAt(moveText, currentRow, 1);
                currentRow++;
            }
        }
        if(moveLog.getMoves().size() > 0){
            final Move lastMove = moveLog.getMoves().get(moveLog.size()-1);
            final String moveText = lastMove.toString();

            if(lastMove.getMovedPiece().getPieceAlliance().isWhite()){
                this.model.setValueAt(moveText + calculateCheckAndCheckmateHash(board), currentRow, 0);
            } else if(lastMove.getMovedPiece().getPieceAlliance().isBlack()){
                this.model.setValueAt(moveText + calculateCheckAndCheckmateHash(board), currentRow-1, 1);
            }
        }
        final JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());

    }

    private String calculateCheckAndCheckmateHash(final Board board){
        if(board.currentPlayer().isInCheckmate()){
            return "#";
        } else if(board.currentPlayer().isInCheck()){
            return "+";
        }
        return "";
    }

    /**A class that represents the last move for each player. */
    private static class Row {

        //fields
        private String whiteMove;
        private String blackMove;

        /**A constructor that creates a Row object. */
        Row(){
            //do nothing
        }

        /**A method that returns a string representation of white's last move. */
        public String getWhiteMove(){
            return this.whiteMove;
        }

        /**A method that returns a string representation of black's last move. */
        public String getBlackMove(){
            return this.blackMove;
        }

        /**A method that sets whiteMove. */
        public void setWhiteMove(final String move){
            this.whiteMove = move;
        }

        /**A method that sets blackMove. */
        public void setBlackMove(final String move){
            this.blackMove = move;
        }

    }

    /**A class that helps format the move history table. */
    private static class DataModel extends DefaultTableModel {

        //fields
        private final List<Row> values;
        private static final String[] NAMES = {"White", "Black"};

        /**A constructor that creates a DateModel object. */
        private DataModel(){
            this.values = new ArrayList<>();
        }

        /**A method that clears all rows. */
        public void clear(){
            this.values.clear();
            setRowCount(0);
        }

        /**A method that returns the number of rows. */
        @Override
        public int getRowCount(){
            if(this.values == null) {
                return 0;
            }
            return this.values.size();
        }

        /**A method that returns the number of columns. */
        @Override
        public int getColumnCount(){
            return NAMES.length;
        }

        /**A method that returns the object stored at a given position. */
        @Override
        public Object getValueAt(final int row, final int column){
            final Row currentRow = this.values.get(row);
            if(column == 0){
                return currentRow.getWhiteMove();
            } else if (column == 1){
                return currentRow.getBlackMove();
            }
            return null;
        }

        /**A method that sets the value at a given position to a given object. */
        @Override
        public void setValueAt(final Object aValue, final int row, final int column){
            final Row currentRow;
            if(this.values.size() <= row){
                currentRow = new Row();
                this.values.add(currentRow);
            } else {
                currentRow = this.values.get(row);
            }
            if(column == 0){
                currentRow.setWhiteMove((String) aValue);
                fireTableRowsInserted(row, row);
            } else if(column == 1){
                currentRow.setBlackMove((String) aValue);
                fireTableCellUpdated(row, column);
            }
        }

        /**A method that returns the class stored in a column. */
        @Override
        public Class<?> getColumnClass(final int column){
            return Move.class;
        }

        /**A method that returns the name of a column. */
        @Override
        public String getColumnName(final int column){
            return NAMES[column];
        }

    }

}
