package com.battlezone.megamachines.events.keys;

/**
 * A class that's used to pass event details when an action is applied to a key.
 *
 * @author Kieran
 */
public class KeyEvent {

    private final int KEYCODE;
    private final boolean PRESSED;

    /**
     * Creates an event with a given key code.
     *
     * @param keyCode The key that has been pressed.
     */
    public KeyEvent(int keyCode, boolean pressed) {
        KEYCODE = keyCode;
        PRESSED = pressed;
    }

    /**
     * Method to retrieve the key code involved.
     *
     * @return The key code of the involved key.
     */
    public int getKeyCode() {
        return KEYCODE;
    }

    /**
     * Method to retrieve whether the key was pressed or released.
     *
     * @return true if the key was pressed, false if it was released.
     */
    public boolean getPressed() {
        return PRESSED;
    }

}
