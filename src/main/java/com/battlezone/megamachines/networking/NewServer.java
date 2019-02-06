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
import static java.util.Arrays.copyOfRange;

public class NewServer {

    public NewServer() throws SocketException {
        this.socket = new DatagramSocket(PORT);
        this.receive = new DatagramPacket(new byte[14], 6);
        this.send = new DatagramPacket(new byte[258], 258);
    }



    // Define other constants
    private static final int MAX_PLAYERS = 8;

    // Define final port
    static final int PORT = 6969;

    public static final int SERVER_TO_CLIENT_LENGTH = 300;

    private boolean running = true;
    private Protocol.State currentState = Protocol.State.LOBBY;
    private final DatagramSocket socket;
    private final DatagramPacket receive;
    private final DatagramPacket send;
    private InetAddress host;
    
    public void run() {
        Map<InetAddress, Player> players = new HashMap<>();

        while (running) {
            try {
                socket.receive(receive);
                byte[] received = receive.getData();
                if (received[0] == Protocol.JOIN_LOBBY) {
                    // Make the first player as the host
                    if ( players.isEmpty() )
                        host = receive.getAddress();
                    System.out.println(Arrays.toString(received));
                    // Add new players to the player ArrayList if there's less than MAX_PLAYERS amount
                    if ( players.size() < MAX_PLAYERS )
                        players.put(receive.getAddress(), new Player((int) received[1], Vector3f.fromByteArray(received, 2)));
                } if ( received[0] == Protocol.START_GAME && receive.getAddress().equals(host) ) {
                    currentState = Protocol.State.IN_GAME;
                    initGame(players);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initGame(Map<InetAddress, Player> players) throws IOException {

        Game game = new Game(players, this);
        new Thread(game).start();

        // Send track info
        Track track = game.getTrack();
        send.setData(ByteBuffer.allocate(track.getTracksAcross()*track.getTracksDown()+5).put(Protocol.TRACK_TYPE).put(track.toByteArray()).array());
        socket.send(send);
        // Send players info
        List<RWDCar> cars = new ArrayList<>();
        players.values().forEach(player -> cars.add(player.getCar()));
        send.setData(ByteBuffer.allocate(3+cars.size()*13).put(Protocol.PLAYER_INFO).put(RWDCar.toByteArray(cars)).array());
        socket.send(send);

        while (running) {
            // Receive the package
            socket.receive(receive);
            byte[] data = receive.getData();

            // Case when packet specifies key info
            if (data[0] == KEY_EVENT) {
                // Process the key
                int eventKeyCode = ByteBuffer.wrap(copyOfRange(data, 2, 6)).getInt();

                game.keyPress(new NetworkKeyEvent(eventKeyCode, data[1] == KEY_PRESSED, receive.getAddress()));
            }
        }
    }

//    public void sendTrackInfo(Track track) {
//        // Set the buffer to the track info
//        byte[] buffer = new byte[300];
//        buffer[0] = TRACK_TYPE;
//        send.setData(buffer);
//
//        // Send the track info to every player
//        for ( var playerAddress : players.keySet() ) {
//            send.setAddress(playerAddress);
//            try {
//                socket.send(send);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public void sendPacket(InetAddress address, byte[] data) {
        try {
            send.setAddress(address);
            send.setData(data);
            socket.send(send);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendGameState(Map<InetAddress, Player> players) {
        // Set data to game state
        ByteBuffer byteBuffer = ByteBuffer.allocate(players.size()*32+2);
        byteBuffer.put(Protocol.GAME_STATE);
        byteBuffer.put((byte) players.size());
        for ( InetAddress i : players.keySet() ) {
            byteBuffer.putDouble(players.get(i).getCar().getX());
            byteBuffer.putDouble(players.get(i).getCar().getY());
            byteBuffer.putDouble(players.get(i).getCar().getAngle());
            byteBuffer.putDouble(players.get(i).getCar().getSpeed());
        }
        byte[] data = byteBuffer.array();

        // Send the data to all the players
        for (var playerAddress : players.keySet()) {
            sendPacket(playerAddress, data);
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