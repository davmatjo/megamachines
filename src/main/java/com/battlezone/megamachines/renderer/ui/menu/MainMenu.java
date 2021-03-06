package com.battlezone.megamachines.renderer.ui.menu;

import com.battlezone.megamachines.events.ui.ErrorEvent;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.renderer.Window;
import com.battlezone.megamachines.renderer.theme.Theme;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.renderer.ui.elements.Button;
import com.battlezone.megamachines.renderer.ui.elements.NumericInput;
import com.battlezone.megamachines.storage.Storage;
import com.battlezone.megamachines.util.Triple;
import com.battlezone.megamachines.world.track.Track;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.battlezone.megamachines.renderer.ui.menu.MenuScene.*;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

/**
 * The main menu of the game
 */
public class MainMenu extends BaseMenu {

    private static final int IP_MAX_LENGTH = 15;
    private static final MenuBackground background = new MenuBackground();
    private final MenuScene mainMenu;
    private final SettingsMenuScene settingsMenu;
    private final MenuScene multiplayerAddressMenu;
    private final TrackSelectionScene trackSelectionScene;
    private final TrackManagementScene trackManagementScene;

    /**
     * @param startSingleplayer The consumer for starting a singleplayer game
     * @param startMultiplayer  The consumer for starting a multiplayer game
     */
    public MainMenu(Consumer<Triple<Track, Theme, Integer>> startSingleplayer, BiConsumer<InetAddress, Byte> startMultiplayer) {
        this.mainMenu = new MenuScene(Colour.WHITE, Colour.BLUE, background);
        this.settingsMenu = new SettingsMenuScene(this, Colour.WHITE, Colour.BLUE, background);
        this.multiplayerAddressMenu = new MenuScene(Colour.WHITE, Colour.BLUE, background);
        this.trackSelectionScene = new TrackSelectionScene(this, Colour.WHITE, Colour.BLUE, background, startSingleplayer);
        this.trackManagementScene = new TrackManagementScene(this, Colour.WHITE, Colour.BLUE, background);

        initMainMenu();
        initMultiplayerAddress(startMultiplayer);

        navigationPush(mainMenu);
    }

    private void initMainMenu() {
        //Init the buttons
        mainMenu.addLabel("MEGA MACHINES", 2, 0.88f, Colour.WHITE);
        mainMenu.addButton("SINGLEPLAYER", 1, () -> navigationPush(trackSelectionScene));
        mainMenu.addButton("MULTIPLAYER", 0, () -> navigationPush(multiplayerAddressMenu));
        mainMenu.addButton("TRACKS", -1, () -> navigationPush(trackManagementScene));

        mainMenu.addButton("SETTINGS", -2, () -> navigationPush(settingsMenu), 1, 2);
        mainMenu.addButton("QUIT", -2, this::quit, 2, 2);
    }

    private void quit() {
        glfwSetWindowShouldClose(Window.getWindow().getGameWindow(), true);
    }

    /**
     * Set up multiplayer game menu
     *
     * @param startMultiplayer Runnable to start the game
     */
    private void initMultiplayerAddress(BiConsumer<InetAddress, Byte> startMultiplayer) {
        multiplayerAddressMenu.addLabel("MULTIPLAYER", 1.5f, 1f, Colour.WHITE);

        NumericInput roomNumber = multiplayerAddressMenu.addNumericInput(Storage.getStorage().getString(Storage.ROOM_NUMBER, "ROOM NUMBER"), IP_MAX_LENGTH, 0.5f);
        NumericInput ipAddress = multiplayerAddressMenu.addNumericInput(Storage.getStorage().getString(Storage.IP_ADDRESS, "IP"), IP_MAX_LENGTH, -0.5f);

        Button start = multiplayerAddressMenu.addButton("START", -1.5f, null, BUTTON_WIDTH / 2 - PADDING, BUTTON_HEIGHT, BUTTON_WIDTH / 2 + PADDING);
        start.setAction(() -> {
            try {
                byte room = Byte.parseByte(roomNumber.getDisplayedValue());
                InetAddress address = InetAddress.getByName(ipAddress.getDisplayedValue());
                Storage.getStorage().setValue(Storage.ROOM_NUMBER, room);
                Storage.getStorage().setValue(Storage.IP_ADDRESS, ipAddress.getDisplayedValue());
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
