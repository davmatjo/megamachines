package com.battlezone.megamachines;

import com.battlezone.megamachines.input.GameInput;
import com.battlezone.megamachines.input.KeyCode;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.game.*;
import com.battlezone.megamachines.renderer.ui.Box;
import com.battlezone.megamachines.renderer.ui.FontConvertor;
import com.battlezone.megamachines.renderer.ui.Scene;
import com.battlezone.megamachines.world.Track;
import com.battlezone.megamachines.entities.Cars.DordConcentrate;
import com.battlezone.megamachines.entities.RWDCar;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import com.battlezone.megamachines.physics.PhysicsEngine;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;

public class Main {

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

        glfwSwapInterval(1);

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
        trackSet.setTrack(Track.generateMap(10, 10, 10));

        gameInput = new GameInput();
        glfwSetKeyCallback(gameWindow, gameInput);

        double previousTime = System.nanoTime();
        double frametime = 0;
        int frames = 0;

        RWDCar car = new DordConcentrate(0.0, 0.0, 1.25f, 1, new Vector3f(1f, 0.7f, 0.8f));
        PhysicsEngine.addCar(car);

        Renderer renderer = new Renderer(camera);
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

        Box box = new Box(1f, 1f, -1.5f, -1f, new Vector4f(0f, 0f, 1f, 1.0f));
        Scene scene = new Scene();
        scene.addElement(box);

        int i = 0;
        int j = 0;
        int thing = 0;
        // Game loop for now
        while (!glfwWindowShouldClose(gameWindow)) {
            glfwPollEvents();

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
    }

    public static void main(String[] args) {
//        SharedLibraryLoader.load();
        new Main();

    }
}
