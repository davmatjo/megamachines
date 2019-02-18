package com.battlezone.megamachines.renderer.ui;

public class PauseMenu extends AbstractMenu {

    private final MenuScene pauseMenu;

    public PauseMenu(boolean isReallyPaused, Runnable resume, Runnable quit) {
        this.pauseMenu = new MenuScene(Colour.WHITE, Colour.BLUE, null);

        if (isReallyPaused)
            pauseMenu.addLabel("PAUSED", 1, 1f);
        pauseMenu.addButton("BACK TO GAME", -1, resume);
        pauseMenu.addButton("QUIT GAME", -2, quit);

        currentScene = pauseMenu;
    }

}
