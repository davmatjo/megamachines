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
    private ConcurrentLinkedQueue<byte[]> gameStates;
    DatagramPacket receivePacket, sendPacket;
    KeyPacket keyPacket;

    // Define constant server event types for Client to Server packets -> on byte 0
    private static final byte JOIN_LOBBY = 0;
    private static final byte START_GAME = 1;
    private static final byte KEY_EVENT = 2;

    // Define constant for types of packets for Server to Client -> on byte 0
    private static final byte GAME_STATE = 0;
    private static final byte TRACK_TYPE = 1;

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
        buf = new byte[300];

        // Set packet
<<<<<<< HEAD
        receivePacket = new DatagramPacket(buf, buf.length);
        sendPacket = new DatagramPacket(buf, buf.length, address, serverPort);
=======
        receivePacket = new DatagramPacket(buf, buf.length, address, port);
>>>>>>> 8ddb9c5d39426e87c8a167a34d39a6680c40eb6f

        // Initialise key packet for listening to keys
        keyPacket = new KeyPacket(socket);

        // Set first byte of the buffer to JOIN_LOBBY (0) to send to server to connect to it
        buf[0] = JOIN_LOBBY;
        sendMessage();
    }

    public void receiveMessage() {
        if ( running == false )
           return;

        // Try to receive packet
        try {
            socket.receive(receivePacket);
        } catch (IOException e) {
<<<<<<< HEAD
//            e.printStackTrace();
//            running = false;
            return "";
=======
            e.printStackTrace();
            return;
>>>>>>> 8ddb9c5d39426e87c8a167a34d39a6680c40eb6f
        }
    }

<<<<<<< HEAD
    public void sendMessage(ClientDataPacket msg) {
        msg.updateTimestamp();
        sendPacket.setData(msg.toString().getBytes());
=======
    public void sendMessage() {
        sendPacket = new DatagramPacket(buf, buf.length, address, port);
>>>>>>> 8ddb9c5d39426e87c8a167a34d39a6680c40eb6f
        try {
            socket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        running = false;
    }

    public void run() {
        running = true;

        while (running) {
            // Listen for messages
            receiveMessage();

            // Handle data
            if ( receivePacket.getData()[0] == TRACK_TYPE ) {
                // If we get track type, set the track with the data TODO: do
            }
            else if ( receivePacket.getData()[0] == GAME_STATE ) {
                // If we get packets about the game state, process the data
                gameStates.add(receivePacket.getData());
            }
        }

        socket.close();
    }
}