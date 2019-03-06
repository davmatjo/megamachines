package com.battlezone.megamachines.events.mouse;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseButtonEvent {

    private final int button;
    private final int action;

    public static final int PRESSED = GLFW_PRESS;
    public static final int RELEASED = GLFW_RELEASE;

    /**
     * Create a MouseButtonEvent
     * @param button The id of the button that was pressed
     * @param action Whether the button was pressed or released
     */
    public MouseButtonEvent(int button, int action) {
        this.button = button;
        this.action = action;
    }

    public int getButton() {
        return button;
    }

    public int getAction() {
        return action;
    }
}
