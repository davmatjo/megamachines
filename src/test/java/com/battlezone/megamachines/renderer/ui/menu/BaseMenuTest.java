package com.battlezone.megamachines.renderer.ui.menu;

import com.battlezone.megamachines.events.ui.ErrorEvent;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.mockito.Mockito.*;

public class BaseMenuTest {

    @Test
    public void push() {
        if (glfwInit()) {
            MenuScene sceneA = Mockito.mock(MenuScene.class);
            MenuScene sceneB = Mockito.mock(MenuScene.class);

            BaseMenu menu = new BaseMenu();

            menu.navigationPush(sceneA);
            Assert.assertSame(menu.getCurrentScene(), sceneA);

            menu.navigationPush(sceneB);

            Assert.assertSame(menu.getCurrentScene(), sceneB);
            verify(sceneA, atLeastOnce()).hide();
        }
    }

    @Test
    public void pop() {
        if (glfwInit()) {
            MenuScene sceneA = Mockito.mock(MenuScene.class);
            MenuScene sceneB = Mockito.mock(MenuScene.class);

            BaseMenu menu = new BaseMenu();

            menu.navigationPush(sceneA);
            Assert.assertSame(menu.getCurrentScene(), sceneA);

            menu.navigationPush(sceneB);

            Assert.assertSame(menu.getCurrentScene(), sceneB);
            verify(sceneA, atLeastOnce()).hide();

            menu.navigationPop();

            Assert.assertSame(menu.getCurrentScene(), sceneA);
            verify(sceneB, atLeastOnce()).hide();
            verify(sceneA, atLeastOnce()).show();
        }
    }

    @Test
    public void popToRoot() {
        if (glfwInit()) {
            MenuScene sceneA = Mockito.mock(MenuScene.class);
            MenuScene sceneB = Mockito.mock(MenuScene.class);
            MenuScene sceneC = Mockito.mock(MenuScene.class);

            BaseMenu menu = new BaseMenu();

            menu.navigationPush(sceneA);
            Assert.assertSame(menu.getCurrentScene(), sceneA);

            menu.navigationPush(sceneB);
            Assert.assertSame(menu.getCurrentScene(), sceneB);

            menu.navigationPush(sceneC);
            Assert.assertSame(menu.getCurrentScene(), sceneC);

            menu.popToRoot();

            Assert.assertSame(menu.getCurrentScene(), sceneA);
        }
    }

    @Test
    public void showError() {
        if (glfwInit()) {
            MenuScene sceneA = Mockito.mock(MenuScene.class);
            MenuScene sceneB = Mockito.mock(MenuScene.class);

            BaseMenu menu = new BaseMenu();

            menu.navigationPush(sceneA);
            menu.navigationPush(sceneB);

            var error = new ErrorEvent("title", "message", 1);
            menu.showError(error);

            verify(sceneA, never()).showError(error);
            verify(sceneB, atLeastOnce()).showError(error);
        }
    }

}