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
    private Server server;

    // Variables
    private boolean running = true;

    public PlayerConnection(Socket conn, Server server, ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        this.conn = conn;
        this.server = server;
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
        while ( running ) {
            byte[] received = new byte[Client.CLIENT_TO_SERVER_LENGTH];
            try {
                received = (byte[]) inputStream.readObject();
            } catch (IOException e) {
                close();
            } catch (ClassNotFoundException e) {
                close();
            }
            if ( received[0] == Protocol.START_GAME && conn.getInetAddress().equals(server.host) ) {
                try {
                    server.startGame();
                } catch (IOException e) {
                    close();
                }
            }
        }
    }
}
