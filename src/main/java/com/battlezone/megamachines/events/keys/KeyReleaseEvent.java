package com.battlezone.megamachines.events.keys;

/**
 * A class that's used to pass event details when a key is released.
 *
 * @author Kieran
 */
public class KeyReleaseEvent extends KeyEvent {

    public KeyReleaseEvent(int keyCode) {
        super(keyCode);
    }

}
