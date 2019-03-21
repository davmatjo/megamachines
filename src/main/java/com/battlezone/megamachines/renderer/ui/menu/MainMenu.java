package com.battlezone.megamachines.renderer.ui.menu;

import com.battlezone.megamachines.events.ui.ErrorEvent;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.renderer.Window;
import com.battlezone.megamachines.renderer.theme.Theme;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.renderer.ui.elements.Button;
import com.battlezone.megamachines.renderer.ui.elements.NumericInput;
import com.battlezone.megamachines.storage.Storage;
import com.battlezone.megamachines.world.track.Track;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.function.BiConsumer;

import static com.battlezone.megamachines.renderer.ui.menu.MenuScene.*;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

public class MainMenu extends BaseMenu {

    private final MenuScene mainMenu;
    private final SettingsMenuScene settingsMenu;
    private final MenuScene multiplayerAddressMenu;
    private final TrackSelectionScene trackSelectionScene;

    private static final int IP_MAX_LENGTH = 15;

    private static final MenuBackground background = new MenuBackground();

    public MainMenu(BiConsumer<Track, Theme> startSingleplayer, BiConsumer<InetAddress, Byte> startMultiplayer) {
        this.mainMenu = new MenuScene(Colour.WHITE, Colour.BLUE, background);
        this.settingsMenu = new SettingsMenuScene(this, Colour.WHITE, Colour.BLUE, background);
        this.multiplayerAddressMenu = new MenuScene(Colour.WHITE, Colour.BLUE, background);
        this.trackSelectionScene = new TrackSelectionScene(this, Colour.WHITE, Colour.BLUE, background, startSingleplayer);

        initMainMenu();
        initMultiplayerAddress(startMultiplayer);

        navigationPush(mainMenu);
    }

    private void initMainMenu() {
        mainMenu.addLabel("MEGA MACHINES", 2, 0.88f, Colour.WHITE);
        mainMenu.addButton("SINGLEPLAYER", 1, () -> navigationPush(trackSelectionScene));
        mainMenu.addButton("MULTIPLAYER", 0, () -> navigationPush(multiplayerAddressMenu));
        mainMenu.addButton("SETTINGS", -1, () -> navigationPush(settingsMenu));
        mainMenu.addButton("QUIT", -2, () -> glfwSetWindowShouldClose(Window.getWindow().getGameWindow(), true));
    }

    private void initMultiplayerAddress(BiConsumer<InetAddress, Byte> startMultiplayer) {
        multiplayerAddressMenu.addLabel("MULTIPLAYER", 1.5f, 1f, Colour.WHITE);

        NumericInput roomNumber = multiplayerAddressMenu.addNumericInput(Storage.getStorage().getString(Storage.ROOM_NUMBER, "ROOM NUMBER"), IP_MAX_LENGTH, 0.5f);
        NumericInput ipAddress = multiplayerAddressMenu.addNumericInput(Storage.getStorage().getString(Storage.IP_ADDRESS, "IP"), IP_MAX_LENGTH, -0.5f);

        Button start = multiplayerAddressMenu.addButton("START", -1.5f, null, BUTTON_WIDTH / 2 - PADDING, BUTTON_HEIGHT, BUTTON_WIDTH / 2 + PADDING);
        start.setAction(() -> {
            try {
                byte room = Byte.parseByte(roomNumber.getTextValue());
                InetAddress address = InetAddress.getByName(ipAddress.getTextValue());
                Storage.getStorage().setValue(Storage.ROOM_NUMBER, room);
                Storage.getStorage().setValue(Storage.IP_ADDRESS, ipAddress.getTextValue());
                Storage.getStorage().save();
                startMultiplayer.accept(address, room);
            } catch (NumberFormatException e) {
                MessageBus.fire(new ErrorEvent("ERROR", "INVALID ROOM NUMBER", 2));
            } catch (UnknownHostException e) {
                MessageBus.fire(new ErrorEvent("ERROR CONNECTING", "UNKNOWN HOST", 2));
            }
        });

        multiplayerAddressMenu.addButton("BACK", -1.5f, this::navigationPop, BUTTON_WIDTH / 2 - PADDING, BUTTON_HEIGHT, 0);

        multiplayerAddressMenu.hide();
    }

}
