package com.battlezone.megamachines.renderer.game.animation;

import com.battlezone.megamachines.entities.RWDCar;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LandAnimationTest {

    @Test
    public void sizeOfTargetIsBeingDecreased() {
        RWDCar car = mock(RWDCar.class);
        LandAnimation animation = new LandAnimation(car);
        when(car.getScale()).thenReturn(200f);
        animation.play(() -> {});
        animation.tryUpdate(0.1);
        verify(car).setScale(floatThat(f -> f < 200f));
        for (int i=0; i<25; i++) {
            animation.tryUpdate(0.1);
        }
        assertFalse(animation.isRunning());
    }
}