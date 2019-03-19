package com.battlezone.megamachines.renderer.ui.elements;

import com.battlezone.megamachines.events.keys.KeyEvent;
import com.battlezone.megamachines.events.mouse.MouseButtonEvent;
import com.battlezone.megamachines.input.KeyCode;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.renderer.RenderTestUtil;
import com.battlezone.megamachines.renderer.Window;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.renderer.ui.Scene;
import com.battlezone.megamachines.util.AssetManager;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lwjgl.BufferUtils;

import static org.junit.Assert.*;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;

public class NumericInputRegressionTest {

    private static long windowID;
    private static Scene scene;

    @BeforeClass
    public static void setup() {

        Assert.assertEquals(1920, Window.getWindow().getWidth());
        Assert.assertEquals(1080, Window.getWindow().getHeight());
        AssetManager.setIsHeadless(false);
        windowID = Window.getWindow().getGameWindow();
        scene = new Scene();

    }

    @AfterClass
    public static void clear() {
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        glfwSwapBuffers(windowID);
        glClear(GL_COLOR_BUFFER_BIT);
    }

    @Test
    public void drawNumericInput() {

        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        var input = new NumericInput(2f, 0.25f, -1f, -0.125f, Colour.WHITE, 0.1f, 10, "");
        scene.addElement(input);
        scene.show();
        scene.render();
        glfwSwapBuffers(windowID);
        var actual = BufferUtils.createByteBuffer(4 * 1920 * 1080);
        glReadBuffer(GL_FRONT);
        glReadPixels(0, 0, 1920, 1080, GL_RGBA, GL_UNSIGNED_BYTE, actual);

        RenderTestUtil.rendersAreEqual("src/test/resources/regression/numericInput1.bmp", actual);
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        scene.hide();
        scene.removeElement(input);

    }

    @Test
    public void inputNumbersIntoNumericInput() {

        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        var input = new NumericInput(2f, 0.25f, -1f, -0.125f, Colour.WHITE, 0.1f, 10, "");
        scene.addElement(input);
        scene.show();
        glfwSetCursorPos(windowID, 0, 0);
        input.update();
        MessageBus.fire(new MouseButtonEvent(0, MouseButtonEvent.PRESSED));
        MessageBus.fire(new KeyEvent(KeyCode.N_1, true));
        MessageBus.fire(new KeyEvent(KeyCode.N_1, false));
        MessageBus.fire(new KeyEvent(KeyCode.PERIOD, true));
        MessageBus.fire(new KeyEvent(KeyCode.PERIOD, false));
        MessageBus.fire(new KeyEvent(KeyCode.N_0, true));
        MessageBus.fire(new KeyEvent(KeyCode.N_0, false));
        MessageBus.fire(new KeyEvent(KeyCode.BACKSPACE, true));
        MessageBus.fire(new KeyEvent(KeyCode.BACKSPACE, false));

        scene.render();
        glfwSwapBuffers(windowID);
        var actual = BufferUtils.createByteBuffer(4 * 1920 * 1080);
        glReadBuffer(GL_FRONT);
        glReadPixels(0, 0, 1920, 1080, GL_RGBA, GL_UNSIGNED_BYTE, actual);

        RenderTestUtil.rendersAreEqual("src/test/resources/regression/numericInput2.bmp", actual);
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        scene.hide();
        scene.removeElement(input);

    }

}