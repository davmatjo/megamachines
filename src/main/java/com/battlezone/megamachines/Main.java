package com.battlezone.megamachines;

import com.battlezone.megamachines.entities.Cars.DordConcentrate;
import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.input.Cursor;
import com.battlezone.megamachines.input.GameInput;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.physics.PhysicsEngine;
import com.battlezone.megamachines.renderer.game.*;
import com.battlezone.megamachines.renderer.ui.Box;
import com.battlezone.megamachines.renderer.ui.Button;
import com.battlezone.megamachines.renderer.ui.Label;
import com.battlezone.megamachines.renderer.ui.Scene;
import com.battlezone.megamachines.world.Track;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;

public class Main {

    private static final double FRAME_CAP = (1000000000.0/60.0);
    public static float aspectRatio;
    public static GameInput gameInput;

    private Main() {
//        FontConvertor.toPNG("font.png");
        // Attempt to initialise GLFW
        if (!glfwInit()) {
            System.err.println("GLFW Failed to initialise");
            System.exit(-1);
        }

        GLFWVidMode mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        int width = 1920;
        int height = 1080;
        // Create window
        long gameWindow = glfwCreateWindow(width, height, "MegaMachines", 0, 0);

        Cursor cursor = new Cursor(gameWindow, width, height);

        glfwSwapInterval(1);

        // Create a Server to run the game and start it
//        Server server = new Server();
//        server.start();
//
//        // Create a Client to communicate with the server
//        Client client = new Client();
//        client.start();

        // Initialise openGL states
        glfwShowWindow(gameWindow);
        glfwMakeContextCurrent(gameWindow);
        GL.createCapabilities(false);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        aspectRatio = (float) width / (float) height;

        Camera camera = new Camera(25 * aspectRatio, 25f);
        TrackSet trackSet = new TrackSet(Model.generateSquare(), camera);
        trackSet.setTrack(new Track(10, 10, 10));

        gameInput = new GameInput();
        glfwSetKeyCallback(gameWindow, gameInput);

        double previousTime = System.nanoTime();
        double frametime = 0;
        int frames = 0;

        Background background = new Background();

        RWDCar car = new DordConcentrate(0.0, 0.0, 1.25f, 1, new Vector3f(1f, 0.7f, 0.8f));
        PhysicsEngine.addCar(car);

        Renderer renderer = new Renderer(camera);
        renderer.addRenderable(background);
        renderer.addRenderable(trackSet);
        renderer.addRenderable(car);
//        renderer.addRenderable(carSet);

        GLFWWindowSizeCallback resize = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                glViewport(0, 0, width, height);
                aspectRatio = (float) width / (float) height;
                camera.setProjection(25 * aspectRatio, 25f);
            }
        };
        glfwSetWindowSizeCallback(gameWindow, resize);

//        Box box = new Box(1f, 1f, -1.5f, -1f, new Vector4f(0f, 0f, 1f, 1.0f), AssetManager.loadTexture("/tracks/background_1.png"));
        Label label = new Label("POSITION", 0.1f, -1.5f, -1f);
        Scene scene = new Scene();
        scene.addElement(label);

        Button button = new Button(1f, 0.25f, -0.7f, 0f, new Vector4f(1, 1, 1, 1), new Vector4f(0, 0, 0, 1), "BUTTON", 0.05f, cursor);
//        Box box = new Box(1f, 1f, -0.7f, 0.5f, new Vector4f(1, 1, 1, 1));
        scene.addElement(button);
        //        cursor.disable();

        int i = 0;
        int j = 0;
        int thing = 0;
        // Game loop for now
        while (!glfwWindowShouldClose(gameWindow)) {
            glfwPollEvents();
//            cursor.update();
            button.update();
//            System.out.println("X: " + cursor.getX() + " Y: " + cursor.getY());

            double currentTime = System.nanoTime();
            double interval = currentTime - previousTime;
            frametime += interval;
            frames += 1;
            previousTime = currentTime;

            PhysicsEngine.crank();

            glClearColor(0.0f, .6f, 0.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);


//            System.out.println("X: " + car.getXInPixels() + "Y: " + car.getYInPixels());
            camera.setPosition(car.getXf(), car.getYf(), 0);
            background.setX(car.getXf() / 10f);
            background.setY(car.getYf() / 10f);
//            trackSet.render();
//            carSet.render();
            renderer.render();
            scene.render();

            glfwSwapBuffers(gameWindow);

            if (frametime >= 1000000000) {
                frametime = 0;
                System.out.println("FPS: " + frames);
                frames = 0;
            }
        }

        // Close both server and client threads
//        server.close();
//        client.close();
    }

    public static void main(String[] args) {
//        SharedLibraryLoader.load();
        new Main();

    }
}
