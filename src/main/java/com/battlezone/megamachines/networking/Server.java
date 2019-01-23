package com.battlezone.megamachines.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server extends Thread {

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[1024];
    private int port = 6969;

    public Server() {
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            ;
        }
    }

    private boolean receiveMessage() {
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            return true;
        }

        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        packet = new DatagramPacket(buf, buf.length, address, port);
        String received
                = new String(packet.getData(), 0, packet.getLength());

        // If we receive a message "end", server will stop
        if (received.equals("end")) {
            return true;
        }

        // Return false signal to let the server run
        return false;
    }

    public void sendMessage(UDPPacketData msg, InetAddress address) {
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
            boolean endServer = receiveMessage();

            // If endServer flag was raised, exit while loop
            if ( endServer == true )
                continue;
        }

        socket.close();
    }
}