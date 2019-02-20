package com.battlezone.megamachines.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class PlayerConnection implements Runnable {

    // TCP connection
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Socket conn;
    byte[] received;

    // Variables
    private boolean running = true;
    private LobbyRoom lobbyRoom;

    private LobbyRoom connectionDroppedListener;

    public PlayerConnection(Socket conn, ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        this.conn = conn;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public void close() {
        this.running = false;
    }

    public boolean getRunning() {
        return this.running;
    }

    public ObjectOutputStream getOutputStream() {
        return this.outputStream;
    }

    public InetAddress getAddress() {
        return this.conn.getInetAddress();
    }

    public void run() {
        while (running) {
            received = new byte[Client.CLIENT_TO_SERVER_LENGTH];
            try {
                received = (byte[]) inputStream.readObject();

                if (received[0] == Protocol.START_GAME && this.conn.getInetAddress().equals(lobbyRoom.getHost())) {
                    lobbyRoom.startGame();
                }

            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Connection to player lost: " + conn.getInetAddress());
                running = false;
            }

        }
        if (connectionDroppedListener != null) {
            connectionDroppedListener.clean(conn.getInetAddress());
        }
    }

    public void setLobbyAndStart(LobbyRoom lobbyRoom) {
        this.lobbyRoom = lobbyRoom;
        (new Thread(this)).start();
    }

    void setConnectionDroppedListener(LobbyRoom r) {
        this.connectionDroppedListener = r;
    }
}
