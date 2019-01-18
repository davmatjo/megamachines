package com.battlezone.megamachines.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

@SuppressWarnings("Duplicates")
public class EchoServer extends Thread {

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];

    public EchoServer() {
        try {
            socket = new DatagramSocket(6969);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public boolean receiveMessage() {
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

        // Echo capability TODO: to be removed
//        transmitMessage(received, address);

        // If we receive a message "end", server will stop
        if (received.equals("end")) {
            return true;
        }

        // Return false signal to let the server run
        return false;
    }

    private void transmitMessage(String msg, InetAddress address) {
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

            if ( endServer == true )
                continue;
        }

        socket.close();
    }
}