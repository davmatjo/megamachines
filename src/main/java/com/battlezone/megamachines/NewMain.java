package com.battlezone.megamachines;

import com.battlezone.megamachines.entities.Cars.DordConcentrate;
import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.input.Cursor;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.networking.Client;
import com.battlezone.megamachines.renderer.Window;
import com.battlezone.megamachines.renderer.ui.Menu;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.world.Lobby;
import com.battlezone.megamachines.world.ScaleController;
import com.battlezone.megamachines.world.World;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.TrackPiece;
import com.battlezone.megamachines.world.track.generator.TrackCircleLoop;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
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


    public NewMain() throws UnknownHostException {
        MessageBus.register(this);
        Window window = Window.getWindow();
        long gameWindow = window.getGameWindow();

        this.cursor = window.getCursor();
        this.menu = new Menu(cursor,
                this::startSingleplayer, this::startMultiplayer);

        List<RWDCar> players = null;

        while (!glfwWindowShouldClose(window.getGameWindow())) {

            lengthOfTimestamp = (System.currentTimeMillis() - lastCrank) / 1000;
            lastCrank = System.currentTimeMillis();

            glfwPollEvents();
            glClear(GL_COLOR_BUFFER_BIT);
            cursor.update();
            menu.render();


            glfwSwapBuffers(gameWindow);
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println(Arrays.toString(new Vector3f(1.0f, 0, 0).toByteArray()));
            AssetManager.setIsHeadless(false);
            new NewMain();
        } catch (UnknownHostException e) {
            System.err.println("Unknown host. Exiting...");
        }
    }

    public void startMultiplayer() {
        try {
            menu.hide();
            new Lobby(serverAddress, cursor);
            menu.show();
        } catch (SocketException e) {
            e.printStackTrace();
            System.err.println("Error connecting to server");
        }
    }

    private void startSingleplayer() {
        Track track = new TrackCircleLoop(20, 20, true).generateTrack();
        TrackPiece startPiece = track.getStartPiece();
        new World(
                new ArrayList<>() {{
                    add(new DordConcentrate(startPiece.getX(), startPiece.getY(), ScaleController.RWDCAR_SCALE, 1, new Vector3f(1f, 0, 0)));
                }},
                track,
                0,
                2).start();
    }
}
