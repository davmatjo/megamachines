package com.battlezone.megamachines.networking;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.events.keys.NetworkKeyEvent;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.battlezone.megamachines.networking.Protocol.*;

public class GameRoom implements Runnable {

    // UDP connection
    private DatagramSocket socket;
    private DatagramPacket receive;
    private DatagramPacket send;
    private int PORT;

    // Player data
    private final ByteBuffer gameStateBuffer;
    private List<PlayerConnection> playerConnections;
    private List<PlayerConnection> connectionsToDelete = new ArrayList<>();
    private Map<InetAddress, Player> players;

    // Room variables
    private LobbyRoom lobbyRoom;
    public Game game;

    // Variables
    private boolean running = true;
    private byte[] received;


    public GameRoom(LobbyRoom lobbyRoom, int aiCount) throws IOException {
        // Setting variables
        this.gameStateBuffer = ByteBuffer.allocate(Server.MAX_PLAYERS * Server.GAME_STATE_EACH_LENGTH + 2);
        this.PORT = Protocol.DEFAULT_PORT + lobbyRoom.roomNumber*2;
        this.playerConnections = lobbyRoom.playerConnections;
        this.players = lobbyRoom.players;
        this.lobbyRoom = lobbyRoom;

        // Create and initialise game
        game = new Game(players, this, aiCount);
        gameInit();

        // Setting server components
        this.received = new byte[Client.CLIENT_TO_SERVER_LENGTH];
        this.socket = new DatagramSocket(this.PORT);
        this.receive = new DatagramPacket(new byte[Client.CLIENT_TO_SERVER_LENGTH], Client.CLIENT_TO_SERVER_LENGTH);
        this.send = new DatagramPacket(new byte[Server.SERVER_TO_CLIENT_LENGTH], Server.SERVER_TO_CLIENT_LENGTH, null, this.PORT+1);
    }

    public boolean getRunning() {
        return this.running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void gameInit() {
        lobbyRoom.sendPortToAll();
        lobbyRoom.sendPlayers(game.getCars());
        lobbyRoom.sendTrack(game.getTrack());
        this.running = true;
    }

    public void sendGameState(List<RWDCar> cars) {
        // Set data to game state
        gameStateBuffer.put(Protocol.GAME_STATE).put((byte) cars.size());
        for ( RWDCar car : cars )
            gameStateBuffer.putDouble(car.getX()).putDouble(car.getY()).putDouble(car.getAngle()).putDouble(car.getSpeed()).put(car.getLap()).put(car.getPosition());

        // Send the data to all the players
        for (InetAddress playerAddress : players.keySet())
            sendPacket(playerAddress, gameStateBuffer.array());
        gameStateBuffer.clear();
    }

    private void sendPacket(InetAddress address, byte[] data) {
        try {
            send.setAddress(address);
            send.setData(data);
            socket.send(send);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        dropPlayers();
        socket.close();
        game.close();
        this.running = false;
    }

    private void dropPlayers() {
        for ( PlayerConnection player : playerConnections )
            if ( !player.getRunning() ) {
                players.get(player.getAddress()).getCar().setX(-1000);
                System.out.println("Room " + (PORT - Protocol.DEFAULT_PORT)/2 + " has dropped player with address " + player.getAddress());
                connectionsToDelete.add(player);
            }
        for ( PlayerConnection player : connectionsToDelete )
            playerConnections.remove(player);
        connectionsToDelete.clear();
    }

    public boolean stillRunning() {
        for ( PlayerConnection player : playerConnections )
            if ( player.getRunning() )
                return true;
        close();
        return false;
    }

    @Override
    public void run() {
        (new Thread(game)).start();
        while (running) {
            // Drop players that are not connected anymore
            dropPlayers();

            // Receive the package
            try {
                socket.receive(receive);
            } catch (IOException e) {
                System.out.println("Room " + (PORT - DEFAULT_PORT)/2 + " failed to receive UDP packets.");
            }
            received = receive.getData();

            // Case when packet specifies key info
            if (received[0] == KEY_EVENT) {
                int eventKeyCode = received[2];
                game.keyPress(new NetworkKeyEvent(eventKeyCode, received[1] == KEY_PRESSED, receive.getAddress()));
            }
        }
    }
}
