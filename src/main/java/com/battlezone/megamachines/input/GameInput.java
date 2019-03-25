package com.battlezone.megamachines.input;

import com.battlezone.megamachines.events.keys.KeyEvent;
import com.battlezone.megamachines.messaging.MessageBus;
import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

/**
 * A class that handles the input and produces an abstracted input model.
 *
 * @author Kieran
 */
public class GameInput extends GLFWKeyCallback {

    private static GameInput instance;

    /**
     * @return A singleton instance of GameInput
     */
    public static GameInput getGameInput() {
        if (instance == null) {
            instance = new GameInput();
        }
        return instance;
    }

    // Tracks the states of key presses as booleans
    private boolean[] keys = new boolean[KeyCode.MAX_VALUE];

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        if (key != -1) {
            keys[key] = action != GLFW_RELEASE;
            if (action == GLFW_PRESS) {
                MessageBus.fire(new KeyEvent(key, true));
            } else if (action == GLFW_RELEASE) {
                MessageBus.fire(new KeyEvent(key, false));
            }
        }
    }
}