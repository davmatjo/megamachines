package com.battlezone.megamachines.events.keys;

/**
 * A class that's used to pass event details when an action is applied to a key.
 *
 * @author Kieran
 */
public abstract class KeyEvent {

    private final int KEYCODE;

    /**
     * Creates an event with a given key code.
     *
     * @param keyCode The key that has been pressed.
     */
    KeyEvent(int keyCode) {
        KEYCODE = keyCode;
    }

    /**
     * Method to retrieve the key code involved.
     *
     * @return The key code of the involved key.
     */
    public int getKeyCode() {
        return KEYCODE;
    }

}
