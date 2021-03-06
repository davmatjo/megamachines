package com.battlezone.megamachines.renderer.ui.menu;

import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.theme.Theme;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.renderer.ui.elements.Box;
import com.battlezone.megamachines.util.ArrayUtil;
import com.battlezone.megamachines.util.AssetManager;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * A scene for the user to select a theme
 */
public class ThemeSelectionScene extends MenuScene {

    private BaseMenu menu;
    private Consumer<Theme> startGame;
    private ThemeOption[] themeOptions;
    private ScrollingItems trackSelector;

    public ThemeSelectionScene(BaseMenu menu, Vector4f primaryColor, Vector4f secondaryColor, Box background, Consumer<Theme> startGame) {
        super(primaryColor, secondaryColor, background);

        this.startGame = startGame;
        this.menu = menu;

        this.themeOptions = getThemeOptions();

        var boxTop = getButtonY(0.5f);
        var boxBottom = getButtonY(-2f);
        var buttonHeight = Math.abs(boxTop - boxBottom);
        this.trackSelector = new ScrollingItems(BUTTON_X, (boxTop + boxBottom) / 2f, BUTTON_WIDTH, buttonHeight, themeOptions, (opt) -> startGame((ThemeOption) opt), getPrimaryColor(), getSecondaryColor());

        init();
    }

    private void init() {
        addLabel("THEME SELECTION", 2f, 0.8f, Colour.WHITE);

        addButton("RANDOM", -2f, this::randomTheme, BUTTON_WIDTH / 2 - PADDING, BUTTON_HEIGHT, BUTTON_WIDTH / 2 + PADDING);
        addButton("BACK", -2f, menu::navigationPop, BUTTON_WIDTH / 2 - PADDING, BUTTON_HEIGHT, 0);

        addElement(trackSelector);

        hide();
    }

    /**
     * Starts the game with a random theme
     */
    private void randomTheme() {
        startGame(ArrayUtil.randomElement(getThemeOptions()));
    }

    /**
     * @return An array of options for the theme
     */
    private ThemeOption[] getThemeOptions() {
        var options = new ArrayList<ThemeOption>();

        for (Theme t : Theme.values()) {
            options.add(new ThemeOption(t));
        }
        return options.toArray(ThemeOption[]::new);
    }

    private void startGame(ThemeOption chosen) {
        menu.navigationPop();
        this.startGame.accept(chosen.getTheme());
    }

    @Override
    public void show() {
        super.show();
        if (this.trackSelector != null)
            trackSelector.show();
    }

    @Override
    public void hide() {
        super.hide();
        if (this.trackSelector != null)
            trackSelector.hide();
    }

    class ThemeOption extends ListItem {

        private Theme theme;

        public ThemeOption(Theme theme) {
            super(theme.getName(), AssetManager.loadTexture(theme + "/tracks/track_l.png"));
            this.theme = theme;
        }

        public Theme getTheme() {
            return theme;
        }
    }
}
