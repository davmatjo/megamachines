package com.battlezone.megamachines.renderer.ui.menu;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class MainMenuTest {

    @Test
    public void push() {
        if (glfwInit()) {
            MenuScene sceneA = Mockito.mock(MenuScene.class);
            MenuScene sceneB = Mockito.mock(MenuScene.class);

            AbstractMenu menu = new AbstractMenu();

            menu.navigationPush(sceneA);
            Assert.assertSame(menu.getCurrentScene(), sceneA);


            menu.navigationPush(sceneB);

            Assert.assertSame(menu.getCurrentScene(), sceneB);
            verify(sceneA, atLeastOnce()).hide();
        }
    }

}