package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.input.Cursor;
import com.battlezone.megamachines.math.Vector4f;

public class Menu {

    private Scene currentScene;
    private final Scene mainMenu;
    private static final float BUTTON_WIDTH = 1.5f;
    private static final float BUTTON_HEIGHT = 0.25f;

    public Menu(Cursor cursor) {
        this.mainMenu = new Scene();

        Button button = new Button(BUTTON_WIDTH, BUTTON_HEIGHT, -0.75f, -0.125f, Colour.WHITE, Colour.BLUE, "START", 0.05f, cursor);
        button.setAction(() -> System.out.println("Start"));
        mainMenu.addElement(button);

        currentScene = mainMenu;
    }

    public void render() {
        currentScene.render();
    }
}
