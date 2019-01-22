package com.battlezone.megamachines;

import com.battlezone.megamachines.input.GameInput;
import com.battlezone.megamachines.input.KeyCode;
import com.battlezone.megamachines.renderer.game.*;
import com.battlezone.megamachines.world.Track;
import entities.Cars.DordConcentrate;
import entities.RWDCar;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import physics.PhysicsEngine;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;

public class Main {

    private Main() {
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

//        glfwSwapInterval(1);

        // Initialise openGL states
        glfwShowWindow(gameWindow);
        glfwMakeContextCurrent(gameWindow);
        GL.createCapabilities(false);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        Camera camera = new Camera((int) (1000 * ((float) width / (float) height)), 1000);
        TrackSet trackSet = new TrackSet(Model.generateSquare(), camera);
        trackSet.setTrack(Track.generateMap(10, 10, 400));

        GameInput gameInput = new GameInput();
        glfwSetKeyCallback(gameWindow, gameInput);

        double previousTime = System.nanoTime();
        double frametime = 0;
        int frames = 0;

        RWDCar car = new DordConcentrate(0.0, 0.0, 50f, 0);
        List<RWDCar> cars = new ArrayList<>();
        cars.add(car);
        PhysicsEngine.addCar(car);

        CarSet carSet = new CarSet(Model.generateCar(), cars, camera);

        Renderer renderer = new Renderer(camera);
        renderer.addRenderable(trackSet);
        renderer.addRenderable(carSet);

        GLFWWindowSizeCallback resize = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                glViewport(0, 0, width, height);
                camera.setProjection((int) (1000 * ((float) width / (float) height)), 1000);
            }
        };
        glfwSetWindowSizeCallback(gameWindow, resize);

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

            if (gameInput.isPressed(KeyCode.D))
                i += 10;
            if (gameInput.isPressed(KeyCode.A))
                i -= 10;
            if (gameInput.isPressed(KeyCode.W))
                j += 10;
            if (gameInput.isPressed(KeyCode.S))
                j -= 10;

            PhysicsEngine.crank();

            glClearColor(0.0f, .6f, 0.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);


//            System.out.println("X: " + car.getXInPixels() + "Y: " + car.getYInPixels());
            camera.setPosition(car.getXf(), car.getYf(), 0);
//            trackSet.render();
//            carSet.render();
            renderer.render();

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
