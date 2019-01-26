package com.battlezone.megamachines.networking;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Client extends Thread {
    private DatagramSocket socket;
    private InetAddress address;
    private int port = 6969;
    private byte[] buf;
    private boolean running;
    private ConcurrentLinkedQueue<GameStatePacket> gameStates;
    DatagramPacket receivePacket, sendPacket;
    String received;


    public Client() {
        // Set the server address to localhost
        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // Try to make connection
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // Set Thread on running state
        running = true;

        // Set game states as a new linked list
        gameStates = new ConcurrentLinkedQueue<>();

        // Set buffer
        buf = new byte[576];

        // Set packet
        receivePacket = new DatagramPacket(buf, buf.length, address, port);
        sendPacket = new DatagramPacket(buf, buf.length, address, port);
    }

    public String receiveMessage() {
        if ( running == false )
            return "";

        // Try to receive packet
        try {
            socket.receive(receivePacket);
        } catch (IOException e) {
            return "";
        }

        // Return string
        received = new String(
                receivePacket.getData(), 0, receivePacket.getLength());
        return received;
    }

    public void sendMessage(ClientDataPacket msg) {
        msg.updateTimestamp();
        buf = msg.toString().getBytes();
        sendPacket = new DatagramPacket(buf, buf.length, address, port);
        try {
            socket.send(sendPacket);
        } catch (IOException e) {
            ;
        }
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

            // Process the game state and add it to the queue
            GameStatePacket newServerPacket = GameStatePacket.fromString(packetAsString);
            gameStates.add(newServerPacket);
        }

        socket.close();
    }
}