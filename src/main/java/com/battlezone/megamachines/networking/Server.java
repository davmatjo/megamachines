package com.battlezone.megamachines.networking;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.world.track.Track;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.*;

public class Server {

    // Constants
    public static final int MAX_PLAYERS = 3; // TODO: fix bug to make it work with 8 or more
    static final int PORT = 6970;
    public static final int SERVER_TO_CLIENT_LENGTH = 300;
    public static final int CLIENT_TO_SERVER_LENGTH = 14;
    public static final int GAME_STATE_EACH_LENGTH = 34;

    // TCP Server
    private final ServerSocket socket;

    // Whole server variables
    private Map<Byte, GameRoom> rooms;
    private List<Byte> roomsToDelete = new ArrayList<>();
    public InetAddress host;
    private byte[] received;
    private byte roomCount = 0;
    private boolean running = true;

    // Lobby variables
    private Map<InetAddress, Player> players = new HashMap<>();
    private List<RWDCar> cars = new ArrayList<>();
    private List<WaitingPlayer> waitingPlayers = new ArrayList<>();
    private List<WaitingPlayer> toDeletePlayers = new ArrayList<>();

    public Server() throws IOException {
        this.socket = new ServerSocket(PORT);
        this.rooms = new HashMap<>();
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void run() {
        while (running) {
            try {
                // Listen to new connections
                Socket conn = socket.accept();
                ObjectInputStream inputStream = new ObjectInputStream(conn.getInputStream());
                received = (byte[]) inputStream.readObject();

                // Clean lost players
                clean();

                // Handle if player wants to join lobby
                if (received[0] == Protocol.JOIN_LOBBY) {
                    if ( players.isEmpty() ) host = conn.getInetAddress();
                    WaitingPlayer player = new WaitingPlayer(conn, this, inputStream, new ObjectOutputStream(conn.getOutputStream()));
                    waitingPlayers.add(player);
                    (new Thread(player)).start();

                    Player newPlayer = new Player((int) received[1], Vector3f.fromByteArray(received, 2));
                    players.put(conn.getInetAddress(), newPlayer);
                    cars.add(players.get(conn.getInetAddress()).getCar());
                    sendPlayers(cars);
                }
                // Handle starting game
                if ( players.size() == MAX_PLAYERS )
                    startGame();

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void clean() {
        // Remove lost players
        for ( WaitingPlayer player : waitingPlayers )
            if ( !player.getRunning() ) {
                cars.remove(players.get(player.getAddress()).getCar());
                players.remove(player.getAddress());
                toDeletePlayers.add(player);
            }
        for ( WaitingPlayer player : toDeletePlayers ) {
            waitingPlayers.remove(player);
            System.out.println("Removed player " + player.getAddress());
        }
        toDeletePlayers.clear();

        // Remove empty game rooms
        for ( Byte b : rooms.keySet() )
            roomsToDelete.add(b);
        for ( Byte b : roomsToDelete ) {
            System.out.println(2);
            rooms.get(b).close();
            rooms.remove(rooms.get(b));
            System.out.println("Emptied room " + b);
        }
        roomsToDelete.clear();
    }

    public void startGame() throws IOException {
        GameRoom room = new GameRoom(this, new HashMap(players), MAX_PLAYERS - players.size(), roomCount);
        this.rooms.put(roomCount, room);
        new Thread(room).start();
        resetLobby();
    }

    public void resetLobby() {
        roomCount = (byte) ((roomCount + 2) % 256);
        players.clear();
        waitingPlayers.forEach(x -> x.close());
        waitingPlayers.clear();
        cars.clear();
    }

    public void sendPlayers(List<RWDCar> cars) {
        byte[] buffer = ByteBuffer.allocate(3+cars.size()*RWDCar.BYTE_LENGTH).put(Protocol.PLAYER_INFO).put(RWDCar.toByteArray(cars)).array();
        int i = 0;
        for ( WaitingPlayer player : waitingPlayers ) {
            buffer[2] = (byte)i++;
            sendTCP(player.getOutputStream(), buffer);
        }
    }

    public void createAndSendTrack(Game game) {
        Track track = game.getTrack();
        byte[] buffer = ByteBuffer.allocate(track.getTracksAcross()*track.getTracksDown()+5).put(Protocol.TRACK_TYPE).put(track.toByteArray()).array();
        waitingPlayers.forEach(x -> sendTCP(x.getOutputStream(), buffer));
    }

    private void sendTCP(ObjectOutputStream address, byte[] data) {
        try {
            address.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPortToAll() {
        byte[] buffer = ByteBuffer.allocate(2).put(Protocol.UDP_DATA).put(this.roomCount).array();
        waitingPlayers.forEach(x -> sendTCP(x.getOutputStream(), buffer));
    }

    public static void main(String[] args) {
        try {
            (new Server()).run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}