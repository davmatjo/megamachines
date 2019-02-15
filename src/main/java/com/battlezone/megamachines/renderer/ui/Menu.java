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

public class Menu {

    private Scene currentScene;
    private final Scene mainMenu;
    private final Scene settingsMenu;
    private final Scene multiplayerAddressMenu;
    private static final float BUTTON_WIDTH = 3f;
    private static final float BUTTON_HEIGHT = 0.25f;
    private static final float BUTTON_X = -BUTTON_WIDTH / 2;
    private static final float BUTTON_CENTRE_Y = -0.125f;
    private static final float BUTTON_OFFSET_Y = 0.4f;
    private static final float PADDING = 0.05f;
    private static final int MAX_CAR_MODEL = 3;
    private static final int IP_MAX_LENGTH = 15;

    public Menu(Runnable startSingleplayer, BiConsumer<InetAddress, Byte> startMultiplayer) {
        this.mainMenu = new Scene();
        this.settingsMenu = new Scene();
        this.multiplayerAddressMenu = new Scene();

        initMainMenu(startSingleplayer);
        initSettings();
        initMultiplayerAddress(startMultiplayer);

        currentScene = mainMenu;
        currentScene.show();
    }

    public static float getButtonY(int numberFromCenter) {
        return BUTTON_CENTRE_Y + numberFromCenter * BUTTON_OFFSET_Y;
    }

    private void initMainMenu(Runnable startSingleplayer) {
        Button singleplayer = new Button(BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_X, getButtonY(1), Colour.WHITE, Colour.BLUE, "SINGLEPLAYER", PADDING);
        Button multiplayer = new Button(BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_X, getButtonY(0), Colour.WHITE, Colour.BLUE, "MULTIPLAYER", PADDING);
        Button settings = new Button(BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_X, getButtonY(-1), Colour.WHITE, Colour.BLUE, "SETTINGS", PADDING);

        singleplayer.setAction(startSingleplayer);
        multiplayer.setAction(this::startMultiplayerPressed);
        settings.setAction(this::showSettings);

        mainMenu.addElement(singleplayer);
        mainMenu.addElement(multiplayer);
        mainMenu.addElement(settings);
        mainMenu.hide();
    }

    private void initSettings() {
        float backgroundVolume = Storage.getStorage().getFloat(Storage.KEY_BACKGROUND_MUSIC_VOLUME, 1);
        float fxVolume = Storage.getStorage().getFloat(Storage.KEY_SFX_VOLUME, 1);
        int carModelNumber = Storage.getStorage().getInt(Storage.CAR_MODEL, 1);
        Vector3f carColour = Storage.getStorage().getVector3f(Storage.CAR_COLOUR, new Vector3f(1, 1, 1));

        SeekBar backgroundToggle = new SeekBar(BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_X, getButtonY(2), Colour.WHITE, Colour.BLUE, "BACKGROUND MUSIC", backgroundVolume, PADDING);
        backgroundToggle.setOnValueChanged(() -> backgroundVolumeChanged(backgroundToggle));
        settingsMenu.addElement(backgroundToggle);

        SeekBar fxToggle = new SeekBar(BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_X, getButtonY(1), Colour.WHITE, Colour.BLUE, "SFX", fxVolume, PADDING);
        fxToggle.setOnValueChanged(() -> fxVolumeChanged(fxToggle));
        settingsMenu.addElement(fxToggle);

        Box colourPreview = new Box(BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_X, getButtonY(-1), new Vector4f(carColour, 1));
        settingsMenu.addElement(colourPreview);

        Box carModel = new Box((BUTTON_WIDTH / 4) - 3 * PADDING, BUTTON_HEIGHT - PADDING, BUTTON_X + (3 * BUTTON_WIDTH / 4) + 3 * PADDING / 2, getButtonY(-1) + PADDING / 2, new Vector4f(carColour, 1), AssetManager.loadTexture("/cars/car1.png"));
        settingsMenu.addElement(carModel);

        SeekBar carColourX = new SeekBar((BUTTON_WIDTH / 4) - 2 * PADDING, BUTTON_HEIGHT - PADDING, PADDING / 2 + BUTTON_X, getButtonY(-1) + PADDING / 2, Colour.WHITE, Colour.RED, "R", carColour.x, PADDING * 1.2f);
        carColourX.setOnValueChanged(() -> colourChangedX(carColourX, colourPreview, carModel, carColour));
        settingsMenu.addElement(carColourX);

        SeekBar carColourY = new SeekBar((BUTTON_WIDTH / 4) - 2 * PADDING, BUTTON_HEIGHT - PADDING, BUTTON_X + (BUTTON_WIDTH / 4) + PADDING, getButtonY(-1) + PADDING / 2, Colour.WHITE, Colour.GREEN, "G", carColour.y, PADDING * 1.2f);
        carColourY.setOnValueChanged(() -> colourChangedY(carColourY, colourPreview, carModel, carColour));
        settingsMenu.addElement(carColourY);

        SeekBar carColourZ = new SeekBar((BUTTON_WIDTH / 4) - 2 * PADDING, BUTTON_HEIGHT - PADDING, BUTTON_X + (2 * BUTTON_WIDTH / 4) + 3 * PADDING / 2, getButtonY(-1) + PADDING / 2, Colour.WHITE, Colour.BLUE, "B", carColour.z, PADDING * 1.2f);
        carColourZ.setOnValueChanged(() -> colourChangedZ(carColourZ, colourPreview, carModel, carColour));
        settingsMenu.addElement(carColourZ);

        Button toggleCarModel = new Button(BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_X, getButtonY(0), Colour.WHITE, Colour.BLUE, "CAR MODEL " + carModelNumber, PADDING);
        toggleCarModel.setAction(() -> carModelChanged(toggleCarModel, carModel));
        settingsMenu.addElement(toggleCarModel);

        Button back = new Button(BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_X, getButtonY(-2), Colour.WHITE, Colour.BLUE, "SAVE AND EXIT", PADDING);
        back.setAction(() -> {
            Storage.getStorage().save();
            settingsMenu.hide();
            currentScene = mainMenu;
            mainMenu.show();
        });
        settingsMenu.addElement(back);
        settingsMenu.hide();
    }

    private void initMultiplayerAddress(BiConsumer<InetAddress, Byte> startMultiplayer) {
        NumericInput roomNumber = new NumericInput(BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_X, getButtonY(1), Colour.WHITE, PADDING, IP_MAX_LENGTH, "ROOM NUMBER");
        multiplayerAddressMenu.addElement(roomNumber);

        NumericInput ipAddress = new NumericInput(BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_X, getButtonY(0), Colour.WHITE, PADDING, IP_MAX_LENGTH, "IP");
        multiplayerAddressMenu.addElement(ipAddress);

        Button start = new Button(BUTTON_WIDTH / 2 - PADDING, BUTTON_HEIGHT, BUTTON_X + (BUTTON_WIDTH / 2) + PADDING, getButtonY(-1), Colour.WHITE, Colour.BLUE, "START", PADDING);
        start.setAction(() -> {
            try {
                byte room = Byte.parseByte(roomNumber.getTextValue());
                InetAddress address = InetAddress.getByName(ipAddress.getTextValue());
                startMultiplayer.accept(address, room);
            } catch (NumberFormatException e) {
                MessageBus.fire(new ErrorEvent("ERROR", "INVALID ROOM NUMBER", 2));
            } catch (UnknownHostException e) {
                MessageBus.fire(new ErrorEvent("ERROR CONNECTING", "UNKNOWN HOST", 2));
            }
        });
        multiplayerAddressMenu.addElement(start);

        Button back = new Button(BUTTON_WIDTH / 2 - PADDING, BUTTON_HEIGHT, BUTTON_X, getButtonY(-1), Colour.WHITE, Colour.BLUE, "BACK", PADDING);
        back.setAction(() -> {
            multiplayerAddressMenu.hide();
            currentScene = mainMenu;
            mainMenu.show();
        });
        multiplayerAddressMenu.addElement(back);
    }


    private void startMultiplayerPressed() {
        mainMenu.hide();
        multiplayerAddressMenu.show();
        currentScene = multiplayerAddressMenu;
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

    private void colourChangedX(SeekBar seekBar, Box colourPreview, Box carPreview, Vector3f currentColour) {
        currentColour.x = seekBar.getValue();
        setColour(colourPreview, carPreview, currentColour);
    }

    private void colourChangedY(SeekBar seekBar, Box colourPreview, Box carPreview, Vector3f currentColour) {
        currentColour.y = seekBar.getValue();
        setColour(colourPreview, carPreview, currentColour);
    }

    private void colourChangedZ(SeekBar seekBar, Box colourPreview, Box carPreview, Vector3f currentColour) {
        currentColour.z = seekBar.getValue();
        setColour(colourPreview, carPreview, currentColour);
    }

    private void setColour(Box colourPreview, Box carPreview, Vector3f currentColour) {
        colourPreview.setColour(new Vector4f(currentColour, 1));
        carPreview.setColour(new Vector4f(currentColour, 1));
        Storage.getStorage().setValue(Storage.CAR_COLOUR, currentColour);
    }

    private void showSettings() {
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
