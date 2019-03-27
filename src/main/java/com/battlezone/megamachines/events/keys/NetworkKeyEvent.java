package com.battlezone.megamachines.events.keys;

import com.battlezone.megamachines.entities.RWDCar;

public class NetworkKeyEvent extends KeyEvent {

    private final RWDCar player;

    /**
     * Creates an event with a given key code.
     *
     * @param keyCode The key that has been pressed.
     */
    public NetworkKeyEvent(int keyCode, boolean pressed, RWDCar player) {
        super(keyCode, pressed);
        this.player = player;
    }

    public RWDCar getPlayer() {
        return player;
    }
}
