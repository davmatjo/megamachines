package com.battlezone.megamachines.renderer.game.animation;

import com.battlezone.megamachines.entities.RWDCar;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FallAnimationTest {

    @Test
    public void animationStartsAndEnds() {
        RWDCar car = mock(RWDCar.class);
        FallAnimation fallAnimation = new FallAnimation(car);
        assertFalse(fallAnimation.isRunning());
        fallAnimation.play(() -> {});
        assertTrue(fallAnimation.isRunning());
        for (int i=0; i<5; i++) {
            fallAnimation.tryUpdate(0.5d);
        }
        assertFalse(fallAnimation.isRunning());
    }

    @Test
    public void animationWorksAsExpectedWhenTargetIsEnlarged() {
        RWDCar car = mock(RWDCar.class);
        when(car.isEnlargedByPowerup()).thenReturn(true);
        FallAnimation fallAnimation = new FallAnimation(car);
        assertFalse(fallAnimation.isRunning());
        fallAnimation.play(() -> {});
        assertTrue(fallAnimation.isRunning());
        for (int i=0; i<5; i++) {
            fallAnimation.tryUpdate(0.5d);
        }
        assertFalse(fallAnimation.isRunning());
    }

}