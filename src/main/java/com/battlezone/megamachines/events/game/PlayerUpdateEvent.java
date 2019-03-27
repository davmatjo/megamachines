package com.battlezone.megamachines.events.game;

public class PlayerUpdateEvent {

    private final byte[] data;
    private final int playerNumber;
    private boolean running;

    /**
     * Creates a PlayerUpdateEvent which contains information of players in the game
     *
     * @param data         Data of all the players in the lobby
     * @param playerNumber The player this client is playing
     * @param running      Whether it is running
     */
    public PlayerUpdateEvent(byte[] data, int playerNumber, boolean running) {
        this.data = data;
        this.playerNumber = playerNumber;
        this.running = running;
    }

    public byte[] getData() {
        return data;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public boolean isRunning() {
        return running;
    }
}
