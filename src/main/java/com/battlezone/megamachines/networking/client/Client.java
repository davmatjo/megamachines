package com.battlezone.megamachines.networking.client;

import com.battlezone.megamachines.ai.Driver;
import com.battlezone.megamachines.events.game.*;
import com.battlezone.megamachines.events.keys.KeyEvent;
import com.battlezone.megamachines.events.ui.ErrorEvent;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.networking.secure.Encryption;
import com.battlezone.megamachines.networking.secure.Protocol;
import com.battlezone.megamachines.networking.server.Server;
import com.battlezone.megamachines.renderer.theme.Theme;
import com.battlezone.megamachines.renderer.theme.ThemeHandler;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.storage.Storage;
import com.battlezone.megamachines.world.Lobby;
import com.battlezone.megamachines.world.Race;
import com.battlezone.megamachines.world.track.Track;

import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.battlezone.megamachines.entities.RWDCar.BYTE_LENGTH;

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
        this.roomNumber = roomNumber;

        byte carModelNumber = (byte) Storage.getStorage().getInt(Storage.CAR_MODEL, 1);
        Vector3f colour = Storage.getStorage().getVector3f(Storage.CAR_COLOUR, new Vector3f(1, 1, 1));

        // Socket will time out after 3 seconds
        clientSocket = new Socket();
        clientSocket.connect(new InetSocketAddress(serverAddress, PORT), 3000);

        outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
        toServerData = new byte[CLIENT_TO_SERVER_LENGTH];
        this.toServer = new DatagramPacket(toServerData, CLIENT_TO_SERVER_LENGTH, serverAddress, Server.PORT);

        byte[] fromServer = new byte[Server.SERVER_TO_CLIENT_LENGTH+5];
        this.fromServer = new DatagramPacket(fromServer, Server.SERVER_TO_CLIENT_LENGTH+5);

        // Send a JOIN_GAME packet
        byteBuffer = ByteBuffer.allocate(CLIENT_TO_SERVER_LENGTH).put(Protocol.JOIN_LOBBY).put(roomNumber).put(carModelNumber).put(colour.toByteArray());
        try {
            outToServer.writeObject(Encryption.encrypt(byteBuffer.array()));
            Random r = new Random();
            outToServer.writeObject(Encryption.encrypt(Storage.getStorage().getString(Storage.NAME, Driver.names[r.nextInt(Driver.names.length)]).getBytes()));
        } catch (Exception e) {
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

    public void setLaps(int laps) {
    }

    public void setRoomNumber(byte roomNumber) {
        this.roomNumber = roomNumber;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
            List<String> playerNames = new ArrayList<>();

            while (running) {

                // While in lobby
                while (running) {
                    fromServerData = Encryption.decrypt((byte[]) inputStream.readObject());

                    if (fromServerData[0] == Protocol.PLAYER_INFO) {
                        clientPlayerNumber = fromServerData[2];

                        playerNames.clear();

                        int len = fromServerData[1];
                        for (int i = 1 + 2; i < len * BYTE_LENGTH; i += BYTE_LENGTH) {
                            byte[] name = new byte[20];
                            System.arraycopy(fromServerData, i + 15, name, 0, 20);
                            playerNames.add(new String(name).trim());
                        }

                        MessageBus.fire(new PlayerUpdateEvent(Arrays.copyOf(fromServerData, fromServerData.length), fromServerData[2], false));
                    } else if (fromServerData[0] == Protocol.TRACK_TYPE) {
                        // Handle theme
                        byte themeByte = fromServerData[fromServerData.length - 2], lapCounter = fromServerData[fromServerData.length - 1];
                        ThemeHandler.setTheme(Theme.values()[themeByte]);

                        // Handle power ups
                        byte[] powerupManagerArray = Encryption.decrypt((byte[]) inputStream.readObject());
                        byte[] newArray = new byte[powerupManagerArray.length - 1];

                        System.arraycopy(powerupManagerArray, 1, newArray, 0, newArray.length);
                        MessageBus.fire(new TrackUpdateEvent(Arrays.copyOf(fromServerData, fromServerData.length - 2), Arrays.copyOf(newArray, newArray.length), lapCounter));
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
                if (inGameSocket != null)
                    inGameSocket.close();
                inGameSocket = new DatagramSocket(roomNumber + Protocol.DEFAULT_PORT + 1);
                toServer.setPort(roomNumber + Protocol.DEFAULT_PORT);
                while (running) {
                    inGameSocket.receive(fromServer);
                    fromServerData = Encryption.decrypt(fromServer.getData());

                    if (fromServerData[0] == Protocol.GAME_STATE) {
                        GameUpdateEvent packetBuffer = GameUpdateEvent.create(fromServerData);
                        MessageBus.fire(packetBuffer);
                    } else if (fromServerData[0] == Protocol.PLAYER_FINISH) {
                        if (fromServerData[1] == clientPlayerNumber) {
                            MessageBus.fire(new ErrorEvent("YOU FINISHED", Race.positions[fromServerData[2]], 2, Colour.GREEN));
                        } else {
                            MessageBus.fire(new ErrorEvent(playerNames.get(fromServerData[1]) + " FINISHED", Race.positions[fromServerData[2]], 2, Colour.GREEN));
                        }
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
                    fromServerData = Encryption.decrypt((byte[]) inputStream.readObject());

                    if (fromServerData[0] == Protocol.END_RACE) {
                        List<Integer> leaderboard = new ArrayList<>();
                        for (int i = 0; i < Server.MAX_PLAYERS * Server.END_GAME_STATE_PLAYER; i += Server.END_GAME_STATE_PLAYER) {
                            int position = fromServerData[1 + i] + 1;
                            leaderboard.add(position);
                        }

                        // Find winner
                        int winnerNumber = 0;
                        for (int i = 0; i < leaderboard.size(); i++)
                            if (leaderboard.get(i) == 1) {
                                winnerNumber = i;
                                break;
                            }

                        MessageBus.fire(new ErrorEvent(playerNames.get(winnerNumber) + " WON!", "YOUR POSITION: " + leaderboard.get(clientPlayerNumber), 4, Colour.GREEN));
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

        } catch (Exception e) {
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

            toServer.setData(Encryption.encrypt(toServerData));
            inGameSocket.send(toServer);
        } catch (Exception e) {
            System.err.println("Error sending keypress " + e.getMessage());
        }
    }

    public void close() {
        this.running = false;
        try {
            clientSocket.close();
            if (inGameSocket != null)
                inGameSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startGame(int laps) {
        // Send start game
        toServerData[0] = Protocol.START_GAME;
        try {
            outToServer.writeObject(Encryption.encrypt(toServerData));

            // Then send the track
            outToServer.writeObject(Encryption.encrypt(sentTrack.toByteArray()));

            // Send theme byte
            outToServer.writeObject(ThemeHandler.getTheme().toByte());

            // Send lap counter
            outToServer.writeObject((byte) laps);
        } catch (Exception e) {
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
