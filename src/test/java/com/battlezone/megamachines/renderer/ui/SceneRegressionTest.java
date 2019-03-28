package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.events.ui.ErrorEvent;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.renderer.RenderTestUtil;
import com.battlezone.megamachines.renderer.Window;
import com.battlezone.megamachines.renderer.ui.elements.Box;
import com.battlezone.megamachines.util.AssetManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lwjgl.BufferUtils;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;

public class SceneRegressionTest {

    private static long windowID;
    private static Scene scene;

    @BeforeClass
    public static void setup() {

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
    public void render() {


        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        var box = new Box(1f, 1f, -0.5f, -0.5f, Colour.BLUE);
        scene.addElement(box);

        scene.render();
        scene.removeElement(box);

    }

    @Test
    public void showErrors() {
        scene.show();
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);

        MessageBus.fire(new ErrorEvent("TEST", "EVENT", 1));
        MessageBus.fire(new ErrorEvent("TEST", "EVENT 2", 1, Colour.BLUE));

        scene.render();
        scene.render();
        glfwSwapBuffers(windowID);
        var actual = BufferUtils.createByteBuffer(4 * 1920 * 1080);
        glReadBuffer(GL_FRONT);
        glReadPixels(0, 0, 1920, 1080, GL_RGBA, GL_UNSIGNED_BYTE, actual);

        RenderTestUtil.rendersAreEqual("src/test/resources/regression/event1.bmp", actual);
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);

        for (int i = 0; i < 63; i++) {
            glClearColor(0, 0, 0, 1);
            glClear(GL_COLOR_BUFFER_BIT);
            scene.render();
        }
        glfwSwapBuffers(windowID);
        actual = BufferUtils.createByteBuffer(4 * 1920 * 1080);
        glReadBuffer(GL_FRONT);
        glReadPixels(0, 0, 1920, 1080, GL_RGBA, GL_UNSIGNED_BYTE, actual);

        RenderTestUtil.rendersAreEqual("src/test/resources/regression/event2.bmp", actual);
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        scene.hide();
    }
}