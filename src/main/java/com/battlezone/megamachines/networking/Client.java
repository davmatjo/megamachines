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
        // Try to make connection
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
            address = InetAddress.getByName("localhost");
//            System.out.println("Server: " + address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // Set Thread on running state
        running = true;

        // Set game states as a new linked list
        gameStates = new ConcurrentLinkedQueue<>();

        // Set packet
        receivePacket = sendPacket = null;
    }

    public String receiveMessage() {
        boolean uncaughtPacket = true;
        while (uncaughtPacket) {
            try {
                receivePacket = new DatagramPacket(buf, buf.length, port);
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
        }

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