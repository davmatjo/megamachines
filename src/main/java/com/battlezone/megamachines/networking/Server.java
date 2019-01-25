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
    DatagramPacket receivePacket, sendPacket;
    String received;

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

        // Set packets on null
        receivePacket = sendPacket = null;
    }

    private String receiveMessage() {
        boolean uncaughtPacket = true;
        while (uncaughtPacket) {
            try {
                receivePacket = new DatagramPacket(buf, buf.length);
                uncaughtPacket = false;
            } catch (Exception e) {
                uncaughtPacket = true;
                continue;
            }
            try {
                socket.receive(receivePacket);
            } catch (IOException e) {
                uncaughtPacket = true;
                continue;
            }

            if ( receivePacket.getPort() == -1 ) {
                uncaughtPacket = true;
                continue;
            }

            // Add address if non-existent
            if (!clientAddresses.contains(receivePacket.getSocketAddress())) {
                clientAddresses.add(receivePacket.getSocketAddress());
//            System.out.println("New client: " + receivePacket.getSocketAddress());
            }
        }

        // Process the data and send it as a string
        received = new String(receivePacket.getData(), 0, receivePacket.getLength());
        return received;
    }

    public void sendMessage(GameStatePacket msg) {
        buf = msg.toString().getBytes();
        // Send message to all connected clients
        for (SocketAddress address : clientAddresses) {
            sendPacket= new DatagramPacket(buf, buf.length, address);
            try {
                socket.send(sendPacket);
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
                continue;
            }

            // Process the packet and add it to the queue
            ClientDataPacket clientPacket = ClientDataPacket.fromString(packetAsString);
            clientPackets.add(clientPacket);
        }

        socket.close();
    }
}