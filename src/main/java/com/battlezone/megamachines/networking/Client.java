package com.battlezone.megamachines.networking;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Client extends Thread {
    private DatagramSocket socket;
    private InetAddress address;
    private int serverPort = 6969;
    private int clientPort = 6970;
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
            return;
        }

        // Set Thread on running state
        running = true;

        // Set game states as a new linked list
        gameStates = new ConcurrentLinkedQueue<>();

        // Set buffer
        buf = new byte[576];

        // Set packet
        receivePacket = new DatagramPacket(buf, buf.length);
        sendPacket = new DatagramPacket(buf, buf.length, address, serverPort);

        // Finally send an empty message to the Server so it connects to it
        sendMessage(new ClientDataPacket());
    }

    public String receiveMessage() {
        if ( running == false )
            return "";

        // Try to receive packet
        try {
            socket.receive(receivePacket);
        } catch (IOException e) {
//            e.printStackTrace();
//            running = false;
            return "";
        }

        // Return string
        received = new String(
                receivePacket.getData(), 0, receivePacket.getLength());
        return received;
    }

    public void sendMessage(ClientDataPacket msg) {
        msg.updateTimestamp();
        sendPacket.setData(msg.toString().getBytes());
        try {
            socket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
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