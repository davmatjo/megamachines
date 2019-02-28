package com.battlezone.megamachines.physics;

import com.battlezone.megamachines.entities.Cars.AffordThoroughbred;
import com.battlezone.megamachines.entities.abstractCarComponents.CarBody;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.util.Pair;
import com.battlezone.megamachines.world.ScaleController;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.List;

/**
 * The tests for car body
 */
public class CarBodyTests {
    @Test
    public void correctWeight() {
        PhysicsEngine pe = new PhysicsEngine();
        AffordThoroughbred at = new AffordThoroughbred(0, 0, ScaleController.RWDCAR_SCALE, 1, new Vector3f(0,0,0), 0, 0);
        pe.addCar(at);
        assertEquals(800.0, at.getCarBody().getWeight());
    }
}
