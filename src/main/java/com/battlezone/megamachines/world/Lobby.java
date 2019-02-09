package com.battlezone.megamachines.world;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.events.game.PlayerUpdateEvent;
import com.battlezone.megamachines.events.game.TrackUpdateEvent;
import com.battlezone.megamachines.input.Cursor;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.networking.Client;
import com.battlezone.megamachines.networking.Player;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.Window;
import com.battlezone.megamachines.renderer.ui.Box;
import com.battlezone.megamachines.renderer.ui.Button;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.renderer.ui.Scene;
import com.battlezone.megamachines.world.track.Track;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;

public class Lobby {

    private final Queue<byte[]> playerUpdates = new ConcurrentLinkedQueue<>();
    private final Queue<byte[]> trackUpdates = new ConcurrentLinkedQueue<>();
    private final Scene lobby;
    private final Client client;
    private final Cursor cursor;
    private boolean isHost = false;
    private int playerNumber;
    private final long gameWindow;

    public Lobby(InetAddress serverAddress, Cursor cursor) throws SocketException {
        MessageBus.register(this);

        this.gameWindow = Window.getWindow().getGameWindow();
        this.lobby = new Scene();

        this.cursor = cursor;

        this.client = new Client(serverAddress);
        run();
    }

    private void run() {
        List<RWDCar> players = null;
        List<Box> models = new ArrayList<>();

        while (!glfwWindowShouldClose(gameWindow)) {
            glfwPollEvents();

            byte[] playerUpdates = this.playerUpdates.poll();
            if (playerUpdates != null) {
                players = RWDCar.fromByteArray(playerUpdates, 1);
                if (!isHost && playerNumber == 0) {
                    isHost = true;
                    Button start = new Button(
                            1f, 0.5f, -0.5f, -0.5f, Colour.WHITE, Colour.BLUE, "START", 0.05f, cursor);
                    lobby.addElement(start);
                    start.setAction(client::startGame);
                }
                models.forEach(lobby::removeElement);
                models.forEach(Box::delete);
                models.clear();
                for (int i = 0; i < players.size(); i++) {
                    models.add(new Box(0.2f, 0.4f, i * 0.5f, 0.5f, new Vector4f(players.get(i).getColour(), 1f), Texture.CIRCLE));
                }
            }

            byte[] trackUpdates = this.trackUpdates.poll();
            if (trackUpdates != null) {
                if (players == null) {
                    System.err.println("Received track before players. Fatal");
                    System.exit(-1);
                } else {
                    World world = new World(players, Track.fromByteArray(trackUpdates, 1), playerNumber, 0);
                    world.start();
                }
            }

            glClear(GL_COLOR_BUFFER_BIT);
            lobby.render();
            cursor.update();

            glfwSwapBuffers(gameWindow);
        }
        client.close();
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
