package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.events.ui.ErrorEvent;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.storage.Storage;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.function.BiConsumer;

import static com.battlezone.megamachines.renderer.ui.MenuScene.*;

public class MainMenu extends AbstractMenu {

    private final MenuScene mainMenu;
    private final MenuScene settingsMenu;
    private final MenuScene multiplayerAddressMenu;

    private static final int IP_MAX_LENGTH = 15;

    private static final MenuBackground background = new MenuBackground();

    public MainMenu(Runnable startSingleplayer, BiConsumer<InetAddress, Byte> startMultiplayer) {
        this.mainMenu = new MenuScene(Colour.WHITE, Colour.BLUE, background);
        this.settingsMenu = new SettingsMenuScene(this, Colour.WHITE, Colour.BLUE, background);
        this.multiplayerAddressMenu = new MenuScene(Colour.WHITE, Colour.BLUE, background);

        initMainMenu(startSingleplayer);
        initMultiplayerAddress(startMultiplayer);

        currentScene = mainMenu;
        currentScene.show();
    }

    private void initMainMenu(Runnable startSingleplayer) {
        mainMenu.addLabel("MEGA MACHINES", 2, 0.88f, Colour.WHITE);
        mainMenu.addButton("SINGLEPLAYER", 1, startSingleplayer);
        mainMenu.addButton("MULTIPLAYER", 0, (() -> navigationPush(multiplayerAddressMenu)));
        mainMenu.addButton("SETTINGS", -1, (() -> navigationPush(settingsMenu)));
    }

    private void initMultiplayerAddress(BiConsumer<InetAddress, Byte> startMultiplayer) {
        NumericInput roomNumber = multiplayerAddressMenu.addNumericInput("ROOM NUMBER", IP_MAX_LENGTH, 1);
        NumericInput ipAddress = multiplayerAddressMenu.addNumericInput(Storage.getStorage().getString(Storage.IP_ADDRESS, "IP"), IP_MAX_LENGTH, 0);

        Button start = multiplayerAddressMenu.addButton("START", -1, null, BUTTON_WIDTH / 2 - PADDING, BUTTON_HEIGHT, BUTTON_WIDTH / 2 + PADDING);
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

        multiplayerAddressMenu.addButton("BACK", -1, this::navigationPop, BUTTON_WIDTH / 2 - PADDING, BUTTON_HEIGHT, 0);

        multiplayerAddressMenu.hide();
    }


}
