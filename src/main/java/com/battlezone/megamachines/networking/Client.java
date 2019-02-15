package com.battlezone.megamachines.networking;

import com.battlezone.megamachines.events.game.*;
import com.battlezone.megamachines.events.keys.KeyEvent;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.storage.Storage;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

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
    private byte roomNumber = 1;


    public Client(InetAddress serverAddress) throws IOException {
        byte carModelNumber = (byte) Storage.getStorage().getInt(Storage.CAR_MODEL, 1);
        Vector3f colour = Storage.getStorage().getVector3f(Storage.CAR_COLOUR, new Vector3f(1, 1, 1));

        clientSocket = new Socket(serverAddress, this.PORT);
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
        }
        byteBuffer.rewind();
        new Thread(this).start();
        MessageBus.register(this);
    }

    public void setRoomNumber(byte roomNumber) {
        this.roomNumber = roomNumber;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
            // While in lobby
            while (running) {
                fromServerData = (byte[]) inputStream.readObject();
                System.out.println(Arrays.toString(fromServerData));

                if (fromServerData[0] == Protocol.PLAYER_INFO) {
                    MessageBus.fire(new PlayerUpdateEvent(Arrays.copyOf(fromServerData, fromServerData.length), fromServerData[2], false));
                } else if (fromServerData[0] == Protocol.TRACK_TYPE) {
                    MessageBus.fire(new TrackUpdateEvent(Arrays.copyOf(fromServerData, fromServerData.length)));
                    break;
                } else if (fromServerData[0] == Protocol.UDP_DATA) {
                    MessageBus.fire(new PortUpdateEvent(Arrays.copyOf(fromServerData, fromServerData.length)));
                } else if (fromServerData[0] == Protocol.FAIL_CREATE) {
                    MessageBus.fire(new FailRoomEvent(Arrays.copyOf(fromServerData, fromServerData.length)));
                } else {
                    throw new RuntimeException("Received unexpected packet");
                }
            }

            // While in game
            roomNumber *= 2;
            inGameSocket = new DatagramSocket(roomNumber + Protocol.DEFAULT_PORT + 1);
            toServer.setPort(roomNumber + Protocol.DEFAULT_PORT);
            while (running) {
                inGameSocket.receive(fromServer);
                fromServerData = fromServer.getData();

                if (fromServerData[0] == Protocol.GAME_STATE) {
                    GameUpdateEvent packetBuffer = GameUpdateEvent.create(fromServerData);
                    MessageBus.fire(packetBuffer);
                } else {
                    throw new RuntimeException("Received unexpected packet" + Arrays.toString(fromServerData));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
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
            inGameSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startGame() {
        toServerData[0] = Protocol.START_GAME;
        try {
            outToServer.writeObject(toServerData);
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
