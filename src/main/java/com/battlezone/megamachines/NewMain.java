package com.battlezone.megamachines;

import com.battlezone.megamachines.entities.Cars.DordConcentrate;
import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.events.game.PlayerUpdateEvent;
import com.battlezone.megamachines.events.game.TrackUpdateEvent;
import com.battlezone.megamachines.input.Cursor;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.networking.Client;
import com.battlezone.megamachines.renderer.Window;
import com.battlezone.megamachines.renderer.ui.Menu;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.world.ScaleController;
import com.battlezone.megamachines.world.World;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.TrackPiece;
import com.battlezone.megamachines.world.track.generator.TrackCircleLoop;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

public class NewMain {

    private final InetAddress serverAddress = InetAddress.getByAddress(new byte[]{10, 42, 0, 1});
    private World world;
    private final Queue<byte[]> playerUpdates = new ConcurrentLinkedQueue<>();
    private final Queue<byte[]> trackUpdates = new ConcurrentLinkedQueue<>();
    private int playerNumber;
    private Client client;

    public NewMain() throws UnknownHostException {
        MessageBus.register(this);
        Window window = Window.getWindow();
        long gameWindow = window.getGameWindow();

        Cursor cursor = window.getCursor();
        Menu menu = new Menu(cursor,
                this::startSingleplayer, this::startMultiplayer);

        List<RWDCar> players = null;

        while (!glfwWindowShouldClose(window.getGameWindow())) {
            glfwPollEvents();
            glClear(GL_COLOR_BUFFER_BIT);
            cursor.update();
            menu.render();

            byte[] playerUpdates = this.playerUpdates.poll();
            if (playerUpdates != null) {
                players = RWDCar.fromByteArray(playerUpdates, 1);
            }

            byte[] trackUpdates = this.trackUpdates.poll();
            if (trackUpdates != null) {
                if (players == null) {
                    System.err.println("Received track before players. Fatal");
                    System.exit(-1);
                } else {
                    world = new World(players, Track.fromByteArray(trackUpdates, 1), playerNumber);
                    world.start();
                }
            }

            glfwSwapBuffers(gameWindow);
        }
        client.setRunning(false);
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
        System.out.println("Called");
        try {
            System.out.println("Starting mp");
            client = new Client(serverAddress);

        } catch (SocketException e) {
            System.err.println("Error connecting to server");
        }
    }

    private void startSingleplayer() {
        Track track = new TrackCircleLoop(20, 20, true).generateTrack();
        TrackPiece startPiece = track.getStartPiece();
        new World(List.of(new DordConcentrate(startPiece.getX(), startPiece.getY(), ScaleController.RWDCAR_SCALE, 1, new Vector3f(1f, 0, 0))), track, 0).start();
    }

    @EventListener
    public void updatePlayers(PlayerUpdateEvent event) {
        System.out.println("Player update received");
        System.out.println(Arrays.toString(event.getData()));
        playerUpdates.add(event.getData());
        playerNumber = event.getPlayerNumber();
    }

    @EventListener
    public void updateTrack(TrackUpdateEvent event) {
        System.out.println("Track update received");
        trackUpdates.add(event.getData());
    }
}
