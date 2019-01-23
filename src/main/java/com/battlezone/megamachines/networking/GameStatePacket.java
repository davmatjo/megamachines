package com.battlezone.megamachines.networking;

import com.battlezone.megamachines.entities.PhysicalEntity;

import java.util.ArrayList;

public class GameStatePacket {

    // Game state variables
    ArrayList<PhysicalEntity> entitiesData;
    int gameStateCode;
    int timestampValue;


    // Game state codes TODO: add more states
    static final int SERVER_STARTED = 0;
    static final int INSIDE_LOBBY = 1;
    static final int GAME_LOADING = 5;
    static final int GAME_RUNNING = 10;
    static final int GAME_STOPPED = 15;
    static final int PLAYERS_INFO = 2;
    static final int SERVER_ENDED = -1;


    public GameStatePacket() {
        entitiesData = new ArrayList<>();
        gameStateCode = SERVER_STARTED;
        timestampValue = 0;
    }

    public void setEntitiesData(ArrayList<PhysicalEntity> entitiesData) {
        this.entitiesData = entitiesData;
    }

    public ArrayList<PhysicalEntity> getEntitiesData() {
        return this.entitiesData;
    }

    public void setGameStateCode(int gameStateCode) {
        this.gameStateCode = gameStateCode;
    }

    public int getGameStateCode() {
        return this.gameStateCode;
    }

    /*
    Method to convert the game state packet to String
    @return finalString     Represents final form of the string.
     */
    public String toString() {
        // Initialise string to be returned at the end of the method
        String finalString = "";


        // Convert variables to String

        // Convert game state code to String
        finalString += "c:" + this.gameStateCode + ";";

        // Convert timestamp value to String
        finalString += "t:" + this.timestampValue + ";";

        // Convert physical entities to String
        finalString += "p:{";
        int peCounter = 0;
        for (PhysicalEntity pe : entitiesData) {

            String peString = peCounter + ":[";

            peString += "x:" + pe.getXInMeters() + ",";
            peString += "y:" + pe.getYInMeters() + ",";
            peString += "c" + pe.getScale() + ",";

            peString += "a:" + pe.getAngle() + ",";
            peString += "l:" + pe.getLength() + ",";
            peString += "s:" + pe.getSpeed() + ",";
            peString += "w:" + pe.getWidth();


            peString += "]";
            peCounter++;

        }
        finalString += "};";


        // Return final form of the string
        return finalString;
    }

    /*
    * Method to create GameStatePacket from String
    * @param    string          GameStatePacket in String format
    * @return   GameStatePacket Final form of the GameStatePacket processed from the initial string
    * */
    public static GameStatePacket fromString(String string) {
        return null; // TODO: this method
    }
}