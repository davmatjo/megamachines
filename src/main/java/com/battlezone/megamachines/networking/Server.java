package com.battlezone.megamachines.networking;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings("ALL")
public class Server extends Thread {

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[1024];
    private int port = 6969;
    private ConcurrentLinkedQueue<ClientDataPacket> clientPackets;
    private ArrayList<SocketAddress> clientAddresses;

    public Server() {
        // Try creating the socket with the specific port
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            ;
        }

        // Initialise the queue
        clientPackets = new ConcurrentLinkedQueue<>();

        // Initialise the client addresses
        clientAddresses = new ArrayList<>();
    }

    private String receiveMessage() {
        DatagramPacket packet = null;
        try {
            packet = new DatagramPacket(buf, buf.length);
        } catch (Exception e) {
            return "";
        }
        try {
            socket.receive(packet);
        } catch (IOException e) {
            return "";
        }

        SocketAddress address = packet.getSocketAddress();
        // Add address if non-existent
        if (!clientAddresses.contains(address)) {
            clientAddresses.add(address);
//            System.out.println("New client: " + address + "\n" + packet.getSocketAddress());
        }

        int port = packet.getPort();
        String received
                = new String(packet.getData(), 0, packet.getLength());

        return received;
    }

    public void sendMessage(GameStatePacket msg) {
        buf = msg.toString().getBytes();
        // Send message to all connected clients
        for (SocketAddress address : clientAddresses) {
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length, address);
            try {
                socket.send(packet);
            } catch (IOException e) {
                System.out.println("Failed to send message!");
            }
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

            if (packetAsString.isEmpty()) {
                System.out.println("fuck");
                continue;
            }

            // Process the packet and add it to the queue
            ClientDataPacket clientPacket = ClientDataPacket.fromString(packetAsString);
            clientPackets.add(clientPacket);
        }

        socket.close();
    }
}