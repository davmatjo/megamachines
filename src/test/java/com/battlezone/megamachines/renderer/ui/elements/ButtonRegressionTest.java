package com.battlezone.megamachines.renderer.ui.elements;

import com.battlezone.megamachines.input.Cursor;
import com.battlezone.megamachines.renderer.RenderTestUtil;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.Window;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.renderer.ui.Scene;
import com.battlezone.megamachines.util.AssetManager;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lwjgl.BufferUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

//@RunWith(PowerMockRunner.class)
//@PrepareForTest(Cursor.class)
public class ButtonRegressionTest {

    private static long windowID;
    private static Scene scene;

    @BeforeClass
    public static void setup() {

//        Assert.assertEquals(1920, Window.getWindow().getWidth());
//        Assert.assertEquals(1080, Window.getWindow().getHeight());
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
//
    @Test
    public void drawButton() {

        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        var button = new Button(2f, 0.5f, -1f, -0.25f, Colour.WHITE, Colour.BLUE, "TEST BUTTON", 0.2f);
        scene.addElement(button);
        scene.render();
        glfwSwapBuffers(windowID);
        var actual = BufferUtils.createByteBuffer(4 * 1920 * 1080);
        glReadBuffer(GL_FRONT);
        glReadPixels(0, 0, 1920, 1080, GL_RGBA, GL_UNSIGNED_BYTE, actual);
        var image = AssetManager.imageFromBytes(actual, 1920, 1080);
        AssetManager.saveImage(image, "src/test/resources/regression/button1.bmp");
        RenderTestUtil.rendersAreEqual("src/test/resources/regression/button1.bmp", actual);
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        scene.removeElement(button);

    }

    @Test
    public void changeButtonText() {

        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        var button = new Button(2f, 0.5f, -1f, -0.25f, Colour.WHITE, Colour.BLUE, Texture.BLANK, "TEST BUTTON", 0.2f);
        button.setText("CHANGED TEST BUTTON");
        scene.addElement(button);
        scene.render();
        glfwSwapBuffers(windowID);
        var actual = BufferUtils.createByteBuffer(4 * 1920 * 1080);
        glReadBuffer(GL_FRONT);
        glReadPixels(0, 0, 1920, 1080, GL_RGBA, GL_UNSIGNED_BYTE, actual);

        RenderTestUtil.rendersAreEqual("src/test/resources/regression/button2.bmp", actual);
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        scene.removeElement(button);

    }

    @Test
    public void hoverButton() {

        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);

        var button = new Button(2f, 0.5f, -1f, -0.25f, Colour.WHITE, Colour.BLUE, Texture.BLANK, "TEST BUTTON", 0.2f);
        scene.addElement(button);
        glfwSetCursorPos(windowID, 960, 540);
        button.update();
        scene.render();
        glfwSwapBuffers(windowID);

        var actual = BufferUtils.createByteBuffer(4 * 1920 * 1080);
        glReadBuffer(GL_FRONT);
        glReadPixels(0, 0, 1920, 1080, GL_RGBA, GL_UNSIGNED_BYTE, actual);

        RenderTestUtil.rendersAreEqual("src/test/resources/regression/button3.bmp", actual);
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        scene.removeElement(button);

    }
}