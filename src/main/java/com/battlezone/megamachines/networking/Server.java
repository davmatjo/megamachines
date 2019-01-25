package com.battlezone.megamachines.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings("ALL")
public class Server extends Thread {

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[1024];
    private int port = 6969;
    private ConcurrentLinkedQueue<ClientDataPacket> clientPackets;

    public Server() {
        // Try creating the socket with the specific port
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            ;
        }

        // Initialise the queue
        clientPackets = new ConcurrentLinkedQueue<>();
    }

    private String receiveMessage() {
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            return "";
        }

        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        packet = new DatagramPacket(buf, buf.length, address, port);
        String received
                = new String(packet.getData(), 0, packet.getLength());

        return received;
    }

    public void sendMessage(ClientDataPacket msg, InetAddress address) {
        buf = msg.toString().getBytes();
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, address, port);

        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        socket.close();
    }

    public void run() {
        running = true;

        while (running) {
            // Listen for messages
            String packetAsString = receiveMessage();

            // Process the packet
            ClientDataPacket clientPacket = ClientDataPacket.fromString(packetAsString);
            clientPackets.add(clientPacket);

//            System.out.println(clientPackets);
        }

        socket.close();
    }
}