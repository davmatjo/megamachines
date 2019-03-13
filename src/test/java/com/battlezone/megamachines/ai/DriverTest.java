package com.battlezone.megamachines.ai;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.world.Race;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.generator.TrackSquareLoop;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class DriverTest {

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
}