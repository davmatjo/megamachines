package com.battlezone.megamachines.input;

import com.battlezone.megamachines.events.keys.KeyEvent;
import com.battlezone.megamachines.messaging.MessageBus;
import org.lwjgl.glfw.GLFWKeyCallback;

import static com.battlezone.megamachines.input.KeyCode.*;
import static com.battlezone.megamachines.math.MathUtils.clampd;
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

    private GameInput() {

    }

    // Measured in ms to go from 0 to 1
    private final double INTERPOLATE = 200;
    // Tracks the states of key presses as booleans
    private boolean[] keys = new boolean[KeyCode.MAX_VALUE];
    private double xAxis, yAxis;
    private long previousTimeStamp = System.currentTimeMillis();

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        keys[key] = action != GLFW_RELEASE;
        if (action == GLFW_PRESS) {
            MessageBus.fire(new KeyEvent(key, true));
        } else if (action == GLFW_RELEASE) {
            MessageBus.fire(new KeyEvent(key, false));
        }
    }

    /**
     * A method to update the state of the abstracted input axis.
     */
    public void update() {
        // Update the timestamps
        long currentTimestamp = System.currentTimeMillis();
        long deltaTime = currentTimestamp - previousTimeStamp;
        previousTimeStamp = currentTimestamp;
        double interpolationRate = deltaTime / INTERPOLATE;

        // Determine raw X axis input (-1, 0 or 1) from key presses
        int rawInput = (keys[D] || keys[RIGHT] ? 1 : 0) - (keys[A] || keys[LEFT] ? 1 : 0);
        // Prevent unnecessary calculations
        if (!(rawInput == 0 && xAxis == 0))
            xAxis = updateAxis(xAxis, rawInput, interpolationRate);

        // Determine Y axis input (-1, 0 or 1) from key presses
        rawInput = (keys[W] || keys[UP] ? 1 : 0) - (keys[S] || keys[DOWN] ? 1 : 0);
        // Prevent unnecessary calculations
        if (!(rawInput == 0 && yAxis == 0))
            yAxis = updateAxis(yAxis, rawInput, interpolationRate);
    }

    /**
     * A method to provide an updated value for the abstracted input axis.
     *
     * @param axis              The value of the input axis to apply the update to.
     * @param rawInput          The raw directional value to use in the update.
     * @param interpolationRate The rate of interpolation.
     * @return The updated axis value.
     */
    private double updateAxis(double axis, int rawInput, double interpolationRate) {
        // Check if there's no directional input (or input cancelled out), return the abstracted input axis back to 0
        // Otherwise, change the axis with the given input
        if (rawInput == 0)
            axis = axis < 0 ? clampd(axis + interpolationRate, -1, 0) : clampd(axis - interpolationRate, 0, 1);
        else
            axis = clampd(axis + rawInput * interpolationRate, -1, 1);
        return axis;
    }

    /**
     * A method to retrieve the abstracted X axis value.
     *
     * @return The abstracted Y axis value.
     */
    public double getXInput() {
        return xAxis;
    }

    /**
     * A method to retrieve the abstracted Y axis value.
     *
     * @return The abstracted Y axis value.
     */
    public double getYInput() {
        return yAxis;
    }

    /**
     * A method to determine whether a key is currently pressed.
     *
     * @param keyCode The keycode to check.
     * @return Whether the key is pressed.
     */
    public boolean isPressed(int keyCode) {
        assert (keyCode >= 0 && keyCode <= KeyCode.MAX_VALUE);
        return keys[keyCode];
    }
}