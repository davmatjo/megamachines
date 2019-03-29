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

    byte[] received;
    // TCP connection
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Socket conn;
    // Variables
    private boolean running = true;
    private LobbyRoom lobbyRoom;
    private LobbyRoom connectionDroppedListener;

    /*
     * Main constructor of the Player connection class.
     *
     * @param Socket             Socket to connect to
     * @param ObjectInputStream  Input stream of the player
     * @param ObjectOutputStream Output stream of the player
     * */
    public PlayerConnection(Socket conn, ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        this.conn = conn;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    /*
     * Method to close the connection to the player.
     * */
    public void close() {
        this.running = false;
    }

    /*
     * Method to see if connection is on.
     *
     * @return boolean   True if connection is running and false otherwise
     * */
    public boolean getRunning() {
        return this.running;
    }

    /*
     * Method to get the output stream of the player.
     *
     * @param ObjectOutputStream     Output stream to be returned
     * */
    public ObjectOutputStream getOutputStream() {
        return this.outputStream;
    }

    /*
     * Method to get the internet address of the player connection.
     *
     * @return InetAddress   Address of the player to be returned
     * */
    public InetAddress getAddress() {
        return this.conn.getInetAddress();
    }

    /*
     * Method to run when player connection Thread is being run.
     * */
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

    /*
     * Method to set and start lobby of the player connection.
     *
     * @param LobbyRoom  Lobby to be set for the player connection
     * */
    public void setLobbyAndStart(LobbyRoom lobbyRoom) {
        this.lobbyRoom = lobbyRoom;
        (new Thread(this)).start();
    }

    /*
     * Set connection if listener dropped method.
     *
     * @param LobbyRoom  Lobby of the player connection to be newly added for the player connection
     * */
    public void setConnectionDroppedListener(LobbyRoom r) {
        this.connectionDroppedListener = r;
    }
}
