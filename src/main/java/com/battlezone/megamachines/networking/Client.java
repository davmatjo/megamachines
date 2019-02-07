package com.battlezone.megamachines.networking;

import com.battlezone.megamachines.events.keys.KeyEvent;
import com.battlezone.megamachines.messaging.EventListener;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Client implements Runnable {

    final int CLIENT_TO_SERVER_LENGTH = 14;
    private static final int PORT = 6969;
    private final DatagramSocket socket;
    private final DatagramPacket fromServer;
    private final DatagramPacket toServer;

    public Client(InetAddress serverAddress) throws SocketException {
        socket = new DatagramSocket(6969);

        byte[] toServer = new byte[CLIENT_TO_SERVER_LENGTH];
        this.toServer = new DatagramPacket(toServer, CLIENT_TO_SERVER_LENGTH, serverAddress, NewServer.PORT);

        byte[] fromServer = new byte[NewServer.SERVER_TO_CLIENT_LENGTH];
        this.fromServer = new DatagramPacket(fromServer, NewServer.SERVER_TO_CLIENT_LENGTH);
    }

    @Override
    public void run() {

    }

    @EventListener
    public void keyPressRelease(KeyEvent event) {

    }
}
