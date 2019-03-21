package com.battlezone.megamachines.renderer.ui.elements;

import com.battlezone.megamachines.renderer.RenderTestUtil;
import com.battlezone.megamachines.renderer.Window;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.renderer.ui.Scene;
import com.battlezone.megamachines.util.AssetManager;
import org.junit.*;
import org.lwjgl.BufferUtils;

import static org.junit.Assert.*;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;

public class LabelRegressionTest {

    private long windowID;
    private Scene scene;

    @Before
    public void setup() {

//        Assert.assertEquals(1920, Window.getWindow().getWidth());
//        Assert.assertEquals(1080, Window.getWindow().getHeight());
        AssetManager.setIsHeadless(false);
        windowID = Window.getWindow().getGameWindow();
        scene = new Scene();

    }

    @After
    public void clear() {
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        glfwSwapBuffers(windowID);
        glClear(GL_COLOR_BUFFER_BIT);
    }

    @Test
    public void drawLabel() {

        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        var label = new Label("TEST LABEL.!?:_- 0123456789", 0.1f, -1.5f, -0.05f, Colour.WHITE);
        scene.addElement(label);
        scene.render();
        glfwSwapBuffers(windowID);
        var actual = BufferUtils.createByteBuffer(4 * 1920 * 1080);
        glReadBuffer(GL_FRONT);
        glReadPixels(0, 0, 1920, 1080, GL_RGBA, GL_UNSIGNED_BYTE, actual);

        RenderTestUtil.rendersAreEqual("src/test/resources/regression/label1.bmp", actual);
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        scene.removeElement(label);

    }

    @Test
    public void changePosition() {

        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        var label = new Label("TEST LABEL.!?:_- 0123456789", 0.1f, -1.5f, -0.05f, Colour.WHITE);
        label.setX(-1f);
        label.setY(0.1f);
        label.setPos(-1f, 0.1f);
        scene.addElement(label);
        scene.render();
        glfwSwapBuffers(windowID);
        var actual = BufferUtils.createByteBuffer(4 * 1920 * 1080);
        glReadBuffer(GL_FRONT);
        glReadPixels(0, 0, 1920, 1080, GL_RGBA, GL_UNSIGNED_BYTE, actual);

        RenderTestUtil.rendersAreEqual("src/test/resources/regression/label2.bmp", actual);
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        scene.removeElement(label);

    }
}