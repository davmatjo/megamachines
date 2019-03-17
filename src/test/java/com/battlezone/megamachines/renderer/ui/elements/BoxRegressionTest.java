package com.battlezone.megamachines.renderer.ui.elements;

import com.battlezone.megamachines.renderer.RenderTestUtil;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.Window;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.renderer.ui.Scene;
import com.battlezone.megamachines.util.AssetManager;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lwjgl.BufferUtils;

import java.awt.image.BufferedImage;

import static org.junit.Assert.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;

public class BoxRegressionTest {

    private static long windowID;
    private static Scene scene;

    @BeforeClass
    public static void setup() {
        if (glfwInit()) {
            AssetManager.setIsHeadless(false);
            windowID = Window.getWindow().getGameWindow();
            scene = new Scene();
        }
    }

    @Test
    public void drawBox() {
        if (glfwInit()) {
            var box = new Box(1f, 1f, 0f, 0f, Colour.WHITE, Texture.BLANK);
            scene.addElement(box);
            scene.render();
            glfwSwapBuffers(windowID);
            var actual = BufferUtils.createByteBuffer(4 * 1920 * 1080);
            glReadBuffer(GL_FRONT);
            glReadPixels(0, 0, 1920, 1080, GL_RGBA, GL_UNSIGNED_BYTE, actual);
            var image = AssetManager.imageFromBytes(actual, 1920, 1080);
            RenderTestUtil.rendersAreEqual("src/test/resources/regression/box1.bmp", actual);
            scene.removeElement(box);
        }
    }

    @Test
    public void drawBoxThenMove() {
        if (glfwInit()) {
            var box = new Box(1f, 1f, 0f, 0f, Colour.WHITE, Texture.BLANK);
            scene.addElement(box);
            scene.render();
            glfwSwapBuffers(windowID);

            box.setPos(-1f, -1f);
            scene.render();
            glfwSwapBuffers(windowID);

            var actual = BufferUtils.createByteBuffer(4 * 1920 * 1080);
            glReadBuffer(GL_FRONT);
            glReadPixels(0, 0, 1920, 1080, GL_RGBA, GL_UNSIGNED_BYTE, actual);

            RenderTestUtil.rendersAreEqual("src/test/resources/regression/box2.bmp", actual);
            scene.removeElement(box);
        }
    }
}