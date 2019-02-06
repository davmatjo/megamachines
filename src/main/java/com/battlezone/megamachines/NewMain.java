package com.battlezone.megamachines;

import com.battlezone.megamachines.events.game.PlayerUpdateEvent;
import com.battlezone.megamachines.input.Cursor;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.networking.Client;
import com.battlezone.megamachines.renderer.Window;
import com.battlezone.megamachines.renderer.ui.Menu;
import com.battlezone.megamachines.world.World;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

public class NewMain {

    private final InetAddress serverAddress = InetAddress.getByAddress(new byte[]{10, 42, 0, 1});
    private World world;

    public NewMain() throws UnknownHostException {
        MessageBus.register(this);
        Window window = Window.getWindow();
        long gameWindow = window.getGameWindow();

        Cursor cursor = window.getCursor();
        Menu menu = new Menu(cursor,
                this::startSingleplayer, this::startMultiplayer);

        while (!glfwWindowShouldClose(window.getGameWindow())) {
            glfwPollEvents();
            glClear(GL_COLOR_BUFFER_BIT);
            cursor.update();
            menu.render();
            glfwSwapBuffers(gameWindow);
        }

    }

    public static void main(String[] args) {
        try {
            System.out.println(Vector3f.fromByteArray(new Vector3f(1.0f, 0, 0).toByteArray(), 0));

            new NewMain();
        } catch (UnknownHostException e) {
            System.err.println("Unknown host. Exiting...");
        }
    }

    public void startMultiplayer() {
        System.out.println("Called");
        try {
            System.out.println("Starting mp");
            Client client = new Client(serverAddress);

        } catch (SocketException e) {
            System.err.println("Error connecting to server");
        }
    }

    private void startSingleplayer() {

    }

    @EventListener
    public void updatePlayers(PlayerUpdateEvent event) {
        if (event.isRunning()) {
            if (world != null) {
                world.setRunning(false);
            }
            world = new World(event.getCars(), event.getTrack(), event.getPlayerNumber());
            world.start();
        }
        System.out.println(event.getCars());
    }
}
