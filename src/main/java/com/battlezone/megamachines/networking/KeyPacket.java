package com.battlezone.megamachines.networking;

import com.battlezone.megamachines.events.keys.KeyEvent;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class KeyPacket {

    private final DatagramSocket socket;
    private final byte[] data;
    private final DatagramPacket packet;

    public KeyPacket(DatagramSocket socket) {
        MessageBus.register(this);
        this.socket = socket;
        data = new byte[]{1, 0, 0, 0, 0, 0};
        packet = new DatagramPacket(data, data.length);
    }

    @EventListener
    public void keyPressed(KeyEvent event) {
        if (event.getPressed())
            keyPressed(event.getKeyCode());
        else
            keyReleased(event.getKeyCode());
    }

    private void keyPressed(int keyCode) {
        try {
            data[1] = 1;
            fillKeyData(data, keyCode);

            packet.setData(data);
            socket.send(packet);
        } catch (IOException e) {
            System.err.println("Error sending keypress " + e.getMessage());
        }
    }

    private void keyReleased(int keyCode) {
        try {
            data[1] = 0;
            fillKeyData(data, keyCode);

            packet.setData(data);
            socket.send(packet);
        } catch (IOException e) {
            System.err.println("Error sending keypress " + e.getMessage());
        }
    }

    private void fillKeyData(byte[] data, int keyCode) {
        data[2] = (byte) (keyCode & 0xff);
        data[3] = (byte) ((keyCode >> 8) & 0xff);
        data[4] = (byte) ((keyCode >> 16) & 0xff);
        data[5] = (byte) ((keyCode >> 24) & 0xff);
    }
}
