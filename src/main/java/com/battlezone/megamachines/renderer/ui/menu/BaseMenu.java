package com.battlezone.megamachines.renderer.ui.menu;

import com.battlezone.megamachines.events.ui.ErrorEvent;
import com.battlezone.megamachines.renderer.ui.Scene;

import java.util.Stack;

public class BaseMenu {

    private Stack<Scene> backstack = new Stack<>();
    private Scene currentScene;

    public void render() {
        currentScene.render();
    }

    public void hide() {
        currentScene.hide();
    }

    public void show() {
        currentScene.show();
    }

    public void navigationPush(MenuScene scene) {
        if(currentScene != null) {
            backstack.push(currentScene);
            currentScene.hide();
        }
        scene.show();
        currentScene = scene;
    }

    public void navigationPop() {
        if (!backstack.isEmpty()) {
            currentScene.hide();
            currentScene = backstack.pop();
            currentScene.show();
        }
    }

    void showError(ErrorEvent event) {
        currentScene.showError(event);
    }

    public Scene getCurrentScene() {
        return currentScene;
    }
}
