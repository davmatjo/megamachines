package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.math.Vector4f;

public class PauseMenu extends AbstractMenu {

    private final MenuScene pauseMenu;

    public PauseMenu(boolean isReallyPaused, Runnable resume, Runnable quit) {
        Box semiTransparency = new Box(4, 2, -2, -1, new Vector4f(0, 0, 0, 0.5f));
        this.pauseMenu = new MenuScene(Colour.WHITE, Colour.BLUE, semiTransparency);

        if (isReallyPaused)
            pauseMenu.addLabel("PAUSED", 1, 1f, Colour.WHITE);
        pauseMenu.addButton("BACK TO GAME", -1, resume);
        pauseMenu.addButton("QUIT GAME", -2, quit);

        currentScene = pauseMenu;
    }

}
