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

    public void setTimestampValue(int timestampValue) {
        this.timestampValue = timestampValue;
    }

    public int getTimestampValue() {
        return this.timestampValue;
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
        finalString += "p:";
        int peCounter = 0;
        for (PhysicalEntity pe : entitiesData) {

            String peString = peCounter + ":";

            peString += "x:" + pe.getXInMeters() + ",";
            peString += "y:" + pe.getYInMeters() + ",";
            peString += "c" + pe.getScale() + ",";

            peString += "a:" + pe.getAngle() + ",";
            peString += "l:" + pe.getLength() + ",";
            peString += "s:" + pe.getSpeed() + ",";
            peString += "w:" + pe.getWidth() + "/";

            peCounter++;

        }
        finalString += ";";


        // Return final form of the string
        return finalString + ".";
    }

    /*
    * Method to create GameStatePacket from String
    * @param    string          GameStatePacket in String format
    * @return   GameStatePacket Final form of the GameStatePacket processed from the initial string
    * */
    public static GameStatePacket fromString(String string) {
        GameStatePacket tmp = new GameStatePacket();
        String[] values = string.split(";");

        for ( int i = 0; i < values.length; i++ ) {
            if ( values[i].length() < 1 ) {
                try {
                    throw new Exception("Wrong formatting of GameStatePacket conversion string: length = 0.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if ( values[i].charAt(0) == '.' ) {
                break;
            }
            if ( values[i].charAt(1) == ':' ) {
                String newString = values[i].substring(2);

                if (values[i].charAt(0) == 't') {
                    tmp.setTimestampValue(Integer.parseInt(newString));
                } else if (values[i].charAt(0) == 'c') {
                    tmp.setGameStateCode(Integer.parseInt(newString));
                } else if (values[i].charAt(0) == 'p') {
                    String[] physicalEntities = newString.split("/");
                    ArrayList<PhysicalEntity> tmpPE = new ArrayList<>();

                    for ( int j = 0; j < physicalEntities.length; j++ ) {

                        if ( physicalEntities[j].length() < 1 )
                            break;

                        String[] peString = physicalEntities[j].substring(2).split(",");
//                        PhysicalEntity pe = new PhysicalEntity();
                        ArrayList<Integer> peValues = new ArrayList<>();

                        for ( int k = 0; k < peString.length; k++ ) {
                            if ( peString[k].length() < 1 )
                                break;

                            peValues.add(Integer.parseInt(peString[k].substring(2)));
                        }

                        if ( peValues.size() > 0 ) {
                            int x = peValues.get(0);
                            int y = peValues.get(1);
                            int scale = peValues.get(2);
                            int angle = peValues.get(3);
                            int len = peValues.get(4);
                            int speed = peValues.get(5);
                            int width = peValues.get(6);
                        }
                        // TODO: set these values to the physical entities so you can add them to the arraylist of physical entities so you can set it to the game state packet
                    }

                } else {
                    try {
                        throw new Exception("Wrong formatting of GameStatePacket conversion string: first letter is " + values[0] + " which is unknown.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return tmp;
                }
            } else {
                try {
                    throw new Exception("Wrong formatting of GameStatePacket conversion string: second letter is not character ':'.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return tmp;
            }
        }

        return tmp;
    }
}