package com.battlezone.megamachines;

import com.battlezone.megamachines.ai.Driver;
import com.battlezone.megamachines.ai.TrackRoute;
import com.battlezone.megamachines.entities.Cars.DordConcentrate;
import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.input.Cursor;
import com.battlezone.megamachines.input.GameInput;
import com.battlezone.megamachines.input.Gamepad;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.physics.PhysicsEngine;
import com.battlezone.megamachines.renderer.Model;
import com.battlezone.megamachines.renderer.game.Background;
import com.battlezone.megamachines.renderer.game.Camera;
import com.battlezone.megamachines.renderer.game.Renderer;
import com.battlezone.megamachines.renderer.game.TrackSet;
import com.battlezone.megamachines.renderer.ui.*;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.world.Race;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.generator.TrackGenerator;
import com.battlezone.megamachines.world.track.generator.TrackLoopMutation;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;

public class Main {

    private static final double FRAME_CAP = (1000000000.0 / 60.0);
    public static float aspectRatio;
    public static GameInput gameInput;
    public static Gamepad gamepad;

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

//        Cursor cursor = new Cursor(gameWindow, width, height);

        glfwSwapInterval(1);

//        gamepad = new Gamepad();
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
        TrackSet trackSet = new TrackSet();
        TrackGenerator generator = new TrackLoopMutation(10, 10);
//        TrackGenerator generator = new TrackCircleLoop(30, 30, false);
//        TrackGenerator generator = new TrackSquareLoop(10, 10, 10, true);
        Track track = generator.generateTrack();
        trackSet.setTrack(track);

//        gameInput = new GameInput();
//        glfwSetKeyCallback(gameWindow, gameInput);

        double previousTime = System.nanoTime();
        double frametime = 0;
        int frames = 0;

        Background background = new Background();

        float x = track.getStartPiece().getXf();
        float y = track.getStartPiece().getYf();

        RWDCar car = new DordConcentrate(x, y, 1.25f, 1, new Vector3f(1f, 0.7f, 0.8f));
        RWDCar car2 = new DordConcentrate(x + 1.5f, y, 1.25f, 3, new Vector3f(0f, 1f, 0f));
        PhysicsEngine.addCar(car);
        PhysicsEngine.addCar(car2);

        Renderer renderer = new Renderer(camera);
        renderer.addRenderable(background);
        renderer.addRenderable(trackSet);
        renderer.addRenderable(car);
        renderer.addRenderable(car2);
//        renderer.addRenderable(carSet);
//
//        GLFWWindowSizeCallback resize = new GLFWWindowSizeCallback() {
//            @Override
//            public void invoke(long window, int width, int height) {
//                glViewport(0, 0, width, height);
//                aspectRatio = (float) width / (float) height;
//                camera.setProjection(25 * aspectRatio, 25f);
//            }
//        };
//        glfwSetWindowSizeCallback(gameWindow, resize);

//        Box box = new Box(1f, 1f, -1.5f, -1f, new Vector4f(0f, 0f, 1f, 1.0f), AssetManager.loadTexture("/tracks/background_1.png"));

        List<RWDCar> cars = List.of(car, car2);
        Scene scene = new Scene();
        Minimap minimap = new Minimap(track, cars);
        scene.addElement(minimap);
//        Label label = new Label("POSITION", 0.1f, -1.5f, -1f);
//        scene.addElement(label);

//        Race race = new Race(track, 2, cars);

//        track.getEdges().forEach(PhysicsEngine::addCollidable);

//        Button button = new Button(1f, 0.25f, -0.7f, 0f, new Vector4f(1, 1, 1, 1), new Vector4f(0, 0, 0, 1), "BUTTON", 0.05f, cursor);
//        Box box = new Box(1f, 1f, -0.7f, 0.5f, new Vector4f(1, 1, 1, 1));
//        scene.addElement(button);
        //        cursor.disable();


//        Race race = new Race(track, 10, cars);

//        Driver driver = new Driver(new TrackRoute(track), car2);
//        Menu menu = new Menu(cursor, () -> {
//        }, () -> {
//        });

        int i = 0;
        int j = 0;
        int thing = 0;
        // Game loop for now
        while (!glfwWindowShouldClose(gameWindow)) {
            glfwPollEvents();
//            cursor.update();
//            button.update();
//            System.out.println("X: " + cursor.getX() + " Y: " + cursor.getY());
//            driver.update();
//            race.update();

            double currentTime = System.nanoTime();
            double interval = currentTime - previousTime;
            frametime += interval;
            frames += 1;
            previousTime = currentTime;

            PhysicsEngine.crank(interval);
//            race.update();

            glClearColor(0.0f, .6f, 0.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);


//            System.out.println("X: " + car.getX() + "Y: " + car.getY());
            camera.setPosition(car.getXf(), car.getYf(), 0);
            background.setX(car.getXf() / 10f);
            background.setY(car.getYf() / 10f);
//            trackSet.render();
//            carSet.render();
            renderer.render();
            scene.render();
//            menu.render();

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
        AssetManager.setIsHeadless(false);
        new Main();
    }
}
