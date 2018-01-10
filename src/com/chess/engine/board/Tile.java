package com.chess.engine.board;

import java.util.Map;
import java.util.HashMap;

import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableMap;

/**An abstract class to represent a generic tile on a chess board. */
public abstract class Tile {

    //field to define tile position
    protected final int tileCoordinate;
    private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE = createAllEmptyTiles();

    /**A constructor for concrete sub-classes to call to instantiate themselves. */
    private Tile(final int tileCoordinate) { this.tileCoordinate = tileCoordinate; }

    /**A method that creates a cache of all possible emptyTile objects 0-63. */
    private static Map<Integer, EmptyTile> createAllEmptyTiles(){

        final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();

        for(int i=0; i<BoardUtils.NUM_TILES; i++){
            emptyTileMap.put(i, new EmptyTile(i));
        }

        //Collections.unmodifiableMap(emptyTileMap);	//alternative to importing Guava
        return ImmutableMap.copyOf(emptyTileMap);
    }

    /**A method that creates a new Tile dependant on whether the tile is occupied or not. */
    public static Tile createTile(final int tileNum, final Piece piece){
        return piece != null ? new OccupiedTile(tileNum, piece) : EMPTY_TILES_CACHE.get(tileNum);
    }

    //abstract methods to be implemented by concrete sub-classes
    /**A method that returns true if a piece on a tile. */
    public abstract boolean isTileOccupied();

    /**A method that returns the piece stored on a tile if there is one. */
    public abstract Piece getPiece();

    /**A method that returns the tile coordinate. */
    public int getTileCoordinate(){
        return this.tileCoordinate;
    }

    /**A concrete subclass of Tile that represents a tile with no piece on it. */
    public static final class EmptyTile extends Tile{

        /**A constructor to create an EmptyTile object. */
        private EmptyTile(final int tileNum){
            super(tileNum);
        }

        /**A method that returns a hyphen if the tile is empty. */
        @Override
        public String toString(){
            return "-";
        }

        /**A method that returns false if a tile is empty. */
        @Override
        public boolean isTileOccupied(){
            return false;
        }

        /**A method that returns a null piece value if the tile is empty. */
        @Override
        public Piece getPiece(){
            return null;
        }

    }

    /**A concrete subclass of Tile that represents a tile with a piece on it. */
    public static final class OccupiedTile extends Tile{

        //field to define piece on the tile
        private final Piece piece;

        /**A constructor to create an OccupiedTile object. */
        private OccupiedTile(int tileNum, Piece piece){
            super(tileNum);
            this.piece = piece;
        }

        /**A method that returns an ascii definition for a piece on a tile. */
        @Override
        public String toString(){
            return getPiece().getPieceAlliance().isBlack() ? getPiece().toString().toLowerCase() :
                    getPiece().toString();
        }

        /**A method that returns true if a tile has a piece on it. */
        @Override
        public boolean isTileOccupied(){
            return true;
        }

        /**A method that return the piece on a given tile. */
        @Override
        public Piece getPiece(){
            return piece;
        }

    }

}
