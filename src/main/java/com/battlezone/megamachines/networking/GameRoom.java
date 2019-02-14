package com.battlezone.megamachines.networking;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.events.keys.NetworkKeyEvent;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import static com.battlezone.megamachines.networking.Protocol.*;

public class GameRoom implements Runnable {

    // UDP connection
    private Server server;
    private DatagramSocket socket;
    private DatagramPacket receive;
    private DatagramPacket send;
    private int PORT;

    // Variables
    private boolean running = true;
    private byte[] received;
    private Game game;
    private final ByteBuffer gameStateBuffer;
    private List<PlayerConnection> playerConnections;
    private Map<InetAddress, Player> players;

    public GameRoom(Server server, Map<InetAddress, Player> players, int aiCount, byte room, List<PlayerConnection> playerConnections) throws IOException {
        // Setting variables
        this.gameStateBuffer = ByteBuffer.allocate(Server.MAX_PLAYERS * Server.GAME_STATE_EACH_LENGTH + 2);
        this.PORT = Protocol.DEFAULT_PORT + room;
        this.playerConnections = playerConnections;
        this.players = players;

        // Setting server components
        this.server = server;
        this.received = new byte[Server.CLIENT_TO_SERVER_LENGTH];
        this.socket = new DatagramSocket(this.PORT);
        this.receive = new DatagramPacket(new byte[Server.CLIENT_TO_SERVER_LENGTH], Server.CLIENT_TO_SERVER_LENGTH);
        this.send = new DatagramPacket(new byte[Server.SERVER_TO_CLIENT_LENGTH], Server.SERVER_TO_CLIENT_LENGTH, null, this.PORT+1);

        // Create and initialise game
        game = new Game(players, this, aiCount);
        gameInit();
    }

    public boolean getRunning() {
        return this.running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void gameInit() {
        server.sendPortToAll((byte)(this.PORT - DEFAULT_PORT));
        server.sendPlayers(game.getCars());
        server.createAndSendTrack(game);
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
        socket.close();
        game.close();
        this.running = false;
    }

    private void dropPlayers() {
        for ( PlayerConnection player : playerConnections )
            if ( !player.getRunning() ) {
                player.close();
                players.get(player.getAddress()).getCar().setX(-1000);
                System.out.println("Room " + (PORT - Protocol.DEFAULT_PORT)/2 + " has dropped player with address " + player.getAddress());
            }
    }

    public boolean stillRunning() {
        for ( PlayerConnection player : playerConnections )
            if ( player.getRunning() )
                return true;
            else {
                player.close();
                players.get(player.getAddress()).getCar().setX(-1000);
                System.out.println("Room " + (PORT - Protocol.DEFAULT_PORT)/2 + " has dropped player with address " + player.getAddress());
            }
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
                //e.printStackTrace();
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
