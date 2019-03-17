package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.renderer.Window;
import com.battlezone.megamachines.renderer.ui.elements.Box;
import com.battlezone.megamachines.util.AssetManager;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.lwjgl.glfw.GLFW.*;

public class SceneRegressionTest {

    @Test
    public void render() {
        AssetManager.setIsHeadless(false);
        var windowID = Window.getWindow().getGameWindow();
        var scene = new Scene();
        var box = new Box(1f, 1f, -0.5f, -0.5f, Colour.BLUE);
        scene.addElement(box);

        scene.render();
        glfwSwapBuffers(windowID);
        scene.removeElement(box);
    }
}