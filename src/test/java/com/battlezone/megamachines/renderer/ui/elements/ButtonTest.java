package com.battlezone.megamachines.renderer.ui.elements;

import com.battlezone.megamachines.renderer.Window;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.renderer.ui.Scene;
import com.battlezone.megamachines.util.AssetManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class ButtonTest {

    private long windowID;
    private Scene scene;

    @Before
    public void setup() {
        AssetManager.setIsHeadless(false);
        windowID = Window.getWindow().getGameWindow();
        scene = new Scene();
    }

    @Test
    public void testAction() {
        if (glfwInit()) {
            Runnable runnable = Mockito.mock(Runnable.class);

            Button button = new Button(1, 1, 0, 0, Colour.WHITE, Colour.WHITE, "", 0);
            button.setAction(runnable);

            button.runAction();

            verify(runnable, atLeastOnce()).run();
        }
    }

}