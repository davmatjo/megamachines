package com.battlezone.megamachines.networking;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.events.keys.NetworkKeyEvent;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.world.track.Track;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.*;

import static com.battlezone.megamachines.networking.Protocol.KEY_EVENT;
import static com.battlezone.megamachines.networking.Protocol.KEY_PRESSED;

public class NewServer {

    // Define other constants
    private static final int MAX_PLAYERS = 8;

    // Define final port
    static final int PORT = 6970;

    public static final int SERVER_TO_CLIENT_LENGTH = 300;

    private boolean running = true;
    private Protocol.State currentState = Protocol.State.LOBBY;
    private final DatagramSocket socket;
    private final DatagramPacket receive;
    private final DatagramPacket send;
    private InetAddress host;
    private final ByteBuffer gameStateBuffer;
    byte[] received;

    public NewServer() throws SocketException {
        this.socket = new DatagramSocket(PORT);
        this.receive = new DatagramPacket(new byte[14], 14);
        this.send = new DatagramPacket(new byte[SERVER_TO_CLIENT_LENGTH], SERVER_TO_CLIENT_LENGTH, null, Client.PORT);
        this.gameStateBuffer = ByteBuffer.allocate(MAX_PLAYERS * 32 + 2);
    }

    public void run() {
        Map<InetAddress, Player> players = new HashMap<>();

        while (running) {
            try {
                socket.receive(receive);
                received = receive.getData();

                if (received[0] == Protocol.JOIN_LOBBY) {
                    // Make the first player as the host
                    if ( players.isEmpty() )
                        host = receive.getAddress();

                    // Add new players to the player ArrayList if there's less than MAX_PLAYERS amount
                    if ( players.size() < MAX_PLAYERS ) {
                        players.put(receive.getAddress(), new Player((int) received[1], Vector3f.fromByteArray(received, 2)));

                        // Send players info
                        List<RWDCar> cars = new ArrayList<>();
                        players.values().forEach(player -> cars.add(player.getCar()));
                        sendPlayers(players, cars);
                    }
                }
                if ( received[0] == Protocol.START_GAME && receive.getAddress().equals(host) ) {
                    currentState = Protocol.State.IN_GAME;
                    initGame(players);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendPlayers(Map<InetAddress, Player> players, List<RWDCar> cars) throws IOException {
        byte[] buffer = ByteBuffer.allocate(3+cars.size()*13).put(Protocol.PLAYER_INFO).put(RWDCar.toByteArray(cars)).array();
        int i = 0;
        for ( InetAddress client : players.keySet() ) {
            buffer[2] = (byte)i++;
            send.setAddress(client);
            send.setData(buffer);
            socket.send(send);
        }
    }

    private void initGame(Map<InetAddress, Player> players) throws IOException {
        Game game = new Game(players, this, MAX_PLAYERS - players.size());
        // Send track info

        sendPlayers(players, game.getCars());
        Track track = game.getTrack();
        byte[] buffer = ByteBuffer.allocate(track.getTracksAcross()*track.getTracksDown()+5).put(Protocol.TRACK_TYPE).put(track.toByteArray()).array();
        for ( InetAddress a : players.keySet() ) {
            send.setData(buffer);
            send.setAddress(a);
            socket.send(send);
        }

        new Thread(game).start();

        while (running) {
            // Receive the package
            socket.receive(receive);
            byte[] data = receive.getData();

            // Case when packet specifies key info
            if (data[0] == KEY_EVENT) {
                // Process the key
                int eventKeyCode = data[2];
                game.keyPress(new NetworkKeyEvent(eventKeyCode, data[1] == KEY_PRESSED, receive.getAddress()));
            }
        }
    }

    public void sendPacket(InetAddress address, byte[] data) {
        try {
            send.setAddress(address);
            send.setData(data);
            socket.send(send);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendGameState(Map<InetAddress, Player> players, List<RWDCar> cars) {
        // Set data to game state
        gameStateBuffer.put(Protocol.GAME_STATE);
        gameStateBuffer.put((byte) cars.size());
        for ( RWDCar car : cars ) {
            gameStateBuffer.putDouble(car.getX());
            gameStateBuffer.putDouble(car.getY());
            gameStateBuffer.putDouble(car.getAngle());
            gameStateBuffer.putDouble(car.getSpeed());
        }
        byte[] data = gameStateBuffer.array();

        // Send the data to all the players
        for (InetAddress playerAddress : players.keySet()) {
            sendPacket(playerAddress, data);
        }

        // Clean byte buffer memory
        gameStateBuffer.clear();
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public static void main(String[] args) {
        try {
            (new NewServer()).run();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}