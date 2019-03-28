package com.battlezone.megamachines;

import com.battlezone.megamachines.renderer.Window;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.world.SingleplayerWorld;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.generator.TrackCircleLoop;
import com.battlezone.megamachines.world.track.generator.TrackGenerator;
import com.battlezone.megamachines.world.track.generator.TrackLoopMutation;
import com.battlezone.megamachines.world.track.generator.TrackSquareLoop;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

public class StabilityTest {

    private static final List<Class<? extends TrackGenerator>> tracks = List.of(TrackLoopMutation.class, TrackSquareLoop.class, TrackCircleLoop.class);

    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        AssetManager.setIsHeadless(false);
        Random r = new Random();
        while (!glfwWindowShouldClose(Window.getWindow().getGameWindow())) {
            Class<? extends TrackGenerator> generator = tracks.get(r.nextInt(tracks.size()));
            Track track;
            if (generator.equals(TrackLoopMutation.class)) {
                track = generator.getDeclaredConstructor(int.class, int.class).newInstance(20, 20).generateTrack();
            } else {
                track = generator.getDeclaredConstructor(int.class, int.class, boolean.class).newInstance(20, 20, true).generateTrack();
            }
            new SingleplayerWorld(
                    new ArrayList<>(),
                    track,
                    0, 8, 3).start(false);
        }
    }
}
