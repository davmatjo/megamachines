package com.battlezone.megamachines.events.keys;

/**
 * A class that's used to pass event details when a key is repeated.
 *
 * @author Kieran
 */
public class KeyRepeatEvent extends KeyEvent {

    public KeyRepeatEvent(int keyCode) {
        super(keyCode);
    }

}
