package com.battlezone.megamachines.ai;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.powerups.Powerup;
import com.battlezone.megamachines.entities.powerups.types.Bomb;
import com.battlezone.megamachines.networking.server.game.GameRoom;
import com.battlezone.megamachines.renderer.Window;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.world.Race;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.generator.TrackSquareLoop;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.mockito.Mockito.*;

public class DriverTest {

    @BeforeClass
    public static void setup() {
//        if (!glfwInit()) {
//            AssetManager.setIsHeadless(true);
//        } else {
//            AssetManager.setIsHeadless(false);
//            Window.getWindow();
//        }
    }

    @Test
    public void update() {
        // Create objects and mock objects for testing
        Track track = new TrackSquareLoop(10, 10, true).generateTrack();
        RWDCar car = mock(RWDCar.class);
        Race race = mock(Race.class);
        Driver testDriver = new Driver(track, car, race);

        // Set the car to be "0, 0"
        when(car.getX()).thenReturn(0.0);
        when(car.getY()).thenReturn(0.0);

        testDriver.update(0);

        // Verify that the car wanted to accelerate and it didn't try to turn
        verify(car).setAccelerationAmount(anyDouble());
        verify(car).setTurnAmount(doubleThat(x -> x == 0.0));
        clearInvocations(car);

        when(car.getX()).thenReturn(10.0);
        when(car.getAngle()).thenReturn(90.0);

        testDriver.update(0);

        // Verify that the car tries to turn left here
        verify(car).setAccelerationAmount(anyDouble());
        verify(car).setTurnAmount(doubleThat(x -> x > 0.0));

        clearInvocations(car);
        when(car.getAngle()).thenReturn(-90.0);

        testDriver.update(0);

        // Verify that the car tries to turn right here
        verify(car).setAccelerationAmount(anyDouble());
        verify(car).setTurnAmount(doubleThat(x -> x < 0.0));

        clearInvocations(car);
        when(car.getSpeed()).thenReturn(1000.0);
        testDriver.update(0);
        verify(car).setBrakeAmount(anyDouble());

        clearInvocations(car);

        // Verify that the car tries to slow down when going too fast
        when(car.getY()).thenReturn(100.0);
        when(car.getAngle()).thenReturn(1440.0);

        testDriver.update(0);

        // Verify that the car tries to turn right here when in a difficult configuration
        verify(car).setAccelerationAmount(anyDouble());
        verify(car).setTurnAmount(doubleThat(x -> x < 0.0));
    }

    @Test
    public void driverPowerupTest() {
        // Create objects and mock objects for testing
        Track track = new TrackSquareLoop(10, 10, true).generateTrack();
        RWDCar car = mock(RWDCar.class);
        Race race = mock(Race.class);
        Driver testDriver = new Driver(track, car, race);

        // Powerup to test
        Powerup powerup = mock(Bomb.class);

        when(car.getCurrentPowerup()).thenReturn(powerup);

        // Interval should be 0
        testDriver.update(0);

        verify(powerup, times(0)).activate();

        // Pass 5 seconds
        testDriver.update(5d);

        // Powerup should have been activated
        verify(powerup).activate();
    }

    @Test
    public void driverPowerupMultiplayerTest() {
        // Create objects and mock objects for testing
        Track track = new TrackSquareLoop(10, 10, true).generateTrack();
        RWDCar car = mock(RWDCar.class);
        Race race = mock(Race.class);
        Driver testDriver = new Driver(track, car, race);

        // Powerup to test
        Powerup powerup = mock(Bomb.class);

        // Multiplayer to test ??? !!! stupid
        GameRoom multiplayer = mock(GameRoom.class);

        // Inject multiplayer.dll
        testDriver.setGameroom(multiplayer);

        when(car.getCurrentPowerup()).thenReturn(powerup);

        // Interval should be 0
        testDriver.update(0);

        verify(powerup, times(0)).activate();

        // Pass 5 seconds
        testDriver.update(5d);

        // Powerup should have been activated
        verify(powerup).activate();

        // Multiplayer's send powerup should be triggered
        verify(multiplayer).sendPowerup(car);
    }
}