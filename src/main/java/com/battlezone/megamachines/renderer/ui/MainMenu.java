package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.events.ui.ErrorEvent;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.sound.SoundSettingsEvent;
import com.battlezone.megamachines.storage.Storage;
import com.battlezone.megamachines.util.AssetManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.function.BiConsumer;

import static com.battlezone.megamachines.renderer.ui.MenuScene.*;

public class MainMenu extends AbstractMenu {

    private final MenuScene mainMenu;
    private final MenuScene settingsMenu;
    private final MenuScene multiplayerAddressMenu;

    private static final int MAX_CAR_MODEL = 3;
    private static final int IP_MAX_LENGTH = 15;

    private static final MenuBackground background = new MenuBackground();

    public MainMenu(Runnable startSingleplayer, BiConsumer<InetAddress, Byte> startMultiplayer) {
        this.mainMenu = new MenuScene(Colour.WHITE, Colour.BLUE, background);
        this.settingsMenu = new MenuScene(Colour.WHITE, Colour.BLUE, background);
        this.multiplayerAddressMenu = new MenuScene(Colour.WHITE, Colour.BLUE, background);

        initMainMenu(startSingleplayer);
        initSettings();
        initMultiplayerAddress(startMultiplayer);

        currentScene = mainMenu;
    }

    private void initMainMenu(Runnable startSingleplayer) {
        mainMenu.addLabel("MEGA MACHINES", 2, 0.88f, Colour.BLACK);
        mainMenu.addButton("SINGLEPLAYER", 1, startSingleplayer);
        mainMenu.addButton("MULTIPLAYER", 0, (() -> navigationPush(multiplayerAddressMenu)));
        mainMenu.addButton("SETTINGS", -1, (() -> navigationPush(settingsMenu)));
    }

    private void initSettings() {
        float backgroundVolume = Storage.getStorage().getFloat(Storage.BACKGROUND_MUSIC_VOLUME, 1);
        float fxVolume = Storage.getStorage().getFloat(Storage.SFX_VOLUME, 1);
        int carModelNumber = Storage.getStorage().getInt(Storage.CAR_MODEL, 1);
        Vector3f carColour = Storage.getStorage().getVector3f(Storage.CAR_COLOUR, new Vector3f(1, 1, 1));

        SeekBar backgroundToggle = settingsMenu.addSeekbar("BACKGROUND MUSIC", backgroundVolume, 2);
        backgroundToggle.setOnValueChanged(() -> backgroundVolumeChanged(backgroundToggle));

        SeekBar fxToggle = settingsMenu.addSeekbar("SFX", fxVolume, 1);
        fxToggle.setOnValueChanged(() -> fxVolumeChanged(fxToggle));

        Box colourPreview = new Box(BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_X, getButtonY(-1), new Vector4f(carColour, 1));
        settingsMenu.addElement(colourPreview);

        Box carModel = new Box((BUTTON_WIDTH / 4) - 3 * PADDING, BUTTON_HEIGHT - PADDING, BUTTON_X + getRowX(4, 4), getButtonY(-1) + PADDING / 2, new Vector4f(carColour, 1), AssetManager.loadTexture("/cars/car" + carModelNumber + ".png"));
        settingsMenu.addElement(carModel);

        SeekBar carColourX = settingsMenu.addSeekbar("R", carColour.x, -1, null, BUTTON_WIDTH / 4 - 2 * PADDING, BUTTON_HEIGHT - PADDING, getRowX(1, 4), PADDING / 2, PADDING * 1.2f);
        SeekBar carColourY = settingsMenu.addSeekbar("G", carColour.y, -1, null, BUTTON_WIDTH / 4 - 2 * PADDING, BUTTON_HEIGHT - PADDING, getRowX(2, 4), PADDING / 2, PADDING * 1.2f);
        SeekBar carColourZ = settingsMenu.addSeekbar("B", carColour.z, -1, null, BUTTON_WIDTH / 4 - 2 * PADDING, BUTTON_HEIGHT - PADDING, getRowX(3, 4), PADDING / 2, PADDING * 1.2f);

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

    // x offset when laying out items in a row
    private float getRowX(int position, int total) {
        return ((position - 1) * BUTTON_WIDTH / total) + position * PADDING / 2;
    }

    private void initMultiplayerAddress(BiConsumer<InetAddress, Byte> startMultiplayer) {
        NumericInput roomNumber = multiplayerAddressMenu.addNumericInput("ROOM NUMBER", IP_MAX_LENGTH, 1);
        NumericInput ipAddress = multiplayerAddressMenu.addNumericInput(Storage.getStorage().getString(Storage.IP_ADDRESS, "IP"), IP_MAX_LENGTH, 0);

        Button start = multiplayerAddressMenu.addButton("START", -1);
        start.setAction(() -> {
            try {
                byte room = Byte.parseByte(roomNumber.getTextValue());
                InetAddress address = InetAddress.getByName(ipAddress.getTextValue());
                Storage.getStorage().setValue(Storage.IP_ADDRESS, ipAddress.getTextValue());
                Storage.getStorage().save();
                startMultiplayer.accept(address, room);
            } catch (NumberFormatException e) {
                MessageBus.fire(new ErrorEvent("ERROR", "INVALID ROOM NUMBER", 2));
            } catch (UnknownHostException e) {
                MessageBus.fire(new ErrorEvent("ERROR CONNECTING", "UNKNOWN HOST", 2));
            }
        });

        multiplayerAddressMenu.addButton("BACK", -2, this::navigationPop);

        multiplayerAddressMenu.hide();
    }

    private void backgroundVolumeChanged(SeekBar seekBar) {
        Storage.getStorage().setValue(Storage.BACKGROUND_MUSIC_VOLUME, seekBar.getValue());
        MessageBus.fire(new SoundSettingsEvent());
    }

    private void fxVolumeChanged(SeekBar seekBar) {
        Storage.getStorage().setValue(Storage.SFX_VOLUME, seekBar.getValue());
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
