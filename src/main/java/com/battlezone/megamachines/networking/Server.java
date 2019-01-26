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

        // Set buffer
        buf = new byte[576];

        // Set packets
        receivePacket = new DatagramPacket(buf, buf.length);
        sendPacket = new DatagramPacket(buf, buf.length);
    }

    private String receiveMessage() {
        if ( running == false )
            return "";

        // Try to receive packet
        try {
            socket.receive(receivePacket);
        } catch (IOException e) {
            return "";
        }

        // Add address if non-existent
        if (!clientAddresses.contains(receivePacket.getSocketAddress())) {
            clientAddresses.add(receivePacket.getSocketAddress());
            System.out.println("New client: " + receivePacket.getSocketAddress());
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
//        try {
//            Thread.sleep(10);
//        } catch (InterruptedException e) {
//            return;
//        }
    }

    public void close() {
        running = false;
        socket.close();
    }

    public void run() {
        running = true;

        while (running) {
            // Listen for messages
            String packetAsString = receiveMessage();

            // Process the packet and add it to the queue
            ClientDataPacket clientPacket = ClientDataPacket.fromString(packetAsString);
            clientPackets.add(clientPacket);
        }

        socket.close();
    }
}