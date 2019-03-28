package com.battlezone.megamachines.input;

import com.battlezone.megamachines.events.keys.KeyEvent;
import com.battlezone.megamachines.math.MathUtils;
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
    // Tracks the states of key presses as booleans
    private boolean[] keys = new boolean[KeyCode.MAX_VALUE + 1];

    /**
     * @return A singleton instance of GameInput
     */
    public static GameInput getGameInput() {
        if (instance == null) {
            instance = new GameInput();
        }
        return instance;
    }

    /**
     * A method that updates the pressed keys when triggered from GLFW.
     *
     * @see GLFWKeyCallback#invoke(long, int, int, int, int)
     */
    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        if (MathUtils.inRange(key, 0, KeyCode.MAX_VALUE)) {
            keys[key] = action != GLFW_RELEASE;
            if (action == GLFW_PRESS) {
                MessageBus.fire(new KeyEvent(key, true));
            } else if (action == GLFW_RELEASE) {
                MessageBus.fire(new KeyEvent(key, false));
            }
        }
    }
}