package com.battlezone.megamachines;

import com.battlezone.megamachines.entities.Cars.DordConcentrate;
import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.events.game.GameStateEvent;
import com.battlezone.megamachines.input.Cursor;
import com.battlezone.megamachines.input.GameInput;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.renderer.Window;
import com.battlezone.megamachines.renderer.ui.Menu;
import com.battlezone.megamachines.sound.SoundEngine;
import com.battlezone.megamachines.storage.Storage;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.world.Lobby;
import com.battlezone.megamachines.world.ScaleController;
import com.battlezone.megamachines.world.SingleplayerWorld;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.TrackPiece;
import com.battlezone.megamachines.world.track.generator.TrackCircleLoop;
import com.battlezone.megamachines.world.track.generator.TrackLoopMutation;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

public class NewMain {

    private final InetAddress serverAddress = InetAddress.getByAddress(new byte[]{127, 0, 0, 1});
    /*
    This variable stores the time at which the last crank was performed
     */
    private static double lastCrank = -1;

    /**
     * This variable holds the length of the last time stamp
     */
    private static double lengthOfTimestamp;

    private final Cursor cursor;
    private final Menu menu;
    private final SoundEngine soundEngine;
    private Vector3f carColour = Storage.getStorage().getVector3f(Storage.CAR_COLOUR, new Vector3f(1, 1, 1));
    private int carModel = Storage.getStorage().getInt(Storage.CAR_MODEL, 1);


    public NewMain() throws UnknownHostException {
        MessageBus.register(this);
        Window window = Window.getWindow();
        long gameWindow = window.getGameWindow();

        this.cursor = Cursor.getCursor();
        this.menu = new Menu(
                this::startSingleplayer, this::startMultiplayer);
        this.soundEngine = new SoundEngine();

        GameInput gameInput = new GameInput();
        glfwSetKeyCallback(gameWindow, gameInput);

        List<RWDCar> players = null;

        while (!glfwWindowShouldClose(window.getGameWindow())) {

            lengthOfTimestamp = (System.currentTimeMillis() - lastCrank) / 1000;
            lastCrank = System.currentTimeMillis();

            glfwPollEvents();
            glClear(GL_COLOR_BUFFER_BIT);
            cursor.update();
            menu.render();
            gameInput.update();


            glfwSwapBuffers(gameWindow);
        }
    }

    public static void main(String[] args) {
        try {
            AssetManager.setIsHeadless(false);
            new NewMain();
        } catch (UnknownHostException e) {
            System.err.println("Unknown host. Exiting...");
        }
    }

    public void startMultiplayer(InetAddress address) {
        try {
            menu.hide();
            new Lobby(address, cursor);
            menu.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            System.err.println("Error connecting to server");
        }
    }

    private void startSingleplayer() {
        MessageBus.fire(new GameStateEvent(GameStateEvent.GameState.PLAYING));
        menu.hide();
        Track track = new TrackLoopMutation(10, 10).generateTrack();
        TrackPiece startPiece = track.getStartPiece();
        new SingleplayerWorld(
                new ArrayList<>() {{
                    add(
                            new DordConcentrate(
                                    startPiece.getX(),
                                    startPiece.getY(),
                                    ScaleController.RWDCAR_SCALE,
                                    Storage.getStorage().getInt(Storage.CAR_MODEL, 1),
                                    Storage.getStorage().getVector3f(Storage.CAR_COLOUR, new Vector3f(1, 1, 1)), 0, 1));
                }},
                track,
                0,
                2).start();
        menu.show();
    }
}
