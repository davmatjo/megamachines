package com.battlezone.megamachines.networking;

import java.io.IOException;
import java.net.*;

public class Client extends Thread {
    private DatagramSocket socket;
    private InetAddress address;
    private int port = 6969;

    private byte[] buf;

    boolean running;

    public Client() {
        running = true;

        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public String receiveMessage() {
        DatagramPacket packet = new DatagramPacket(buf, buf.length, port);
        try {
            socket.receive(packet);
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
            e.printStackTrace();
        }
    }

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
        socket.close();
    }

    public void run() {
        running = true;

        while (running) {
            // Listen for messages
            receiveMessage();
        }

        socket.close();
    }
}