package com.battlezone.megamachines.input;

import org.lwjgl.glfw.GLFWJoystickCallback;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class Gamepad {

    /**
     * Configuration for controller goes [x axis, y axis]
     */
    public static Map<String, List<Integer>> controllers = new HashMap<>() {{
        put("Xbox Controller", List.of(0, 5));
    }};

    private static final float DEADZONE = 0.05f;
    private int currentGamepad;
    private int xAxisReference;
    private int yAxisReference;
    private final float[] inputs = new float[2];

    public Gamepad() {
        initialiseGamepad();
        glfwSetJoystickCallback(new GLFWJoystickCallback() {
            @Override
            public void invoke(int jid, int event) {
                System.out.println("joystick event");
                initialiseGamepad();
            }
        });
    }

    private void initialiseGamepad() {
        for (int i = GLFW_JOYSTICK_1; i < GLFW_JOYSTICK_LAST; i++) {
            if (glfwJoystickPresent(i)) {
                System.out.println("Gamepad connected");
                currentGamepad = i;
                List<Integer> config = controllers.getOrDefault(
                        glfwGetJoystickName(currentGamepad),
                        controllers.get("Xbox Controller")
                );
                xAxisReference = config.get(0);
                yAxisReference = config.get(1);
                return;
            }
        }
        System.out.println("No Gamepads Connected");
        currentGamepad = -1;
    }

    public float[] getCurrentAxes() {
        if (currentGamepad != -1) {
            FloatBuffer axes = glfwGetJoystickAxes(currentGamepad);
            float xAxis = -axes.get(xAxisReference);
            inputs[0] = Math.abs(xAxis) > DEADZONE ? xAxis : 0;
            inputs[1] = (axes.get(yAxisReference) + 1) / 2;
        } else {
            inputs[0] = 0;
            inputs[1] = 0;
        }
        return inputs;
    }
}
