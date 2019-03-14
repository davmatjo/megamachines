package com.battlezone.megamachines.input;

import com.battlezone.megamachines.events.keys.KeyEvent;
import com.battlezone.megamachines.messaging.MessageBus;
import org.lwjgl.glfw.GLFWJoystickCallback;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class Gamepad {

    /**
     * Configuration for controller goes [x axis, y axis]
     */
    public static Map<String, List<Integer>> controllers = new HashMap<>() {{
        put("Xbox Controller", List.of(0, 5, 4));
        put("Microsoft X-Box One pad", List.of(0, 4, 5));
    }};

    private static final float DEADZONE = 0.05f;
    private int currentGamepad;
    private int xAxisReference;
    private int brakeReference;
    private int acceleratorReference;
    private final float[] inputs = new float[2];

    private boolean right;
    private boolean left;
    private boolean centre;
    private boolean accelerator;
    private boolean brake;

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
                System.out.println(glfwGetJoystickName(currentGamepad));
                List<Integer> config = controllers.getOrDefault(
                        glfwGetJoystickName(currentGamepad),
                        controllers.get("Xbox Controller")
                );
                xAxisReference = config.get(0);
                acceleratorReference = config.get(1);
                brakeReference = config.get(2);
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
            inputs[1] = (axes.get(brakeReference) + 1) / 2;
        } else {
            inputs[0] = 0;
            inputs[1] = 0;
        }
        return inputs;
    }

    public void update() {
        if (currentGamepad != -1) {
            FloatBuffer axes = glfwGetJoystickAxes(currentGamepad);
            if (axes.get(xAxisReference) > 0.5) {
                if (!right) {
                    MessageBus.fire(new KeyEvent(KeyCode.D, true));
                    right = true;
                    centre = false;
                }
            } else if (axes.get(xAxisReference) < -0.5) {
                if (!left) {
                    MessageBus.fire(new KeyEvent(KeyCode.A, true));
                    left = true;
                    centre = false;
                }
            } else if (!centre) {
                centre = true;
                if (left) {
                    left = false;
                    MessageBus.fire(new KeyEvent(KeyCode.A, false));
                } else if (right) {
                    right = false;
                    MessageBus.fire(new KeyEvent(KeyCode.D,  false));
                }
            }

            if (axes.get(acceleratorReference) > 0) {
                if (!accelerator) {
                    MessageBus.fire(new KeyEvent(KeyCode.S,  true));
                    accelerator = true;
                }
            } else if (accelerator) {
                MessageBus.fire(new KeyEvent(KeyCode.S,  false));
                accelerator = false;
            }

            if (axes.get(brakeReference) > 0) {
                if (!brake) {
                    MessageBus.fire(new KeyEvent(KeyCode.W,  true));
                    brake = true;
                }
            } else if (brake) {
                MessageBus.fire(new KeyEvent(KeyCode.W,  false));
                brake = false;
            }
        }
    }
}
