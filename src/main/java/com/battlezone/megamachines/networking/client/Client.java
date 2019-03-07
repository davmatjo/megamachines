package com.battlezone.megamachines.networking.client;

import com.battlezone.megamachines.events.game.*;
import com.battlezone.megamachines.events.keys.KeyEvent;
import com.battlezone.megamachines.events.ui.ErrorEvent;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.networking.Protocol;
import com.battlezone.megamachines.networking.server.Server;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.storage.Storage;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.generator.TrackLoopMutation2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Client implements Runnable {

    // Server variables
    public static final int CLIENT_TO_SERVER_LENGTH = 15;
    private static final int PORT = 6970;
    private ByteBuffer byteBuffer;
    private final byte[] toServerData;
    private byte[] fromServerData;

    // Server UDP connection
    private DatagramSocket inGameSocket;
    private final DatagramPacket fromServer;
    private DatagramPacket toServer;

    // Server TCP connection
    private Socket clientSocket;
    private ObjectOutputStream outToServer;

    // Variables
    private boolean running = true;
    private byte roomNumber;
    private byte clientPlayerNumber;
    private Track sentTrack;


    public Client(InetAddress serverAddress, byte roomNumber) throws IOException {
        this.roomNumber = roomNumber;

        byte carModelNumber = (byte) Storage.getStorage().getInt(Storage.CAR_MODEL, 1);
        Vector3f colour = Storage.getStorage().getVector3f(Storage.CAR_COLOUR, new Vector3f(1, 1, 1));

        clientSocket = new Socket(serverAddress, PORT);
        outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
        toServerData = new byte[CLIENT_TO_SERVER_LENGTH];
        this.toServer = new DatagramPacket(toServerData, CLIENT_TO_SERVER_LENGTH, serverAddress, Server.PORT);

        byte[] fromServer = new byte[Server.SERVER_TO_CLIENT_LENGTH];
        this.fromServer = new DatagramPacket(fromServer, Server.SERVER_TO_CLIENT_LENGTH);

        // Send a JOIN_GAME packet
        byteBuffer = ByteBuffer.allocate(CLIENT_TO_SERVER_LENGTH).put(Protocol.JOIN_LOBBY).put(roomNumber).put(carModelNumber).put(colour.toByteArray());
        try {
            outToServer.writeObject(byteBuffer.array());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        byteBuffer.rewind();
        new Thread(this).start();
        MessageBus.register(this);
    }

    public void setTrack(Track sentTrack) {
        this.sentTrack = sentTrack;
    }

    public void setRoomNumber(byte roomNumber) {
        this.roomNumber = roomNumber;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());

            while ( running ) {

                // While in lobby
                while (running) {
                    fromServerData = (byte[]) inputStream.readObject();

                    if (fromServerData[0] == Protocol.PLAYER_INFO) {
                        clientPlayerNumber = fromServerData[2];
                        MessageBus.fire(new PlayerUpdateEvent(Arrays.copyOf(fromServerData, fromServerData.length), fromServerData[2], false));
                    } else if (fromServerData[0] == Protocol.TRACK_TYPE) {
                        byte[] powerupManagerArray = (byte[]) inputStream.readObject();
                        byte[] newArray = new byte[powerupManagerArray.length-1];

                        System.arraycopy(powerupManagerArray, 1, newArray, 0, newArray.length);
                        MessageBus.fire(new TrackUpdateEvent(Arrays.copyOf(fromServerData, fromServerData.length), Arrays.copyOf(newArray, newArray.length)));
                        break;
                    } else if (fromServerData[0] == Protocol.UDP_DATA) {
                        MessageBus.fire(new PortUpdateEvent(Arrays.copyOf(fromServerData, fromServerData.length)));
                    } else if (fromServerData[0] == Protocol.FAIL_CREATE) {
                        MessageBus.fire(new FailRoomEvent(Arrays.copyOf(fromServerData, fromServerData.length)));
                    } else {
                        throw new RuntimeException("Received unexpected packet");
                    }
                }

                // Wait for creation of World in Lobby
                try {
                    synchronized (this) {
                        this.wait(3000);
                    }
                } catch (InterruptedException e) {
                    System.err.println("Timed out waiting for notification of world creation");
                }

                // While in game
                roomNumber *= 2;
                if ( inGameSocket != null )
                    inGameSocket.close();
                inGameSocket = new DatagramSocket(roomNumber + Protocol.DEFAULT_PORT + 1);
                toServer.setPort(roomNumber + Protocol.DEFAULT_PORT);
                while (running) {
                    inGameSocket.receive(fromServer);
                    fromServerData = fromServer.getData();

                    if (fromServerData[0] == Protocol.GAME_STATE) {
                        GameUpdateEvent packetBuffer = GameUpdateEvent.create(fromServerData);
                        MessageBus.fire(packetBuffer);
                    } else if (fromServerData[0] == Protocol.GAME_COUNTDOWN) {
                        System.out.println("Countdown packet");
                        String countdown = Byte.toString(fromServerData[1]);
                        MessageBus.fire(new ErrorEvent("GET READY", countdown.equals("0") ? "GO" : countdown, 1, Colour.GREEN));
                    } else if (fromServerData[0] == Protocol.END_RACE) {
                        System.out.println("Game ending on client");
                        break;
                    } else if (fromServerData[0] == Protocol.POWERUP_EVENT) {
                        MessageBus.fire(new PowerupTriggerEvent(Arrays.copyOf(fromServerData, 3)));
                    } else {
                        throw new RuntimeException("Received unexpected packet" + Arrays.toString(fromServerData));
                    }
                }

                // After game has ended. wait for packets regarding leaderboard and ending the game to go back to the lobby
                while (running) {
                    fromServerData = (byte[]) inputStream.readObject();

                    if (fromServerData[0] == Protocol.END_RACE) {
                        List<Integer> leaderboard = new ArrayList<>();
                        for (int i = 0; i < Server.MAX_PLAYERS * Server.END_GAME_STATE_PLAYER; i += Server.END_GAME_STATE_PLAYER) {
                            int position = fromServerData[1 + i] + 1;
                            leaderboard.add(position);
                        }

                        // Find winner
                        int winnerNumber = 0;
                        for ( int i = 0; i < leaderboard.size(); i++ )
                            if ( leaderboard.get(i) == 1 ) {
                                winnerNumber = i;
                                break;
                            }

                        MessageBus.fire(new ErrorEvent("PLAYER" + winnerNumber + " WON!", "YOUR POSITION: " + leaderboard.get(clientPlayerNumber), 4, Colour.GREEN));
                    } else if (fromServerData[0] == Protocol.END_GAME) {
                        MessageBus.fire(new GameEndEvent());
                        break;
                    } else if (fromServerData[0] == Protocol.PLAYER_INFO) {
                        MessageBus.fire(new PlayerUpdateEvent(Arrays.copyOf(fromServerData, fromServerData.length), fromServerData[2], false));
                    } else {
                        throw new RuntimeException("Received unexpected packet" + Arrays.toString(fromServerData));
                    }
                }
                System.out.println(running);
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            close();
        }
    }

    @EventListener
    public void keyPressRelease(KeyEvent event) {
        try {
            toServerData[0] = Protocol.KEY_EVENT;
            toServerData[1] = event.getPressed() ? Protocol.KEY_PRESSED : Protocol.KEY_RELEASED;
            fillKeyData(toServerData, event.getKeyCode());

            toServer.setData(toServerData);
            inGameSocket.send(toServer);
        } catch (IOException e) {
            System.err.println("Error sending keypress " + e.getMessage());
        }
    }

    public void close() {
        this.running = false;
        try {
            clientSocket.close();
            if ( inGameSocket != null )
                inGameSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startGame() {
        // Send start game
        toServerData[0] = Protocol.START_GAME;
        try {
            outToServer.writeObject(toServerData);

            // Then send the track
            sentTrack = new TrackLoopMutation2(20, 20).generateTrack();  // TODO: ELIMINATE THIS SHIT WHEN TRACK IS SET BY HOST AUTOMATICALLY IN LOBBY MENU
            outToServer.writeObject(sentTrack.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fillKeyData(byte[] data, int keyCode) {
        data[2] = (byte) (keyCode & 0xff);
        data[3] = (byte) ((keyCode >> 8) & 0xff);
        data[4] = (byte) ((keyCode >> 16) & 0xff);
        data[5] = (byte) ((keyCode >> 24) & 0xff);
    }
}
