package com.battlezone.megamachines.networking;

import com.battlezone.megamachines.events.game.GameUpdateEvent;
import com.battlezone.megamachines.events.keys.KeyEvent;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class Client implements Runnable {

    final int CLIENT_TO_SERVER_LENGTH = 14;
    private static final int PORT = 6969;
    private final DatagramSocket socket;
    private final DatagramPacket fromServer;
    private final DatagramPacket toServer;
    private final byte[] toServerData;
    private boolean running = true;

    public Client(InetAddress serverAddress) throws SocketException {
        socket = new DatagramSocket(PORT);

        toServerData = new byte[CLIENT_TO_SERVER_LENGTH];
        this.toServer = new DatagramPacket(toServerData, CLIENT_TO_SERVER_LENGTH, serverAddress, NewServer.PORT);

        byte[] fromServer = new byte[NewServer.SERVER_TO_CLIENT_LENGTH];
        this.fromServer = new DatagramPacket(fromServer, NewServer.SERVER_TO_CLIENT_LENGTH);
    }

    @Override
    public void run() {
        try {
            while (running) {
                socket.receive(fromServer);
                byte[] data = fromServer.getData();
                if (data[0] == Protocol.GAME_STATE) {
                    ByteBuffer packetBuffer = GameUpdateEvent.create(data);
                    MessageBus.fire(packetBuffer);
                } else {
                    throw new RuntimeException("Received unexpected packet");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventListener
    public void keyPressRelease(KeyEvent event) {
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

    public void setRunning(boolean running) {
        this.running = running;
    }

    private void fillKeyData(byte[] data, int keyCode) {
        data[2] = (byte) (keyCode & 0xff);
        data[3] = (byte) ((keyCode >> 8) & 0xff);
        data[4] = (byte) ((keyCode >> 16) & 0xff);
        data[5] = (byte) ((keyCode >> 24) & 0xff);
    }
}
