package com.battlezone.megamachines.networking;

import com.battlezone.megamachines.math.Vector3f;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class NewServer implements Runnable {

    private enum State{LOBBY, IN_GAME}

    public NewServer() throws SocketException {
        this.socket = new DatagramSocket();
        this.receive = new DatagramPacket(new byte[14], 6);
        this.send = new DatagramPacket(new byte[258], 258);
    }

    private static final byte JOIN_LOBBY = 0;
    private static final byte START_GAME = 1;
    private static final byte KEY_EVENT = 2;

    private boolean running = true;
    private State currentState = State.LOBBY;
    private final DatagramSocket socket;
    private final DatagramPacket receive;
    private final DatagramPacket send;
    private final Map<InetAddress, Player> players = new HashMap<>();
    private InetAddress host;

    @Override
    public void run() {

        while (running) {
            try {
                socket.receive(receive);
                byte[] received = receive.getData();
                if (received[0] == JOIN_LOBBY) {
                    players.put(receive.getAddress(), new Player((int) received[1], Vector3f.fromByteArray(received, 2)));
                } if (received[0] == START_GAME && receive.getAddress().equals(host)) {

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void runGame() throws IOException {

        while (running) {
            socket.receive(receive);
            byte[] data = receive.getData();
            if (data[0] == KEY_EVENT) {
                Player current = players.get(receive.getAddress());
            }
        }
    }

    public void sendPacket(byte[] data) {
        for (var player : players.keySet()) {
            try {
                send.setAddress(player);
                send.setData(data);
                socket.send(send);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}