package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.sound.SoundSettingsEvent;
import com.battlezone.megamachines.storage.Storage;
import com.battlezone.megamachines.util.AssetManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.function.Consumer;

import static com.battlezone.megamachines.renderer.ui.MenuScene.*;

public class Menu extends AbstractMenu {

    private final MenuScene mainMenu;
    private final MenuScene settingsMenu;
    private final MenuScene multiplayerAddressMenu;

    private static final int MAX_CAR_MODEL = 3;
    private static final int IP_MAX_LENGTH = 15;

    public Menu(Runnable startSingleplayer, Consumer<InetAddress> startMultiplayer) {
        this.mainMenu = new MenuScene(Colour.WHITE, Colour.BLUE);
        this.settingsMenu = new MenuScene(Colour.WHITE, Colour.BLUE);
        this.multiplayerAddressMenu = new MenuScene(Colour.WHITE, Colour.BLUE);

        initMainMenu(startSingleplayer, startMultiplayer);
        initSettings();
        initMultiplayerAddress(startMultiplayer);

        currentScene = mainMenu;
    }

    private void initMainMenu(Runnable startSingleplayer, Consumer<InetAddress> startMultiplayer) {
        mainMenu.addButton("SINGLEPLAYER", 1, startSingleplayer);
        mainMenu.addButton("MULTIPLAYER", 0, (() -> navigationPush(multiplayerAddressMenu)));
        mainMenu.addButton("SETTINGS", -1, (() -> navigationPush(settingsMenu)));
    }

    private void initSettings() {
        float backgroundVolume = Storage.getStorage().getFloat(Storage.KEY_BACKGROUND_MUSIC_VOLUME, 1);
        float fxVolume = Storage.getStorage().getFloat(Storage.KEY_SFX_VOLUME, 1);
        int carModelNumber = Storage.getStorage().getInt(Storage.CAR_MODEL, 1);
        Vector3f carColour = Storage.getStorage().getVector3f(Storage.CAR_COLOUR, new Vector3f(1, 1, 1));

        SeekBar backgroundToggle = settingsMenu.addSeekbar("BACKGROUND MUSIC", backgroundVolume, 2);
        backgroundToggle.setOnValueChanged(() -> backgroundVolumeChanged(backgroundToggle));

        SeekBar fxToggle = settingsMenu.addSeekbar("SFX", fxVolume, 1);
        fxToggle.setOnValueChanged(() -> fxVolumeChanged(fxToggle));

        Box colourPreview = new Box(BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_X, getButtonY(-1), new Vector4f(carColour, 1));
        settingsMenu.addElement(colourPreview);

        Box carModel = new Box((BUTTON_WIDTH / 4) - 3 * PADDING, BUTTON_HEIGHT - PADDING, BUTTON_X + (3 * BUTTON_WIDTH / 4) + 3 * PADDING / 2, getButtonY(-1) + PADDING / 2, new Vector4f(carColour, 1), AssetManager.loadTexture("/cars/car1.png"));
        settingsMenu.addElement(carModel);

        SeekBar carColourX = settingsMenu.addSeekbar("R", carColour.x, -1, null, BUTTON_WIDTH / 4 - 2 * PADDING, BUTTON_HEIGHT - PADDING, PADDING / 2, PADDING / 2, PADDING * 1.2f);
        SeekBar carColourY = settingsMenu.addSeekbar("G", carColour.y, -1, null, BUTTON_WIDTH / 4 - 2 * PADDING, BUTTON_HEIGHT - PADDING, BUTTON_WIDTH / 4 + PADDING, PADDING / 2, PADDING * 1.2f);
        SeekBar carColourZ = settingsMenu.addSeekbar("B", carColour.z, -1, null, BUTTON_WIDTH / 4 - 2 * PADDING, BUTTON_HEIGHT - PADDING, (2 * BUTTON_WIDTH / 4) + 3 * PADDING / 2, PADDING / 2, PADDING * 1.2f);

        Runnable colourChanged = (() -> carColourChanged(carColourX, carColourY, carColourZ, colourPreview, carModel, carColour));
        carColourX.setOnValueChanged(colourChanged);
        carColourY.setOnValueChanged(colourChanged);
        carColourZ.setOnValueChanged(colourChanged);

        Button toggleCarModel = settingsMenu.addButton("CAR MODEL " + carModelNumber, 0);
        toggleCarModel.setAction(() -> carModelChanged(toggleCarModel, carModel));

        settingsMenu.addButton("SAVE AND EXIT", -2, () -> {
            Storage.getStorage().save();
            navigationPop();
        });

        settingsMenu.hide();
    }

    private void initMultiplayerAddress(Consumer<InetAddress> startMultiplayer) {
        NumericInput ipAddress = multiplayerAddressMenu.addNumericInput(IP_MAX_LENGTH, 1);

        multiplayerAddressMenu.addButton("START", 0, () -> {
            try {
                InetAddress address = InetAddress.getByName(ipAddress.getTextValue());
                startMultiplayer.accept(address);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        });

        multiplayerAddressMenu.addButton("BACK", -1, this::navigationPop);

        multiplayerAddressMenu.hide();
    }

    private void backgroundVolumeChanged(SeekBar seekBar) {
        Storage.getStorage().setValue(Storage.KEY_BACKGROUND_MUSIC_VOLUME, seekBar.getValue());
        MessageBus.fire(new SoundSettingsEvent());
    }

    private void fxVolumeChanged(SeekBar seekBar) {
        Storage.getStorage().setValue(Storage.KEY_SFX_VOLUME, seekBar.getValue());
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

}
