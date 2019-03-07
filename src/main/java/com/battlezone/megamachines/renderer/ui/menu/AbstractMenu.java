package com.battlezone.megamachines.renderer.ui.menu;

import com.battlezone.megamachines.events.ui.ErrorEvent;
import com.battlezone.megamachines.renderer.ui.Scene;

import java.util.Stack;

public abstract class AbstractMenu {

    private Stack<Scene> backstack = new Stack<>();
    Scene currentScene;

    public void render() {
        currentScene.render();
    }

    public void hide() {
        currentScene.hide();
    }

    public void show() {
        currentScene.show();
    }

    void navigationPush(MenuScene scene) {
        backstack.push(currentScene);
        currentScene.hide();
        scene.show();
        currentScene = scene;
    }

    void navigationPop() {
        if (!backstack.isEmpty()) {
            currentScene.hide();
            currentScene = backstack.pop();
            currentScene.show();
        }
    }

    void showError(ErrorEvent event) {
        currentScene.showError(event);
    }

}
