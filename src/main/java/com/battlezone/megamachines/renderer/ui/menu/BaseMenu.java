package com.battlezone.megamachines.renderer.ui.menu;

import com.battlezone.megamachines.events.ui.ErrorEvent;
import com.battlezone.megamachines.renderer.ui.Scene;

import java.util.Stack;

/**
 * A base menu class which manages showing scenes in a navigational hierarchy
 */
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

    /**
     * Pushes a new scene onto the navigation stack. This scene becomes visible
     *
     * @param scene The scene to push
     */
    public void navigationPush(MenuScene scene) {
        //If there is an existing scene, put it on the stack and hide it
        if (currentScene != null) {
            backstack.push(currentScene);
            currentScene.hide();
        }
        //Show the new scene and make it current
        scene.show();
        currentScene = scene;
    }

    /**
     * Pop the last scene of the stack, revealing the one underneath
     */
    public void navigationPop() {
        if (!backstack.isEmpty()) {
            //Hide old scene, popup it, and show the new one
            currentScene.hide();
            currentScene = backstack.pop();
            currentScene.show();
        }
    }

    /**
     * Display an error event on the current scene
     *
     * @param event The event
     */
    void showError(ErrorEvent event) {
        currentScene.showError(event);
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    /**
     * Pop all scenes until non are left on the backstack
     */
    public void popToRoot() {
        while (!backstack.isEmpty()) {
            navigationPop();
        }
    }
}
