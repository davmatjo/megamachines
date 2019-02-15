package com.battlezone.megamachines.networking;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.world.track.Track;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

    // Constants
    public static final int MAX_PLAYERS = 8; // TODO: fix bug to make it work with 8 or more
    static final int PORT = 6970;
    public static final int SERVER_TO_CLIENT_LENGTH = 300;
    public static final int GAME_STATE_EACH_LENGTH = 34;
    public static final int ROOMS_AVAILABLE = 256; // DIVIDED BY 2

    // TCP Server
    private final ServerSocket socket;

    // Whole server variables
    private Map<Byte, GameRoom> rooms;
    private List<Byte> roomsToDelete = new ArrayList<>();
    public InetAddress host;
    private byte[] received;
    private boolean running = true;

    // Lobby variables
    private Map<InetAddress, Player> players = new HashMap<>();
    private List<RWDCar> cars = new ArrayList<>();
    private List<PlayerConnection> playerConnections = new ArrayList<>();
    private List<PlayerConnection> toDeletePlayers = new ArrayList<>();

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
                    if ( rooms.containsKey(received[1]) ) {
                        // Connect to the first available room
                        if ( players.isEmpty() ) host = conn.getInetAddress();
                        PlayerConnection player = new PlayerConnection(conn, this, inputStream, new ObjectOutputStream(conn.getOutputStream()));
                        playerConnections.add(player);
                        (new Thread(player)).start();

                        Player newPlayer = new Player((int) received[2], Vector3f.fromByteArray(received, 3));
                        players.put(conn.getInetAddress(), newPlayer);
                        cars.add(players.get(conn.getInetAddress()).getCar());
                        sendPlayers(cars);
                    } else {
                        // Connect to the room he wants to connect
                        if ( players.isEmpty() ) host = conn.getInetAddress();
                        PlayerConnection player = new PlayerConnection(conn, this, inputStream, new ObjectOutputStream(conn.getOutputStream()));
                        playerConnections.add(player);
                        (new Thread(player)).start();

                        Player newPlayer = new Player((int) received[2], Vector3f.fromByteArray(received, 3));
                        players.put(conn.getInetAddress(), newPlayer);
                        cars.add(players.get(conn.getInetAddress()).getCar());
                        sendPlayers(cars);
                    }
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
        for ( PlayerConnection player : playerConnections)
            if ( !player.getRunning() ) {
                cars.remove(players.get(player.getAddress()).getCar());
                players.remove(player.getAddress());
                toDeletePlayers.add(player);
            }
        for ( PlayerConnection player : toDeletePlayers ) {
            playerConnections.remove(player);
        }
        toDeletePlayers.clear();

        // Remove empty game rooms
        for ( Byte b : rooms.keySet() )
            if ( rooms.get(b).stillRunning() == false )
                roomsToDelete.add(b);
        for ( Byte b : roomsToDelete ) {
            rooms.get(b).close();
            rooms.remove(b);
        }
        roomsToDelete.clear();
    }

    public void startGame() throws IOException {
        byte roomAvailable = this.roomAvailable();
        // If server can launch game
        if ( roomAvailable == 0 ) {
            GameRoom room = new GameRoom(this, new HashMap(players), MAX_PLAYERS - players.size(), roomAvailable, playerConnections);
            this.rooms.put(roomAvailable, room);
            new Thread(room).start();
            resetLobby();
        } else if ( roomAvailable == -1 ) {
            // Server can't launch game
            sendFailed(); 
            resetLobby();
        }
    }

    private byte roomAvailable() {
        byte roomCount = 0;
        do {
            if ( !rooms.containsKey(roomCount) ) break;

            roomCount = (byte) ((roomCount + 2) % ROOMS_AVAILABLE);

            // In case no rooms are empty
            if ( roomCount == 0 ) {
                return -1;
            }
        } while ( rooms.containsKey(roomCount) );
        return roomCount;
    }

    public void resetLobby() {
        players.clear();
        playerConnections.forEach(x -> x.close());
        playerConnections.clear();
        cars.clear();
    }

    private void sendFailed() {
        byte[] buffer = new byte[1];
        buffer[0] = Protocol.FAIL_CREATE;
        for ( PlayerConnection player : playerConnections)
            sendTCP(player.getOutputStream(), buffer);
    }

    public void sendPlayers(List<RWDCar> cars) {
        byte[] buffer = ByteBuffer.allocate(3+cars.size()*RWDCar.BYTE_LENGTH).put(Protocol.PLAYER_INFO).put(RWDCar.toByteArray(cars)).array();
        int i = 0;
        for ( PlayerConnection player : playerConnections) {
            buffer[2] = (byte)i++;
            sendTCP(player.getOutputStream(), buffer);
        }
    }

    public void createAndSendTrack(Game game) {
        Track track = game.getTrack();
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

    public void sendPortToAll(byte roomCount) {
        byte[] buffer = ByteBuffer.allocate(2).put(Protocol.UDP_DATA).put(roomCount).array();
        playerConnections.forEach(x -> sendTCP(x.getOutputStream(), buffer));
    }

    public static void main(String[] args) {
        try {
            (new Server()).run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}