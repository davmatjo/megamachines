package com.battlezone.megamachines.renderer.ui.menu;

import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.renderer.ui.elements.Box;
import com.battlezone.megamachines.renderer.ui.elements.Button;
import com.battlezone.megamachines.renderer.ui.elements.SeekBar;
import com.battlezone.megamachines.renderer.ui.elements.TextInput;
import com.battlezone.megamachines.sound.SoundSettingsEvent;
import com.battlezone.megamachines.storage.Storage;
import com.battlezone.megamachines.util.AssetManager;

public class SettingsMenuScene extends MenuScene {

    private static final int MAX_CAR_MODEL = 3;
    private BaseMenu menu;
    private MenuScene soundSettings;
    private MenuScene gameSettings;

    public SettingsMenuScene(BaseMenu menu, Vector4f primaryColor, Vector4f secondaryColor, Box background) {
        super(primaryColor, secondaryColor, background);

        this.soundSettings = new MenuScene(Colour.WHITE, Colour.BLUE, background);
        this.gameSettings = new MenuScene(Colour.WHITE, Colour.BLUE, background);

        this.menu = menu;
        initSound();
        initGame();
        init();
    }

    private void init() {

        addLabel("SETTINGS", 1.5f, 1f, Colour.WHITE);
        addButton("AUDIO", 0.5f, () -> menu.navigationPush(soundSettings));
        addButton("GAME", -0.5f, () -> menu.navigationPush(gameSettings));
        addButton("BACK", -1.5f, () -> {
            menu.navigationPop();
        });
        hide();
    }

    private void initSound() {
        float backgroundVolume = Storage.getStorage().getFloat(Storage.BACKGROUND_MUSIC_VOLUME, 1);
        float fxVolume = Storage.getStorage().getFloat(Storage.SFX_VOLUME, 1);

        soundSettings.addLabel("SOUND SETTINGS", 1.5f, 0.7f, Colour.WHITE);

        SeekBar backgroundToggle = soundSettings.addSeekbar("MUSIC", backgroundVolume, 0.5f);
        backgroundToggle.setOnValueChanged(() -> backgroundVolumeChanged(backgroundToggle));

        SeekBar fxToggle = soundSettings.addSeekbar("SFX", fxVolume, -0.5f);
        fxToggle.setOnValueChanged(() -> fxVolumeChanged(fxToggle));

        soundSettings.addButton("SAVE AND RETURN", -1.5f, () -> {
            Storage.getStorage().save();
            menu.navigationPop();
        });

        soundSettings.hide();
    }

    private void initGame() {
        int carModelNumber = Storage.getStorage().getInt(Storage.CAR_MODEL, 1);
        Vector3f rawColour = Storage.getStorage().getVector3f(Storage.CAR_COLOUR, new Vector3f(1, 1, 1));
        Vector3f carColour = Colour.convertToCarColour(new Vector3f(rawColour.x, rawColour.y, rawColour.z));


        gameSettings.addLabel("GAME SETTINGS", 2f, 0.7f, Colour.WHITE);

        Box colourPreview = new Box(BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_X, getButtonY(0), new Vector4f(rawColour, 1));
        gameSettings.addElement(colourPreview);

        Box carModel = new Box((BUTTON_WIDTH / 4) - 3 * PADDING, BUTTON_HEIGHT - PADDING, BUTTON_X + getRowX(4, 4), getButtonY(0) + PADDING / 2, new Vector4f(carColour, 1), AssetManager.loadTexture("/cars/car" + carModelNumber + ".png"));
        gameSettings.addElement(carModel);

        SeekBar carColourX = gameSettings.addSeekbar("R", rawColour.x, 0, null, BUTTON_WIDTH / 4 - 2 * PADDING, BUTTON_HEIGHT - PADDING, getRowX(1, 4), PADDING / 2, PADDING * 1.2f);
        SeekBar carColourY = gameSettings.addSeekbar("G", rawColour.y, 0, null, BUTTON_WIDTH / 4 - 2 * PADDING, BUTTON_HEIGHT - PADDING, getRowX(2, 4), PADDING / 2, PADDING * 1.2f);
        SeekBar carColourZ = gameSettings.addSeekbar("B", rawColour.z, 0, null, BUTTON_WIDTH / 4 - 2 * PADDING, BUTTON_HEIGHT - PADDING, getRowX(3, 4), PADDING / 2, PADDING * 1.2f);

        String name = Storage.getStorage().getString(Storage.NAME, "");
        TextInput nameEntry = gameSettings.addTextInput("NAME", name, 20, 1);

        Runnable colourChanged = (() -> carColourChanged(carColourX, carColourY, carColourZ, colourPreview, carModel, rawColour, carColour));
        carColourX.setOnValueChanged(colourChanged);
        carColourY.setOnValueChanged(colourChanged);
        carColourZ.setOnValueChanged(colourChanged);

        Button toggleCarModel = gameSettings.addButton("CAR MODEL " + carModelNumber, -1);
        toggleCarModel.setAction(() -> carModelChanged(toggleCarModel, carModel));

        gameSettings.addButton("SAVE AND RETURN", -2, () -> {
            Storage.getStorage().setValue(Storage.NAME, nameEntry.getTextValue());
            Storage.getStorage().save();
            menu.navigationPop();
        });
    }

    private void backgroundVolumeChanged(SeekBar seekBar) {
        //Round to 0.01
        Storage.getStorage().setValue(Storage.BACKGROUND_MUSIC_VOLUME, Math.round(100 * seekBar.getValue()) / 100.0);
        MessageBus.fire(new SoundSettingsEvent());
    }

    private void fxVolumeChanged(SeekBar seekBar) {
        Storage.getStorage().setValue(Storage.SFX_VOLUME, Math.round(100 * seekBar.getValue()) / 100.0);
        MessageBus.fire(new SoundSettingsEvent());
    }

    private void carModelChanged(Button button, Box modelShown) {
        int currentModel = Storage.getStorage().getInt(Storage.CAR_MODEL, 1);
        currentModel += 1;
        currentModel = currentModel > MAX_CAR_MODEL ? 1 : currentModel;
        button.setText("CAR MODEL " + currentModel);
        modelShown.setTexture(AssetManager.loadTexture("/cars/car" + currentModel + ".png"));
        Storage.getStorage().setValue(Storage.CAR_MODEL, currentModel);
    }

    private void carColourChanged(SeekBar barX, SeekBar barY, SeekBar barZ, Box colourPreview, Box carPreview, Vector3f currentColour, Vector3f carColour) {
        currentColour.x = barX.getValue();
        currentColour.y = barY.getValue();
        currentColour.z = barZ.getValue();

        colourPreview.setColour(new Vector4f(currentColour, 1));
        carPreview.setColour(new Vector4f(carColour, 1));
        Storage.getStorage().setValue(Storage.CAR_COLOUR, currentColour);
    }

    // x offset when laying out items in a row
    private float getRowX(int position, int total) {
        return ((position - 1) * BUTTON_WIDTH / total) + position * PADDING / 2;
    }

}
