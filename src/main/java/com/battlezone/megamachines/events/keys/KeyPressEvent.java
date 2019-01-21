package com.battlezone.megamachines.events.keys;

/**
 * A class that's used to pass event details when a key is pressed.
 *
 * @author Kieran
 */
public class KeyPressEvent extends KeyEvent {

    public KeyPressEvent(int keyCode) {
        super(keyCode);
    }

}
