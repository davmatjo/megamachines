package com.battlezone.megamachines.networking;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.world.track.Track;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.*;

public class NewServer {

    // Constants
    public static final int MAX_PLAYERS = 7; // TODO: fix bug to make it work with 8 or more
    static final int PORT = 6970;
    public static final int SERVER_TO_CLIENT_LENGTH = 300;
    public static final int CLIENT_TO_SERVER_LENGTH = 14;

    // Server stuff
    private final DatagramSocket socket;
    private final DatagramPacket receive;
    private final DatagramPacket send;

    // Variables
    private boolean running = true;
    private InetAddress host;
    byte[] received;
    public Map<Byte, GameRoom> rooms;
    private byte roomCount = 0;

    public NewServer() throws SocketException {
        this.socket = new DatagramSocket(PORT);
        this.receive = new DatagramPacket(new byte[CLIENT_TO_SERVER_LENGTH], CLIENT_TO_SERVER_LENGTH);
        this.send = new DatagramPacket(new byte[SERVER_TO_CLIENT_LENGTH], SERVER_TO_CLIENT_LENGTH, null, Client.PORT);
        this.rooms = new HashMap<>();
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void run() {
        Map<InetAddress, Player> players = new HashMap<>();

        while (running) {
            try {
                socket.receive(receive);
                received = receive.getData();

                // Handle if player wants to join lobby
                if (received[0] == Protocol.JOIN_LOBBY) {
                    if ( players.isEmpty() ) host = receive.getAddress();
                    if ( players.size() < MAX_PLAYERS ) {
                        players.put(receive.getAddress(), new Player((int) received[1], Vector3f.fromByteArray(received, 2)));

                        List<RWDCar> cars = new ArrayList<>();
                        players.values().forEach(player -> cars.add(player.getCar()));
                        sendPlayers(players, cars);
                    }
                }
                if ( received[0] == Protocol.START_GAME && receive.getAddress().equals(host) ) {
                    GameRoom room = new GameRoom(this, players, MAX_PLAYERS - players.size(), roomCount);
                    this.rooms.put(roomCount, room);
                    new Thread(room).start();
                    roomCount = (byte) ((roomCount + 2) % 100);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendPlayers(Map<InetAddress, Player> players, List<RWDCar> cars) {
        byte[] buffer = ByteBuffer.allocate(3+cars.size()*13).put(Protocol.PLAYER_INFO).put(RWDCar.toByteArray(cars)).array();
        int i = 0;
        for ( InetAddress client : players.keySet() ) {
            buffer[2] = (byte)i++;
            sendPacket(client, buffer);
        }
    }

    public void createAndSendTrack(Game game, Map<InetAddress, Player> players) throws IOException {
        Track track = game.getTrack();
        byte[] buffer = ByteBuffer.allocate(track.getTracksAcross()*track.getTracksDown()+5).put(Protocol.TRACK_TYPE).put(track.toByteArray()).array();
        for ( InetAddress a : players.keySet() ) {
            send.setAddress(a);
            sendPacket(a, buffer);
        }
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

    public void sendPortToAll(Map<InetAddress, Player> players) throws IOException {
        byte[] buffer = ByteBuffer.allocate(2).put(Protocol.UDP_DATA).put(this.roomCount).array();
        send.setData(buffer);
        for ( InetAddress a : players.keySet() ) {
            send.setAddress(a);
            socket.send(send);
        }
    }

    public static void main(String[] args) {
        try {
            (new NewServer()).run();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}