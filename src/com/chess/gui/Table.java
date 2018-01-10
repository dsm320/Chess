
package com.chess.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

import com.chess.engine.board.*;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.ai.MiniMax;
import com.chess.engine.player.ai.MoveStrategy;
import com.google.common.collect.Lists;

/**A class to create a basic frame for the GUI. */
public class Table extends Observable{

    //fields with information
    private final JFrame gameFrame;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final BoardPanel boardPanel;
    private final MoveLog moveLog;
    private final GameSetup gameSetup;

    private Board chessBoard;

    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;
    private BoardDirection boardDirection;

    private Move computerMove;

    private boolean highlightLegalMoves;

    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);
    private static String defaultPieceImagesPath = "art/pieces/";

    private final Color lightTileColor = Color.decode("#FFFACD");
    private final Color darkTileColor = Color.decode("#593E1A");

    public static final Table INSTANCE = new Table();

    /**A constructor to create a new Table object. */
    private Table(){
        //create the outer window
        this.gameFrame = new JFrame("Chess");
        this.gameFrame.setLayout(new BorderLayout());
        //adds a menu bar
        final JMenuBar tableMenuBar = createTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        //creates a standard board
        this.chessBoard = Board.createStandardBoard();
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();
        this.addObserver(new TableGameAIWatcher());
        this.gameSetup = new GameSetup(this.gameFrame, true);
        this.boardDirection = BoardDirection.NORMAL;
        this.highlightLegalMoves = true;
        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        //makes everything visible
        this.gameFrame.setVisible(true);
    }

    /**A method that gets the Table object. */
    public static Table get(){
        return INSTANCE;
    }

    /**A method that shows the table in the display. */
    public void show(){
        Table.get().getMoveLog().clear();
        Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
        Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
    }

    /**A method that returns the game setup. */
    private GameSetup getGameSetup(){
        return this.gameSetup;
    }

    /**A method that returns the chessboard. */
    private Board getGameBoard(){
        return this.chessBoard;
    }

    /**A method that returns the move log. */
    private MoveLog getMoveLog(){
        return this.moveLog;
    }

    /**A method that returns the game history panel. */
    private GameHistoryPanel getGameHistoryPanel(){
        return this.gameHistoryPanel;
    }

    /**A method that returns the taken piece panel. */
    private TakenPiecesPanel getTakenPiecesPanel(){
        return this.takenPiecesPanel;
    }

    /**A method that returns the board panel. */
    private BoardPanel getBoardPanel(){
        return this.boardPanel;
    }

    /**A method that updates who made the last move. */
    private void moveMadeUpdate(final PlayerType playerType){
        setChanged();
        notifyObservers(playerType);
    }

    /**A method that updates the game board after an AI move is made. */
    public void updateGameBoard(final Board board){
        this.chessBoard = board;
    }

    /**A method that updates the computer move. */
    public void updateComputerMove(final Move move){
        this.computerMove = move;
    }

    /**A method that adds a new drop down to the menu bar. */
    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        tableMenuBar.add(createOptionsMenu());
        return tableMenuBar;
    }

    /**A method that adds a file menu drop down. */
    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");

        //adds a drop down option to load a game via PGN file
        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Open up the PGN file.");
            }
        });
        fileMenu.add(openPGN);

        //adds a drop down option to quit the game
        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);
        return fileMenu;
    }

    /**A method that adds a preferences drop down. */
    private JMenu createPreferencesMenu(){
        final JMenu preferencesMenu = new JMenu("Preferences");
        //adds the option to flip the board orientation
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
        flipBoardMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard(chessBoard);
            }
        });
        preferencesMenu.add(flipBoardMenuItem);

        preferencesMenu.addSeparator();

        //adds the option to highlight legal moves
        final JCheckBoxMenuItem legalMoveHighlighterCheckbox = new JCheckBoxMenuItem("Highlight Legal Moves", true);
        legalMoveHighlighterCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highlightLegalMoves = legalMoveHighlighterCheckbox.isSelected();
            }
        });
        preferencesMenu.add(legalMoveHighlighterCheckbox);

        return preferencesMenu;
    }

    /**A method that adds an option drop down. */
    private JMenu createOptionsMenu(){
        final JMenu optionsMenu = new JMenu("Options");
        final JMenuItem setupGameMenuItem = new JMenuItem("Setup Game");
        setupGameMenuItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                Table.get().getGameSetup().promptUser();
                Table.get().setupUpdate(Table.get().getGameSetup());
            }
        });
        optionsMenu.add(setupGameMenuItem);
        return optionsMenu;
    }

    /**A method that notifies the AI play that it can now make its move. */
    private void setupUpdate(final GameSetup gameSetup){
        setChanged();
        notifyObservers(gameSetup);
    }

    /**A class that represents an AI watcher. */
    private static class TableGameAIWatcher implements Observer{

        /**A method that implements the update method in Observer.*/
        @Override
        public void update(final Observable o, final Object arg){

            if(Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().currentPlayer()) &&
                    !Table.get().getGameBoard().currentPlayer().isInCheckmate() &&
                    !Table.get().getGameBoard().currentPlayer().isInStalemate()){
                //create an AI thread
                //execute AI work
                final AIThinkTank thinkTank = new AIThinkTank();
                thinkTank.execute();
            }

            if(Table.get().getGameBoard().currentPlayer().isInCheckmate()){
                System.out.println("Game over. " + Table.get().getGameBoard().currentPlayer() + " is in checkmate.");
            }

            if(Table.get().getGameBoard().currentPlayer().isInStalemate()){
                System.out.println("Game over. " + Table.get().getGameBoard().currentPlayer() + " is in stalemate.");
            }
        }

    }

    /**A class that represents an AI think tank. */
    private static class AIThinkTank extends SwingWorker<Move, String>{

        /**A method that creates an AIThinkTank object. */
        private AIThinkTank(){
            //do nothing
        }

        /**A method that returns the best move the AI can make. */
        @Override
        protected Move doInBackground() throws Exception{
            final MoveStrategy miniMax = new MiniMax(4);
            final Move bestMove = miniMax.execute(Table.get().getGameBoard());
            return bestMove;
        }

        @Override
        public void done(){

            try{
                final Move bestMove = get();
                Table.get().updateComputerMove(bestMove);
                Table.get().updateGameBoard(Table.get().getGameBoard().currentPlayer().makeMove(bestMove).getToBoard());
                Table.get().getMoveLog().addMove(bestMove);
                Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(), Table.get().getMoveLog());
                Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
                Table.get().moveMadeUpdate(PlayerType.COMPUTER);
            } catch (InterruptedException | ExecutionException e){
                e.printStackTrace();
            }
        }

    }

    /**A class to visually represent the board. */
    private class BoardPanel extends JPanel{
        final List<TilePanel> boardTiles;

        /**A constructor to create a new BoardPanel object. */
        BoardPanel(){
            super(new GridLayout(8,8));
            this.boardTiles = new ArrayList<>();
            //adds 64 tile panels to the board panel
            for(int i=0; i< BoardUtils.NUM_TILES; i++){
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        /**A method to draw the chessboard. */
        public void drawBoard(final Board board){
            removeAll();
            for(final TilePanel tilePanel : boardDirection.traverse(boardTiles)){
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }

    }

    /**A class to keep track of all moves being made. */
    public static class MoveLog{

        //fields
        private final List<Move> moves;

        /**A constructor that creates a MoveLog object. */
        MoveLog(){
            this.moves = new ArrayList<>();
        }

        /**A method that returns all moves that have been made. */
        public List<Move> getMoves(){
            return this.moves;
        }

        /**A method that adds a move to the list of moves that have been made. */
        public void addMove(Move move){
            this.moves.add(move);
        }

        /**A method that returns the number of moves made. */
        public int size(){
            return this.moves.size();
        }

        /**A method that clears the move history. */
        public void clear(){
            this.moves.clear();
        }

        /**A method that removes a move at a particular index. */
        public Move removeMove(final int index){
            return this.moves.remove(index);
        }

        /**A method that removes a particular moves. */
        public boolean removeMove(final Move move){
            return this.moves.remove(move);
        }

    }

    /**A class to visually represent a tile. */
    private class TilePanel extends JPanel{
        private final int tileId;

        /**A constructor to create a new TilePanel object. */
        TilePanel(final BoardPanel boardPanel, final int tileId){
            super(new GridBagLayout());
            this.tileId = tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieceIcon(chessBoard);

            //add listener for mouse clicks
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    //cancels piece selection
                    if(isRightMouseButton(e)){
                        sourceTile = null;
                        destinationTile = null;
                        humanMovedPiece = null;
                    //selects piece to move
                    } else if(isLeftMouseButton(e)) {
                        //first click - pick source
                        if(sourceTile == null) {
                            sourceTile = chessBoard.getTile(tileId);
                            humanMovedPiece = sourceTile.getPiece();
                            if (humanMovedPiece == null) {
                                sourceTile = null;
                            }
                         //second click - pick destination
                        } else {
                            destinationTile = chessBoard.getTile(tileId);
                            final Move move = Move.MoveFactory.createMove(chessBoard, sourceTile.getTileCoordinate(),destinationTile.getTileCoordinate());
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                            if(transition.getMoveStatus().isDone()){
                                chessBoard = transition.getToBoard();
                                moveLog.addMove(move);
                            }
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                //gameHistoryPanel.redo(chessBoard, moveLog);
                                takenPiecesPanel.redo(moveLog);
                                if(gameSetup.isAIPlayer(chessBoard.currentPlayer())){
                                    Table.get().moveMadeUpdate(PlayerType.HUMAN);
                                }
                                boardPanel.drawBoard(chessBoard);
                            }
                        });
                    }
                }
                @Override
                public void mousePressed(MouseEvent e){ }

                @Override
                public void mouseReleased(MouseEvent e){ }

                @Override
                public void mouseEntered(MouseEvent e){ }

                @Override
                public void mouseExited(MouseEvent e){ }
            });

            validate();
        }

        /**A method to place an image of a piece on a tile. */
        private void assignTilePieceIcon(final Board board){
            this.removeAll();
            if(board.getTile(this.tileId).isTileOccupied()){
                try {
                    final BufferedImage image =
                            ImageIO.read(new File(defaultPieceImagesPath + board.getTile(this.tileId).getPiece().getPieceAlliance().toString().substring(0,1) +
                            board.getTile(this.tileId).getPiece().toString() + ".gif"));
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /**A method that determines if a tile is black or white. */
        private void assignTileColor() {
            if(BoardUtils.EIGHTH_RANK.get(this.tileId) ||
                    BoardUtils.SIXTH_RANK.get(this.tileId) ||
                    BoardUtils.FOURTH_RANK.get(this.tileId) ||
                    BoardUtils.SECOND_RANK.get(this.tileId)){
                setBackground(this.tileId % 2 == 0 ? lightTileColor : darkTileColor);
            } else if(BoardUtils.SEVENTH_RANK.get(this.tileId) ||
                    BoardUtils.FIFTH_RANK.get(this.tileId) ||
                    BoardUtils.THIRD_RANK.get(this.tileId) ||
                    BoardUtils.FIRST_RANK.get(this.tileId)){
                setBackground(this.tileId % 2 != 0 ? lightTileColor : darkTileColor);
            }
        }

        /**A method that calculates the legal moves of a selected piece. */
        private Collection<Move> pieceLegalMoves(final Board board){
            if(humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()){
                return humanMovedPiece.calculateLegalMoves(board);
            }
            return Collections.emptyList();
        }

        /**A method that adds a green dow to all legal moves. */
        private void highlightLegalMoves(final Board board){
            if(highlightLegalMoves){
                if(board.currentPlayer().isInCheckmate() || board.currentPlayer().isInStalemate()){
                    return;
                }
                for(final Move move : pieceLegalMoves(board)){
                    if(move.getDestinationCoordinate() == this.tileId){
                        try {
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("art/misc/green_dot.png")))));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        /**A method that draws a tile. */
        public void drawTile(final Board board){
            assignTileColor();
            assignTilePieceIcon(board);
            highlightLegalMoves(board);
            validate();
            repaint();
        }
    }

    /**An enum that represents the two different board orientations. */
    public enum BoardDirection {

        NORMAL {

            /**A method that implements the abstract traverse method. */
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return boardTiles;
            }

            /**A method that implements the abstract method opposite. */
            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }
        },
        FLIPPED {
            /**A method that implements the abstract traverse method. */
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return Lists.reverse(boardTiles);
            }

            /**A method that implements the abstract method opposite. */
            @Override
            BoardDirection opposite() {
                return NORMAL;
            }
        };

        /**An abstract method that tells how to traverse board tiles. */
        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);

        /**An abstract method that returns the opposite board direction. */
        abstract BoardDirection opposite();

    }

    /**An enum that represents the two different player types. */
    public enum PlayerType{
        HUMAN,
        COMPUTER
    }

}
