package com.chess.engine.board;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**A class that contains various utilities that might be useful when dealing with a Board object. */
public enum BoardUtils {

    //fields to dictate if an exception must be made to the generic move algorithms of a piece

    INSTANCE;

    public static final List<Boolean> FIRST_FILE = initFile(0);
    public static final List<Boolean> SECOND_FILE = initFile(1);
    public static final List<Boolean> THIRD_FILE = initFile(2);
    public static final List<Boolean> FOURTH_FILE = initFile(3);
    public static final List<Boolean> FIFTH_FILE = initFile(4);
    public static final List<Boolean> SIXTH_FILE = initFile(5);
    public static final List<Boolean> SEVENTH_FILE = initFile(6);
    public static final List<Boolean> EIGHTH_FILE = initFile(7);

    public static final List<Boolean> EIGHTH_RANK = initRank(0);
    public static final List<Boolean> SEVENTH_RANK = initRank(8);
    public static final List<Boolean> SIXTH_RANK = initRank(16);
    public static final List<Boolean> FIFTH_RANK = initRank(24);
    public static final List<Boolean> FOURTH_RANK = initRank(32);
    public static final List<Boolean> THIRD_RANK = initRank(40);
    public static final List<Boolean> SECOND_RANK = initRank(48);
    public static final List<Boolean> FIRST_RANK = initRank(56);

    public static final int START_TILE_INDEX = 0;

    public static final List<String> ALGEBRAIC_NOTATION = initAlgebraicNotation();
    public static final Map<String, Integer> POSITION_TO_COORDINATE = initPositionToCoordinateMap();

    public static final int NUM_TILES = 64;
    public static final int NUM_TILES_PER_ROW = 8;

//    /**A constructor that throws a RuntimeException when called because this class should not be instantiated. */
//    private BoardUtils(){
//        throw new RuntimeException("You cannot instantiate this class!");
//    }

    /**A method that initializes a boolean array appropriately based on the specific columns of a chess board. */
    private static List<Boolean> initFile(int fileNumber){
        final Boolean[] file = new Boolean[NUM_TILES];
        for(int i = 0; i < file.length; i++){
            file[i] = false;
        }
        do{
            file[fileNumber] = true;
            fileNumber += NUM_TILES_PER_ROW;
        } while(fileNumber < NUM_TILES);

        return ImmutableList.copyOf(file);
    }

    /**A method that initializes a boolean array appropriately based on the specific rows of a chess board. */
    private static List<Boolean> initRank(int rankNumber){
        final Boolean[] rank = new Boolean[NUM_TILES];
        for(int i = 0; i < rank.length; i++) {
            rank[i] = false;
        }
        do{
            rank[rankNumber] = true;
            rankNumber++;
        } while(rankNumber % NUM_TILES_PER_ROW != 0);

        return ImmutableList.copyOf(rank);
    }

    /**A method that assigns all possible PGN notations.*/
    private static List<String> initAlgebraicNotation() {
        return ImmutableList.copyOf(new String[]{
                "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
                "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
                "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
                "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
                "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
                "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
                "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"
        });
    }

    /**A method that creates a Map linking corresponding coordinates and PGN notation. */
    private static  Map<String, Integer> initPositionToCoordinateMap() {
        final Map<String, Integer> positionToCoordinate = new HashMap<>();
        for (int i = START_TILE_INDEX; i < NUM_TILES; i++) {
            positionToCoordinate.put(ALGEBRAIC_NOTATION.get(i), i);
        }
        return ImmutableMap.copyOf(positionToCoordinate);
    }

    /**A method that determines if the coordinate of a given Tile object is valid. */
    public static boolean isValidTileCoordinate(final int coordinate) {
        return (coordinate >= 0) && (coordinate < 64);
    }

    /**A method to return a coordinate based on PNG notation. */
    public static int getCoordinateAtPosition(final String position){
        return POSITION_TO_COORDINATE.get(position);
    }

    /**A method to return PNG notation based on a coordinate. */
    public static String getPositionAtCoordinate(final int coordinate) {
        return ALGEBRAIC_NOTATION.get(coordinate);
    }
}
