package com.battlezone.megamachines.networking;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.world.track.Track;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.*;

public class LobbyRoom {

    // Player data
    public Map<InetAddress, Player> players = new LinkedHashMap<>();

    // Room variables
    public GameRoom gameRoom;
    public InetAddress host;

    // Variables
    private boolean running = true;
    private byte roomNumber;

    public LobbyRoom(byte roomNumber, InetAddress host) {
        this.roomNumber = roomNumber;
        this.host = host;
    }

    public boolean isRunning() {
        return running;
    }

    public void updatePlayerData(InetAddress address, Player player) throws IOException {
        players.put(address, player);
        player.getConnection().setConnectionDroppedListener(this);

        sendPlayers(new ArrayList<>() {{ players.values().forEach((p) -> add(p.getCar())); }});

        // Handle starting game
        if (players.size() == Server.MAX_PLAYERS)
            startGame();
    }

    public void clean(InetAddress player) {
        // Remove lost players
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
            sendPlayers(new ArrayList<>() {{ players.values().forEach((p) -> add(p.getCar())); }});
        }


    }

    public void startGame() throws IOException {
        gameRoom = new GameRoom(this, Server.MAX_PLAYERS - players.size());
        new Thread(gameRoom).start();
    }

    private void sendFailed() {
        byte[] buffer = new byte[1];
        buffer[0] = Protocol.FAIL_CREATE;
        players.values().forEach((p) -> sendTCP(p.getConnection().getOutputStream(), buffer));
    }

    public void sendPlayers(List<RWDCar> cars) {
        byte[] buffer = ByteBuffer.allocate(3 + cars.size() * RWDCar.BYTE_LENGTH).put(Protocol.PLAYER_INFO).put(RWDCar.toByteArray(cars)).array();
        int i = 0;
        for (Player player : players.values()) {
            buffer[2] = (byte) i++;
            sendTCP(player.getConnection().getOutputStream(), buffer);
        }
    }

    public void sendTrack(Track track) {
        byte[] buffer = ByteBuffer.allocate(track.getTracksAcross() * track.getTracksDown() + 5).put(Protocol.TRACK_TYPE).put(track.toByteArray()).array();
        players.values().forEach((p) -> sendTCP(p.getConnection().getOutputStream(), buffer));
    }

    private void sendTCP(ObjectOutputStream address, byte[] data) {
        try {
            address.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPortToAll() {
        System.out.println("sending " + roomNumber);
        byte[] buffer = ByteBuffer.allocate(2).put(Protocol.UDP_DATA).put(roomNumber).array();
        players.values().forEach((p) -> sendTCP(p.getConnection().getOutputStream(), buffer));
    }

    public byte getRoomNumber() {
        return roomNumber;
    }
}
