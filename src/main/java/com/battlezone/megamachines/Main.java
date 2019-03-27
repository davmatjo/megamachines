package com.battlezone.megamachines;

import com.battlezone.megamachines.entities.cars.AffordThoroughbred;
import com.battlezone.megamachines.events.game.GameStateEvent;
import com.battlezone.megamachines.events.ui.ErrorEvent;
import com.battlezone.megamachines.input.Cursor;
import com.battlezone.megamachines.input.GameInput;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.renderer.Window;
import com.battlezone.megamachines.renderer.theme.Theme;
import com.battlezone.megamachines.renderer.theme.ThemeHandler;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.renderer.ui.menu.LeaderboardScene;
import com.battlezone.megamachines.renderer.ui.menu.MainMenu;
import com.battlezone.megamachines.renderer.ui.menu.MenuBackground;
import com.battlezone.megamachines.sound.SoundEngine;
import com.battlezone.megamachines.storage.Storage;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.util.Triple;
import com.battlezone.megamachines.world.Lobby;
import com.battlezone.megamachines.world.ScaleController;
import com.battlezone.megamachines.world.SingleplayerWorld;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.TrackPiece;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

public class Main {

    /*
    This variable stores the time at which the last crank was performed
     */
    private static double lastCrank = -1;

    /**
     * This variable holds the length of the last time stamp
     */
    private static double lengthOfTimestamp;

    private final Cursor cursor;
    private final MainMenu menu;
    private Vector3f carColour = Storage.getStorage().getVector3f(Storage.CAR_COLOUR, new Vector3f(1, 1, 1));
    private int carModel = Storage.getStorage().getInt(Storage.CAR_MODEL, 1);


    public Main() {
        MessageBus.register(this);
        Window window = Window.getWindow();
        long gameWindow = window.getGameWindow();

        //need to instantiate sound engine
        SoundEngine.getSoundEngine();

        this.cursor = Cursor.getCursor();
        this.menu = new MainMenu(this::startSingleplayer, this::startMultiplayer);

        GameInput gameInput = GameInput.getGameInput();
        glfwSetKeyCallback(gameWindow, gameInput);

        while (!glfwWindowShouldClose(window.getGameWindow())) {

            lengthOfTimestamp = (System.currentTimeMillis() - lastCrank) / 1000;
            lastCrank = System.currentTimeMillis();

            glfwPollEvents();
            glClear(GL_COLOR_BUFFER_BIT);
            menu.render();

            glfwSwapBuffers(gameWindow);
        }
    }

    public static void main(String[] args) {
        AssetManager.setIsHeadless(false);
        new Main();
    }

    public void startMultiplayer(InetAddress address, byte roomNumber) {
        menu.hide();
        try {
            new Lobby(address, roomNumber);
        } catch (ConnectException e) {
            menu.show();
            MessageBus.fire(new ErrorEvent("ERROR CONNECTING", "CONNECTION REFUSED", 2));
        } catch (SocketException e) {
            menu.show();
            MessageBus.fire(new ErrorEvent("ERROR CONNECTING", "INVALID ADDRESS", 2));
        } catch (IOException e) {
            menu.show();
            e.printStackTrace();
            MessageBus.fire(new ErrorEvent("ERROR CONNECTING", "IO EXCEPTION", 2));
        }
        menu.show();

    }

    private void startSingleplayer(Triple<Track, Theme, Integer> options) {
        MessageBus.fire(new GameStateEvent(GameStateEvent.GameState.PLAYING));
        menu.hide();

        ThemeHandler.setTheme(options.getSecond());
        var track = options.getFirst();

        TrackPiece finishPiece = track.getFinishPiece();
        SingleplayerWorld world = new SingleplayerWorld(
                new ArrayList<>() {{
                    add(
                            new AffordThoroughbred(
                                    finishPiece.getX(),
                                    finishPiece.getY(),
                                    ScaleController.RWDCAR_SCALE,
                                    Storage.getStorage().getInt(Storage.CAR_MODEL, 1),
                                    Storage.getStorage().getVector3f(Storage.CAR_COLOUR, new Vector3f(1, 1, 1)), 0, 1, "Damn"));
                }},
                track,
                0, 7, options.getThird());
        world.start(true);
        var cars = world.getCars();
        menu.show();
        var leaderboard = new LeaderboardScene(menu, Colour.WHITE, Colour.BLUE, new MenuBackground(), cars);
        menu.navigationPush(leaderboard);
    }
}
