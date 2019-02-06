package com.battlezone.megamachines.events.keys;

import java.net.InetAddress;

public class NetworkKeyEvent extends KeyEvent {

    private final InetAddress address;

    /**
     * Creates an event with a given key code.
     *
     * @param keyCode The key that has been pressed.
     */
    public NetworkKeyEvent(int keyCode, boolean pressed, InetAddress address) {
        super(keyCode, pressed);
        this.address = address;
    }

    public InetAddress getAddress() {
        return address;
    }
}
