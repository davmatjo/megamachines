package com.battlezone.megamachines.networking;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.world.track.Track;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LobbyRoom {

    // Player data
    public Map<InetAddress, Player> players = new HashMap<>();
    public List<RWDCar> cars = new ArrayList<>();
    public List<PlayerConnection> playerConnections = new ArrayList<>();
    private List<PlayerConnection> toDeletePlayers = new ArrayList<>();

    // Room variables
    public GameRoom gameRoom;
    public PlayerConnection host;

    // Variables
    private boolean running = true;
    public byte roomNumber;

    public LobbyRoom(byte roomNumber, PlayerConnection host) {
        this.roomNumber = roomNumber;
        this.host = host;
    }

    public boolean isRunning() {
        return running;
    }

    public void updatePlayerData(InetAddress address, Player player, PlayerConnection conn) throws IOException {
        players.put(address, player);
        cars.add(player.getCar());
        playerConnections.add(conn);
        sendPlayers(cars);

        // Handle starting game
        if ( players.size() == Server.MAX_PLAYERS )
            startGame();
    }

    public void clean() {
        // Remove lost players
        for ( PlayerConnection player : playerConnections )
            if ( !player.getRunning() ) {
                cars.remove(players.get(player.getAddress()).getCar());
                players.remove(player.getAddress());
                toDeletePlayers.add(player);
                System.out.println("Deleted player " + player.getAddress() + " from room " + roomNumber);
            }
        for ( PlayerConnection player : toDeletePlayers ) {
            playerConnections.remove(player);
        }
        toDeletePlayers.clear();

        // If game is not running anymore, close game Thread
        if ( gameRoom != null && !gameRoom.stillRunning() )
            gameRoom.game.close();

        // If host disconnected from lobby while in lobby, exit lobby for each
        if ( gameRoom != null && !gameRoom.getRunning() && !host.getRunning() ) {
            // Close lobby
            running = false;
            sendFailed();
            return;
        }

        // No more people here
        if ( playerConnections.size() == 0 ) {
            running = false;
            return;
        }

        // Select another host
        if ( !host.getRunning() )
            host = playerConnections.get(0);
    }

    public void startGame() throws IOException {
        gameRoom = new GameRoom(this, Server.MAX_PLAYERS - playerConnections.size());
        new Thread(gameRoom).start();
    }

    private void sendFailed() {
        byte[] buffer = new byte[1];
        buffer[0] = Protocol.FAIL_CREATE;
        for ( PlayerConnection player : playerConnections )
            sendTCP(player.getOutputStream(), buffer);
    }

    public void sendPlayers(List<RWDCar> cars) {
        byte[] buffer = ByteBuffer.allocate(3+cars.size()*RWDCar.BYTE_LENGTH).put(Protocol.PLAYER_INFO).put(RWDCar.toByteArray(cars)).array();
        int i = 0;
        for ( PlayerConnection player : playerConnections ) {
            buffer[2] = (byte)i++;
            sendTCP(player.getOutputStream(), buffer);
        }
    }

    public void sendTrack(Track track) {
        byte[] buffer = ByteBuffer.allocate(track.getTracksAcross()*track.getTracksDown()+5).put(Protocol.TRACK_TYPE).put(track.toByteArray()).array();
        playerConnections.forEach(x -> sendTCP(x.getOutputStream(), buffer));
    }

    private void sendTCP(ObjectOutputStream address, byte[] data) {
        try {
            address.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPortToAll() {
        byte[] buffer = ByteBuffer.allocate(2).put(Protocol.UDP_DATA).put(roomNumber).array();
        playerConnections.forEach(x -> sendTCP(x.getOutputStream(), buffer));
    }
}
