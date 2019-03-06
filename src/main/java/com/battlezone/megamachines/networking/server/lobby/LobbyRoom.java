package com.battlezone.megamachines.networking.server.lobby;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.powerups.PowerupManager;
import com.battlezone.megamachines.networking.Protocol;
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

        sendPlayers(new ArrayList<>() {{
            players.values().forEach((p) -> add(p.getCar()));
        }});

        // Handle starting game
        if (players.size() == Server.MAX_PLAYERS)
            startGame();
    }

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

    public void startGame() throws IOException {
        gameRoom = new GameRoom(players, this, roomNumber, Server.MAX_PLAYERS - players.size());
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

    public void sendPowerupManager(PowerupManager manager) {
        byte[] buffer = ByteBuffer.allocate(1 + manager.toByteArray().length).put(Protocol.POWERUP_EVENT).put(manager.toByteArray()).array();
        for (Player player : players.values())
            sendTCP(player.getConnection().getOutputStream(), buffer);
    }

    public void sendTrack(Track track) {
        byte[] buffer = ByteBuffer.allocate(track.getTracksAcross() * track.getTracksDown() + 5).put(Protocol.TRACK_TYPE).put(track.toByteArray()).array();
        players.values().forEach((p) -> sendTCP(p.getConnection().getOutputStream(), buffer));
        players.values().forEach((p) -> sendTCP(p.getConnection().getOutputStream(), buffer));
    }

    protected void sendTCP(ObjectOutputStream address, byte[] data) {
        try {
            address.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPortToAll() {
        byte[] buffer = ByteBuffer.allocate(2).put(Protocol.UDP_DATA).put(roomNumber).array();
        players.values().forEach((p) -> sendTCP(p.getConnection().getOutputStream(), buffer));
    }

    public byte getRoomNumber() {
        return roomNumber;
    }

    public boolean isGameRunning() {
        return gameRoom != null && gameRoom.getRunning();
    }

    public InetAddress getHost() {
        return host;
    }

    public void gameEnded(List<RWDCar> finalPositions, List<RWDCar> cars) {
        gameRoom = null;

        // Send leaderboard to show race ended
        byte[] buffer = new byte[2 + Server.MAX_PLAYERS * Server.END_GAME_STATE_PLAYER];
        buffer[0] = Protocol.END_RACE;
        // From byte 1 to 9, put the leaderboard
        for (byte i = 1; i < cars.size(); i++)
            buffer[i] = (byte) finalPositions.indexOf(cars.get(i-1));
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
}
