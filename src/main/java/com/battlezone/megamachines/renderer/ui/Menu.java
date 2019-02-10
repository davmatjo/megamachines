package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.input.Cursor;
import com.battlezone.megamachines.storage.Storage;

public class Menu {

    private Scene currentScene;
    private final Scene mainMenu;
    private final Scene settingsMenu;
    private static final float BUTTON_WIDTH = 2.5f;
    private static final float BUTTON_HEIGHT = 0.25f;
    private static final float BUTTON_X = -1f;
    private static final float BUTTON_CENTRE_Y = -0.125f;
    private static final float BUTTON_OFFSET_Y = 0.5f;
    private static final float PADDING = 0.05f;

    public Menu(Cursor cursor, Runnable startSingleplayer, Runnable startMultiplayer) {
        this.mainMenu = new Scene();
        this.settingsMenu = new Scene();

        initMainMenu(cursor, startSingleplayer, startMultiplayer);
        initSettings(cursor);

        currentScene = mainMenu;
        currentScene.show();
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
        mainMenu.hide();
    }

    private void initSettings(Cursor cursor) {
        float backgroundVolume = Storage.getStorage().getFloat(Storage.KEY_BACKGROUND_MUSIC_VOLUME, 1);
        float fxVolume = Storage.getStorage().getFloat(Storage.KEY_SFX_VOLUME, 1);

        SeekBar backgroundToggle = new SeekBar(BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_X, getButtonY(0), Colour.WHITE, Colour.BLUE, "BACKGROUND MUSIC", backgroundVolume, PADDING, cursor);
        backgroundToggle.setOnValueChanged(() -> backgroundVolumeChanged(backgroundToggle));
        settingsMenu.addElement(backgroundToggle);

        SeekBar fxToggle = new SeekBar(BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_X, getButtonY(1), Colour.WHITE, Colour.BLUE, "SFX", fxVolume, PADDING, cursor);
        fxToggle.setOnValueChanged(() -> fxVolumeChanged(fxToggle));
        settingsMenu.addElement(fxToggle);

        Button back = new Button(BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_X, getButtonY(-1), Colour.WHITE, Colour.BLUE, "BACK", PADDING, cursor);
        back.setAction(() -> {
            settingsMenu.hide();
            currentScene = mainMenu;
            mainMenu.show();
        });
        settingsMenu.addElement(back);
        settingsMenu.hide();
    }

    private void backgroundVolumeChanged(SeekBar seekBar) {
        Storage.getStorage().setValue(Storage.KEY_BACKGROUND_MUSIC_VOLUME, seekBar.getValue());
        Storage.getStorage().save();
    }

    private void fxVolumeChanged(SeekBar seekBar) {
        Storage.getStorage().setValue(Storage.KEY_SFX_VOLUME, seekBar.getValue());
        Storage.getStorage().save();
    }

    private void showSettings() {
        System.out.println("Settings pressed");
        mainMenu.hide();
        currentScene = settingsMenu;
        settingsMenu.show();

    }

    public void render() {
        currentScene.render();
//        System.out.println(currentScene);
    }

    public void hide() {
        currentScene.hide();
    }

    public void show() {
        currentScene.show();
    }
}
