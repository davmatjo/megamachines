package com.battlezone.megamachines.networking.server;

import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.networking.secure.Encryption;
import com.battlezone.megamachines.networking.secure.Protocol;
import com.battlezone.megamachines.networking.server.lobby.LobbyRoom;
import com.battlezone.megamachines.networking.server.player.Player;
import com.battlezone.megamachines.networking.server.player.PlayerConnection;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.util.AssetManager;

import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public final class Server {

    // Constants
    public static final int MAX_PLAYERS = 8;
    public static final int PORT = 6970;
    public static final int GAME_STATE_EACH_LENGTH = 101;
    public static final int SERVER_TO_CLIENT_LENGTH = GAME_STATE_EACH_LENGTH * MAX_PLAYERS + 3;
    public static final int END_GAME_STATE_PLAYER = 1;
    public static final int ROOMS_AVAILABLE = 128;
    private static final byte ROOM_FAIL = -1;
    // Server
    public static Server server;
    // Lobby data
    private static Map<Byte, LobbyRoom> lobbyRooms = new HashMap<>();
    // TCP Server
    private final ServerSocket socket;
    // Whole server variables
    private byte[] received;
    private boolean running = true;


    public Server() throws IOException {
        this.socket = new ServerSocket(PORT);
    }

    public static void resetLobby(LobbyRoom lobbyRoom) {
        System.out.println("Resetting lobby: " + lobbyRoom.getRoomNumber());
        lobbyRooms.remove(lobbyRoom.getRoomNumber());
    }

    public static void main(String[] args) {
        AssetManager.setIsHeadless(true);
        try {
            new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    String s = scanner.nextLine();
                    if (s == null || s.toLowerCase().equals("q") || s.toLowerCase().equals("quit")
                            || s.toLowerCase().equals("stop") || s.toLowerCase().equals("s")) {
                        System.exit(0);
                    }
                }
            }).start();
            server = new Server();
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void run() {
        // Add Cleaner to Server
//        ServerCleaner cleaner = new ServerCleaner();
//        (new Thread(cleaner)).start();

        // Setup encryption
        try {
            Encryption.setUp();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // Run
        while (running) {
            try {
                // Listen to new connections
                Socket conn = socket.accept();
                ObjectInputStream inputStream = new ObjectInputStream(conn.getInputStream());
                received = Encryption.decrypt((byte[]) inputStream.readObject());

                LobbyRoom lobbyRoom;

                // Handle room
                byte roomNumber = received[1];
                if (lobbyRooms.containsKey(roomNumber) && lobbyRooms.get(roomNumber).isGameRunning())
                    roomNumber = roomAvailable();

                // If no room available, send failed to connection
                if (roomNumber == ROOM_FAIL) {
                    roomConnectionFail(new ObjectOutputStream(conn.getOutputStream()), conn);
                    continue;
                }

                // Handle if player wants to join lobby
                if (received[0] == Protocol.JOIN_LOBBY) {
                    // Add new player to lobby room
                    var name = new String(Encryption.decrypt((byte[]) inputStream.readObject()));
                    PlayerConnection playerConn = new PlayerConnection(conn, inputStream, new ObjectOutputStream(conn.getOutputStream()));
                    Player newPlayer = new Player((int) received[2], Colour.convertToCarColour(Vector3f.fromByteArray(received, 3)), playerConn, name);


                    // If the lobby room did not exist before
                    if (!lobbyRooms.containsKey(roomNumber)) {
                        lobbyRooms.put(roomNumber, new LobbyRoom(roomNumber, newPlayer.getConnection().getAddress()));
                        System.out.println("Created new lobby room " + roomNumber);
                    }

                    // Get lobby room
                    lobbyRoom = lobbyRooms.get(roomNumber);

                    // Set player connection lobby and start listening
                    playerConn.setLobbyAndStart(lobbyRoom);
                    lobbyRoom.updatePlayerData(conn.getInetAddress(), newPlayer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // If Server ended, close all lobbies
        for (byte i = -128; i <= 127; i++)
            if (lobbyRooms.containsKey(i)) {
                lobbyRooms.get(i).close();
                System.out.println("Room " + i + " has been stopped. ");
            }

//        cleaner.close();
    }

    private void roomConnectionFail(ObjectOutputStream objectOutputStream, Socket conn) throws IOException {
        byte[] buffer = new byte[1];
        buffer[0] = Protocol.FAIL_CREATE;
        objectOutputStream.writeObject(buffer);
        conn.close();
    }

    private byte roomAvailable() {
        if (ROOMS_AVAILABLE == 0)
            return ROOM_FAIL;

        byte roomCount = 0;
        while (lobbyRooms.containsKey(roomCount))
            if (roomCount == ROOMS_AVAILABLE)
                return ROOM_FAIL;
            else
                roomCount = (byte) ((roomCount + 1) % ROOMS_AVAILABLE);
        return roomCount;
    }

    public void close() {
        this.running = false;
    }
}