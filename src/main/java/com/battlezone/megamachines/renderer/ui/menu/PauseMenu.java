package com.battlezone.megamachines.renderer.ui.menu;

import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.renderer.ui.elements.Box;

public class PauseMenu extends AbstractMenu {

    private final MenuScene pauseMenu;
    private final MenuScene settingsMenu;

    public PauseMenu(boolean isReallyPaused, Runnable resume, Runnable quit) {
        Box semiTransparency = new Box(4, 2, -2, -1, new Vector4f(0, 0, 0, 0.5f));
        this.pauseMenu = new MenuScene(Colour.WHITE, Colour.BLUE, semiTransparency);
        this.settingsMenu = new SettingsMenuScene(this, Colour.WHITE, Colour.BLUE, semiTransparency);

        if (isReallyPaused)
            pauseMenu.addLabel("PAUSED", 1.5f, 1f, Colour.WHITE);

        pauseMenu.addButton("SETTINGS", 0, () -> navigationPush(settingsMenu));
        pauseMenu.addButton("BACK TO GAME", -1, resume);
        pauseMenu.addButton("QUIT GAME", -2, quit);

        navigationPush(pauseMenu);
    }

}
