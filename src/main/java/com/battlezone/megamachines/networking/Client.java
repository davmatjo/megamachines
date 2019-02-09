package com.battlezone.megamachines.networking;

import com.battlezone.megamachines.events.game.GameUpdateEvent;
import com.battlezone.megamachines.events.game.PlayerUpdateEvent;
import com.battlezone.megamachines.events.game.TrackUpdateEvent;
import com.battlezone.megamachines.events.keys.KeyEvent;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Client implements Runnable {

    final int CLIENT_TO_SERVER_LENGTH = 14;
    static final int PORT = 6969;
    private final DatagramSocket socket;
    private final DatagramPacket fromServer;
    private final DatagramPacket toServer;
    private final byte[] toServerData;
    private boolean running = true;
    private byte[] fromServerData;

    // Car info
    byte modelNumber = 1;
    float xColor = 1, yColor = 200, zColor = 100;

    public Client(InetAddress serverAddress) throws SocketException {
        MessageBus.register(this);

        socket = new DatagramSocket(PORT);

        toServerData = new byte[CLIENT_TO_SERVER_LENGTH];
        this.toServer = new DatagramPacket(toServerData, CLIENT_TO_SERVER_LENGTH, serverAddress, NewServer.PORT);

        byte[] fromServer = new byte[NewServer.SERVER_TO_CLIENT_LENGTH];
        this.fromServer = new DatagramPacket(fromServer, NewServer.SERVER_TO_CLIENT_LENGTH);

        // Send a JOIN_GAME packet
        toServer.setData(ByteBuffer.allocate(14).put(Protocol.JOIN_LOBBY).put(modelNumber).putFloat(xColor).putFloat(yColor).putFloat(zColor).array());
        try {
            System.out.println(Arrays.toString(toServer.getData()));
            socket.send(toServer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            while (running) {
                socket.receive(fromServer);
                fromServerData = fromServer.getData();
                System.out.println("Received " + Arrays.toString(fromServerData));
                if (fromServerData[0] == Protocol.GAME_STATE) {
                    GameUpdateEvent packetBuffer = GameUpdateEvent.create(fromServerData);
                    MessageBus.fire(packetBuffer);
                } else if (fromServerData[0] == Protocol.PLAYER_INFO) {
                    MessageBus.fire(new PlayerUpdateEvent(Arrays.copyOf(fromServerData, fromServerData.length), fromServerData[2], false));
                } else if (fromServerData[0] == Protocol.TRACK_TYPE) {
                    MessageBus.fire(new TrackUpdateEvent(Arrays.copyOf(fromServerData, fromServerData.length)));
                } else {
                    throw new RuntimeException("Received unexpected packet");
                }
            }
        } catch (IOException e) {
            socket.close();
            e.printStackTrace();
        }
    }

    @EventListener
    public void keyPressRelease(KeyEvent event) {
        System.out.println("Key event");
        try {
            toServerData[0] = Protocol.KEY_EVENT;
            toServerData[1] = event.getPressed() ? Protocol.KEY_PRESSED : Protocol.KEY_RELEASED;
            fillKeyData(toServerData, event.getKeyCode());

            toServer.setData(toServerData);
            socket.send(toServer);
        } catch (IOException e) {
            System.err.println("Error sending keypress " + e.getMessage());
        }
    }

    public void close() {
        System.out.println("closing");
        this.running = false;
        socket.close();
    }

    public void startGame() {
        toServerData[0] = Protocol.START_GAME;
        toServer.setData(toServerData);
        try {
            socket.send(toServer);
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
