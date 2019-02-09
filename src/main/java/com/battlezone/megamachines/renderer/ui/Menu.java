package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.input.Cursor;

public class Menu {

    private Scene currentScene;
    private final Scene mainMenu;
    private final Scene settingsMenu;
    private final Scene lobbyMenu;
    private static final float BUTTON_WIDTH = 2.0f;
    private static final float BUTTON_HEIGHT = 0.25f;
    private static final float BUTTON_X = -1f;
    private static final float BUTTON_CENTRE_Y = -0.125f;
    private static final float BUTTON_OFFSET_Y = 0.5f;
    private static final float PADDING = 0.05f;

    public Menu(Cursor cursor, Runnable startSingleplayer, Runnable startMultiplayer) {
        this.mainMenu = new Scene();
        this.settingsMenu = new Scene();
        this.lobbyMenu = new Scene();

        initMainMenu(cursor, startSingleplayer, startMultiplayer);
        initSettings(cursor);

        currentScene = mainMenu;
    }

    public static float getButtonY(int numberFromCenter) {
        return BUTTON_CENTRE_Y + numberFromCenter * BUTTON_OFFSET_Y;
    }

    private void initMainMenu(Cursor cursor, Runnable startSingleplayer, Runnable startMultiplayer) {
        Button singleplayer = new Button(BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_X, getButtonY(1), Colour.WHITE, Colour.BLUE, "SINGLEPLAYER", PADDING, cursor);
        Button multiplayer = new Button(BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_X, getButtonY(0), Colour.WHITE, Colour.BLUE, "MULTIPLAYER", PADDING, cursor);
        Button settings = new Button(BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_X, getButtonY(-1), Colour.WHITE, Colour.BLUE, "SETTINGS", PADDING, cursor);

        singleplayer.setAction(startSingleplayer);
        multiplayer.setAction(startMultiplayer);
        settings.setAction(this::showSettings);

        mainMenu.addElement(singleplayer);
        mainMenu.addElement(multiplayer);
        mainMenu.addElement(settings);
    }

    private void initSettings(Cursor cursor) {
        Button soundToggle = new Button(BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_X, getButtonY(0), Colour.WHITE, Colour.BLUE, "SOUND OFF", PADDING, cursor);
        soundToggle.setAction(() -> toggleSound(soundToggle));
        settingsMenu.addElement(soundToggle);
    }

    private void toggleSound(Button soundToggle) {
        soundToggle.setText("SOUND ON");
    }

    private void showSettings() {
        currentScene.hide();
        currentScene = settingsMenu;
    }

    public void render() {
        currentScene.render();
    }

    public void hide() {
        currentScene.hide();
    }

    public void show() {
        currentScene.show();
    }
}
