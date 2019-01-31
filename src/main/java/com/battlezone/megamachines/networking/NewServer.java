package com.battlezone.megamachines.networking;

import com.battlezone.megamachines.math.Vector3f;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.copyOfRange;

public class NewServer implements Runnable {

    // Define the states of the server
    private enum State{LOBBY, IN_GAME}

    public NewServer() throws SocketException {
        this.socket = new DatagramSocket();
        this.receive = new DatagramPacket(new byte[14], 6);
        this.send = new DatagramPacket(new byte[258], 258);
    }

    // Define constant server event types for Client to Server packets -> on byte 0
    private static final byte JOIN_LOBBY = 0;
    private static final byte START_GAME = 1;
    private static final byte KEY_EVENT = 2;

    // Define constant key event types for Client to Server packets -> on byte 1
    private static final byte KEY_PRESSED = 0;
    private static final byte KEY_RELEASED = 1;

    // Define constant for types of packets for Server to Client -> on byte 0
    private static final byte GAME_STATE = 0;
    private static final byte TRACK_TYPE = 1;

    // Define other constants
    private static final int MAX_PLAYERS = 8;

    private boolean running = true;
    private State currentState = State.LOBBY;
    private final DatagramSocket socket;
    private final DatagramPacket receive;
    private final DatagramPacket send;
    private final Map<InetAddress, Player> players = new HashMap<>();
    private InetAddress host;

    @Override
    public void run() {

        while (running) {
            try {
                socket.receive(receive);
                byte[] received = receive.getData();
                if (received[0] == JOIN_LOBBY) {
                    // Make the first player as the host
                    if ( players.isEmpty() )
                        host = receive.getAddress();

                    // Add new players to the player ArrayList if there's less than MAX_PLAYERS amount
                    if ( players.size() < MAX_PLAYERS )
                        players.put(receive.getAddress(), new Player((int) received[1], Vector3f.fromByteArray(received, 2)));
                } if ( received[0] == START_GAME && receive.getAddress().equals(host) ) {
                    currentState = State.IN_GAME;

                    runGame();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void runGame() throws IOException {
        while (running) {
            // Receive the package
            socket.receive(receive);
            byte[] data = receive.getData();

            // Case when packet specifies key info
            if (data[0] == KEY_EVENT) {
                Player current = players.get(receive.getAddress());

                // Process the key
                int eventKeyCode = ByteBuffer.wrap(copyOfRange(data, 2, 6)).getInt();

                // Check whether pressed/released
                if ( data[1] == KEY_PRESSED ) {
                    // TODO: press the eventKeyCode in PhysicsEngine
                } else if ( data[1] == KEY_RELEASED ) {
                    // TODO: release the eventKeyCode in PhysicsEngine
                }
            }
        }
    }

    public void sendGameStatePackets() {
        // Set data to game state
        final int GAME_STATE_DATA_LENGTH = 258;
        byte[] data = new byte[GAME_STATE_DATA_LENGTH];
        data[0] = GAME_STATE;
        data[1] = (byte) players.size();
        for ( int i = 0; i < players.size(); i++ ) {
            byte[] x = ByteBuffer.allocate(8).putDouble(players.get(i).getCar().getX()).array();
            byte[] y = ByteBuffer.allocate(8).putDouble(players.get(i).getCar().getY()).array();
            byte[] angle = ByteBuffer.allocate(8).putDouble(players.get(i).getCar().getAngle()).array();
            byte[] speed = ByteBuffer.allocate(8).putDouble(players.get(i).getCar().getSpeed()).array();
            System.arraycopy(x, 0, data, 2 + i*32, 8);
            System.arraycopy(y, 0, data, 2 + i*32 + 8, 8);
            System.arraycopy(angle, 0, data, 2 + i*32 + 16, 8);
            System.arraycopy(speed, 0, data, 2 + i*32 + 24, 8);
        }
        send.setData(data);

        // Send the data to all the players
        for (var player : players.keySet()) {
            try {
                send.setAddress(player);
                socket.send(send);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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