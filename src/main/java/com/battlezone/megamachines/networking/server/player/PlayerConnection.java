package com.battlezone.megamachines.networking.server.player;

import com.battlezone.megamachines.networking.client.Client;
import com.battlezone.megamachines.networking.secure.Encryption;
import com.battlezone.megamachines.networking.secure.Protocol;
import com.battlezone.megamachines.networking.server.lobby.LobbyRoom;
import com.battlezone.megamachines.renderer.theme.Theme;
import com.battlezone.megamachines.renderer.theme.ThemeHandler;
import com.battlezone.megamachines.world.track.Track;

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
                received = ((byte[]) inputStream.readObject());
                received = Encryption.decrypt(received);

                if (received[0] == Protocol.START_GAME && this.conn.getInetAddress().equals(lobbyRoom.getHost())) {
                    // Get the track
                    byte[] trackArray = Encryption.decrypt((byte[]) inputStream.readObject());
                    lobbyRoom.setTrack(Track.fromByteArray(trackArray, 0));

                    // Get type of theme
                    lobbyRoom.setThemeByte((byte) inputStream.readObject());
                    ThemeHandler.setTheme(Theme.values()[lobbyRoom.getThemeByte()]);

                    // Get lap counter and set it too
                    byte lapCounter = (byte) inputStream.readObject();
                    lobbyRoom.setLapCounter(lapCounter);

                    // Start game
                    lobbyRoom.startGame();
                }

            } catch (Exception e) {
                e.printStackTrace();
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

    public void setConnectionDroppedListener(LobbyRoom r) {
        this.connectionDroppedListener = r;
    }
}
