package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.sound.SoundSettingsEvent;
import com.battlezone.megamachines.storage.Storage;
import com.battlezone.megamachines.util.AssetManager;

public class SettingsMenuScene extends MenuScene {

    private AbstractMenu menu;
    private static final int MAX_CAR_MODEL = 3;

    public SettingsMenuScene(AbstractMenu menu, Vector4f primaryColor, Vector4f secondaryColor, Box background) {
        super(primaryColor, secondaryColor, background);
        this.menu = menu;
        init();
    }

    private void init() {
        float backgroundVolume = Storage.getStorage().getFloat(Storage.BACKGROUND_MUSIC_VOLUME, 1);
        float fxVolume = Storage.getStorage().getFloat(Storage.SFX_VOLUME, 1);
        int carModelNumber = Storage.getStorage().getInt(Storage.CAR_MODEL, 1);
        Vector3f carColour = Storage.getStorage().getVector3f(Storage.CAR_COLOUR, new Vector3f(1, 1, 1));

        SeekBar backgroundToggle = addSeekbar("BACKGROUND MUSIC", backgroundVolume, 2);
        backgroundToggle.setOnValueChanged(() -> backgroundVolumeChanged(backgroundToggle));

        SeekBar fxToggle = addSeekbar("SFX", fxVolume, 1);
        fxToggle.setOnValueChanged(() -> fxVolumeChanged(fxToggle));

        Box colourPreview = new Box(BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_X, getButtonY(-1), new Vector4f(carColour, 1));
        addElement(colourPreview);

        Box carModel = new Box((BUTTON_WIDTH / 4) - 3 * PADDING, BUTTON_HEIGHT - PADDING, BUTTON_X + getRowX(4, 4), getButtonY(-1) + PADDING / 2, new Vector4f(carColour, 1), AssetManager.loadTexture("/cars/car" + carModelNumber + ".png"));
        addElement(carModel);

        SeekBar carColourX = addSeekbar("R", carColour.x, -1, null, BUTTON_WIDTH / 4 - 2 * PADDING, BUTTON_HEIGHT - PADDING, getRowX(1, 4), PADDING / 2, PADDING * 1.2f);
        SeekBar carColourY = addSeekbar("G", carColour.y, -1, null, BUTTON_WIDTH / 4 - 2 * PADDING, BUTTON_HEIGHT - PADDING, getRowX(2, 4), PADDING / 2, PADDING * 1.2f);
        SeekBar carColourZ = addSeekbar("B", carColour.z, -1, null, BUTTON_WIDTH / 4 - 2 * PADDING, BUTTON_HEIGHT - PADDING, getRowX(3, 4), PADDING / 2, PADDING * 1.2f);

        Runnable colourChanged = (() -> carColourChanged(carColourX, carColourY, carColourZ, colourPreview, carModel, carColour));
        carColourX.setOnValueChanged(colourChanged);
        carColourY.setOnValueChanged(colourChanged);
        carColourZ.setOnValueChanged(colourChanged);

        Button toggleCarModel = addButton("CAR MODEL " + carModelNumber, 0);
        toggleCarModel.setAction(() -> carModelChanged(toggleCarModel, carModel));

        addButton("SAVE AND EXIT", -2, () -> {
            Storage.getStorage().save();
            menu.navigationPop();
        });

        hide();
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

    private void carColourChanged(SeekBar barX, SeekBar barY, SeekBar barZ, Box colourPreview, Box carPreview, Vector3f currentColour) {
        currentColour.x = barX.getValue();
        currentColour.y = barY.getValue();
        currentColour.z = barZ.getValue();

        colourPreview.setColour(new Vector4f(currentColour, 1));
        carPreview.setColour(new Vector4f(currentColour, 1));
        Storage.getStorage().setValue(Storage.CAR_COLOUR, currentColour);
    }

    // x offset when laying out items in a row
    private float getRowX(int position, int total) {
        return ((position - 1) * BUTTON_WIDTH / total) + position * PADDING / 2;
    }

}
