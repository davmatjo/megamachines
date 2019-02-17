package com.battlezone.megamachines.physics;

import com.battlezone.megamachines.util.Pair;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class CollisionsTest {

    @Test
    public void collided() {
//        assertTrue(Collisions.collided(
//                List.of(new Pair<>(-0.5, 1.0), new Pair<>(0.5, 1.0), new Pair<>(0.5, -1.0), new Pair<>(-0.5, -1.0)),
//                List.of(new Pair<>(1.0, 0.5), new Pair<>(1.0, -0.5), new Pair<>(-1.0, -0.5), new Pair<>(-1.0, 0.5))
//        ));
        System.out.println(Collisions.objectsCollided(
                List.of(List.of(new Pair<>(-0.5, 1.0), new Pair<>(0.5, 1.0), new Pair<>(0.5, -1.0), new Pair<>(-0.5, -1.0))),
                List.of(List.of(new Pair<>(0.0, 0.5), new Pair<>(0.0, -0.5), new Pair<>(-1.0, -0.5), new Pair<>(-1.0, 0.5))), 0.0
        ));

    }
}