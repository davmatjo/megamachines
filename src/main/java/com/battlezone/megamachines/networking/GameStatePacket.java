package com.battlezone.megamachines.networking;

import com.battlezone.megamachines.entities.PhysicalEntity;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import static java.util.Arrays.copyOfRange;

public class GameStatePacket {

    // Game state variables
    ArrayList<PhysicalEntity> entitiesData;
    int gameStateCode;


    // Game state codes
    static final int JOIN_LOBBY = 0;
    static final int START_GAME = 1;

    public GameStatePacket() {
        entitiesData = new ArrayList<>();
        gameStateCode = JOIN_LOBBY;
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

    // Get empty GameStatePacket in String format
    public static GameStatePacket emptyPacket() {
        return (new GameStatePacket());
    }

    // Convert byte array to actual data
    public static void fromByteArray(byte[] packet) {
        try {
            int packetCode = packet[0];
            int playerCount = packet[1];

            for ( int i = 0; i < playerCount; i++ ) {
                double x = ByteBuffer.wrap(copyOfRange(packet, 2 + i*8, 2 + i*8+8)).getDouble();
                double y = ByteBuffer.wrap(copyOfRange(packet, 2 + i*8 + 8, 2 + i*8+16)).getDouble();
                double angle = ByteBuffer.wrap(copyOfRange(packet, 2 + i*8 + 16, 2 + i*8+24)).getDouble();
                double speed = ByteBuffer.wrap(copyOfRange(packet, 2 + i*8 + 24, 2 + i*8+32)).getDouble(); 
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}