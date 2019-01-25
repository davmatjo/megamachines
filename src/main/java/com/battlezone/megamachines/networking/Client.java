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
    }

    public String receiveMessage() {
        DatagramPacket packet = null;
        try {
            packet = new DatagramPacket(buf, buf.length, port);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        try {
            socket.receive(packet);
            System.out.println(packet + "lol");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String received = new String(
                packet.getData(), 0, packet.getLength());
        return received;
    }

    public void sendMessage(ClientDataPacket msg) {
        msg.updateTimestamp();
        buf = msg.toString().getBytes();
        DatagramPacket packet
          = new DatagramPacket(buf, buf.length, address, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            ;
        }
    }

    // Please never use
    public void sendMessageAsString(String msg) {
        buf = msg.getBytes();
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, address, port);
        try {
            socket.send(packet);
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
//            System.out.println("fuck");

            // Process the game state and add it to the queue
            GameStatePacket newServerPacket = GameStatePacket.fromString(packetAsString);
            gameStates.add(newServerPacket);
        }

        socket.close();
    }
}