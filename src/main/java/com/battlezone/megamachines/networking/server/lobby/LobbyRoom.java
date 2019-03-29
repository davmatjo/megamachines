package com.battlezone.megamachines.networking.server.lobby;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.powerups.PowerupManager;
import com.battlezone.megamachines.networking.secure.Encryption;
import com.battlezone.megamachines.networking.secure.Protocol;
import com.battlezone.megamachines.networking.server.Server;
import com.battlezone.megamachines.networking.server.game.GameRoom;
import com.battlezone.megamachines.networking.server.player.Player;
import com.battlezone.megamachines.world.track.Track;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LobbyRoom {

    // Player data
    private Map<InetAddress, Player> players = new LinkedHashMap<>();

    // Room variables
    private GameRoom gameRoom;
    private InetAddress host;

    // Variables
    private boolean running = true;
    private byte roomNumber;
    private Track track;
    private byte themeByte;
    private byte lapCounter;

    /*
     * Main constructor of the Lobby room.
     *
     * @param byte        Number id of the room
     * @param InetAddress Host address
     * */
    public LobbyRoom(byte roomNumber, InetAddress host) {
        this.roomNumber = roomNumber;
        this.host = host;
    }

    /*
     * Method to check if Lobby is running or not.
     *
     * @return boolean True if running and false otherwise.
     * */
    public boolean isRunning() {
        return running;
    }

    /*
     * Method to update player data.
     *
     * @param InetAddress    Address of the player
     * @param Player         Data of the player
     * */
    public void updatePlayerData(InetAddress address, Player player) throws IOException {
        players.put(address, player);
        player.getConnection().setConnectionDroppedListener(this);

        sendPlayers(new ArrayList<>() {{
            players.values().forEach((p) -> add(p.getCar()));
        }});

        // Handle starting game
        if (players.size() == Server.MAX_PLAYERS)
            startGame();
    }

    /*
     * Method to clean a player from Lobby.
     *
     * @param InetAddress    Player to be cleaned away from Lobby.
     * */
    public void clean(InetAddress player) {
        // Remove lost players
        if (gameRoom != null) {
            gameRoom.remove(players.get(player).getCar());
        }
        players.remove(player);

        // If there are no more players, end the game and the lobby
        if (players.isEmpty()) {
            if (gameRoom != null) {
                gameRoom.close();
            }
            Server.resetLobby(this);
            return;
        }

        // If the host leaves and the game hasn't started, select a new host
        if (player == host && gameRoom == null) {
            players.keySet().stream().limit(1).forEach((p) -> host = p);
            sendPlayers(new ArrayList<>() {{
                players.values().forEach((p) -> add(p.getCar()));
            }});
        }
    }

    /*
     * Start game method.
     * */
    public void startGame() throws IOException {
        gameRoom = new GameRoom(players, this, roomNumber, Server.MAX_PLAYERS - players.size());
        new Thread(gameRoom).start();
    }

    /*
     * Send failed to all players if it failed to create the game/race.
     * */
    private void sendFailed() {
        byte[] buffer = new byte[1];
        buffer[0] = Protocol.FAIL_CREATE;
        players.values().forEach((p) -> sendTCP(p.getConnection().getOutputStream(), buffer));
    }

    /*
     * Send all player information to all players method.
     *
     * @param List<RWDCar> List of cars to be sent
     * */
    public void sendPlayers(List<RWDCar> cars) {
        byte[] buffer = ByteBuffer.allocate(3 + cars.size() * RWDCar.BYTE_LENGTH).put(Protocol.PLAYER_INFO).put(RWDCar.toByteArray(cars)).array();
        int i = 0;
        for (Player player : players.values()) {
            buffer[2] = (byte) i++;
            sendTCP(player.getConnection().getOutputStream(), buffer);
        }
    }

    /*
     * Send powerup manager method.
     *
     * @param PowerupManager Manager to send to all players
     * */
    public void sendPowerupManager(PowerupManager manager) {
        byte[] buffer = ByteBuffer.allocate(1 + manager.toByteArray().length).put(Protocol.POWERUP_EVENT).put(manager.toByteArray()).array();
        System.out.println(buffer.length);
        for (Player player : players.values())
            sendTCP(player.getConnection().getOutputStream(), buffer);
    }

    /*
     * Send track method to all players.
     *
     * @param Track  Track to send to all players
     * */
    public void sendTrack(Track track) {
        byte[] buffer = ByteBuffer.allocate(track.getTracksAcross() * track.getTracksDown() + 7).put(Protocol.TRACK_TYPE)
                .put(track.toByteArray()).put(themeByte).put(lapCounter).array();
        players.values().forEach((p) -> sendTCP(p.getConnection().getOutputStream(), buffer));
    }

    /*
     * Method to send packet through TCP.
     *
     * @param ObjectOutputStream Stream to send the data to
     * @param byte[]             Data to send
     * */
    protected void sendTCP(ObjectOutputStream address, byte[] data) {
        try {
            address.writeObject(Encryption.encrypt(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Method to send the port to all players.
     */
    public void sendPortToAll() {
        byte[] buffer = ByteBuffer.allocate(2).put(Protocol.UDP_DATA).put(roomNumber).array();
        players.values().forEach((p) -> sendTCP(p.getConnection().getOutputStream(), buffer));
    }

    /*
     * Method to get the room number.
     *
     * @return byte  Byte number of the room
     * */
    public byte getRoomNumber() {
        return roomNumber;
    }

    /*
     * Method to get boolean of game running or not.
     *
     * @return boolean True if game is running and false otherwise
     * */
    public boolean isGameRunning() {
        return gameRoom != null && gameRoom.getRunning();
    }

    /*
     * Method to get address of host
     *
     * @return InetAddress Address of host
     * */
    public InetAddress getHost() {
        return host;
    }

    /*
     * Method to call when game has ended.
     *
     * @param List<RWDCar> List of final positions of cars
     * @param List<RWDCar> List of all cars
     * */
    public void gameEnded(List<RWDCar> finalPositions, List<RWDCar> cars) {
        gameRoom = null;

        // Send leaderboard to show race ended
        byte[] buffer = new byte[2 + Server.MAX_PLAYERS * Server.END_GAME_STATE_PLAYER];
        buffer[0] = Protocol.END_RACE;
        // From byte 1 to 9, put the leaderboard
        for (byte i = 1; i < cars.size(); i++)
            buffer[i] = (byte) finalPositions.indexOf(cars.get(i - 1));
        players.values().forEach((p) -> sendTCP(p.getConnection().getOutputStream(), buffer));

        // Send players data once again
        List<RWDCar> carsNoAI = new ArrayList<>();
        for (InetAddress address : players.keySet())
            carsNoAI.add(players.get(address).getCar());
        sendPlayers(carsNoAI);

        // Wait 4 seconds
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

        // Send END_GAME package
        final byte[] buffer2 = new byte[]{Protocol.END_GAME};
        players.values().forEach((p) -> sendTCP(p.getConnection().getOutputStream(), buffer2));
    }

    /*
     * Method to get current Track.
     *
     * @return Track Track to be returned that is current for the lobby
     * */
    public Track getTrack() {
        return this.track;
    }

    /*
     * Method to set the track.
     *
     * @param Track  Track to be set for the lobby
     * */
    public void setTrack(Track track) {
        this.track = track;
    }

    /*
     * Method to get the lap counter.
     *
     * @return byte  Lap counter number
     * */
    public byte getLapCounter() {
        return this.lapCounter;
    }

    /*
     * Method to set the lap counter.
     *
     * @param byte   The lap number to be set
     * */
    public void setLapCounter(byte laps) {
        this.lapCounter = laps;
    }

    /*
     * Method to get the byte of the actual theme.
     *
     * @return byte  The byte of the actual theme
     * */
    public byte getThemeByte() {
        return this.themeByte;
    }

    /*
     * Method to set the theme of the lobby.
     *
     * @param byte   The byte of the theme to be set
     * */
    public void setThemeByte(byte themeByte) {
        this.themeByte = themeByte;
    }

    /*
     * Method to close the lobby Thread.
     * */
    public void close() {
        gameRoom.close();
        this.running = false;
    }
}
