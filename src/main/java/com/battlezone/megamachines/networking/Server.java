package com.battlezone.megamachines.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

@SuppressWarnings("Duplicates")
public class Server extends Thread {

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[1024];
    private String lastMessage = "";

    public Server() {
        try {
            socket = new DatagramSocket(6969);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public String getLastMessage() {
        return lastMessage;
    }

    private boolean receiveMessage() {
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        packet = new DatagramPacket(buf, buf.length, address, port);
        String received
                = new String(packet.getData(), 0, packet.getLength());
        lastMessage = received;

        // If we receive a message "end", server will stop
        if (received.equals("end")) {
            return true;
        }

        // Return false signal to let the server run
        return false;
    }

    public void sendMessage(String msg, InetAddress address) {
        buf = msg.getBytes();
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, address, 6969);

        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        running = true;

        while (running) {
            // Listen for messages
            boolean endServer = receiveMessage();

            // If endServer flag was raised, exit while loop
            if ( endServer == true )
                continue;
        }

        socket.close();
    }
}