package com.battlezone.megamachines.renderer.game.animation;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.math.MathUtils;

public class FallAnimation extends Animation {

    private static final double DURATION_STAGE_1 = 0.25,
            DURATION_STAGE_2 = 1.0,
            DURATION_STAGE_3 = 0.5,
            DURATION = DURATION_STAGE_2 + DURATION_STAGE_3;
    private static final float TARGET_SCALE = 0.6f;
    private final float scalePerSec;
    private final RWDCar target;
    private float initialScale;
    private boolean firstCall = true;

    public FallAnimation(RWDCar car) {
        super(DURATION);
        target = car;
        initialScale = car.getScale();
        scalePerSec = MathUtils.lerpVelocity(initialScale, TARGET_SCALE, (float) (1 / DURATION_STAGE_2));
    }

    @Override
    void play() {
        initialScale = target.getScale();
//        target.setDepth(-1);
    }

    @Override
    void update(double interval) {
        if (elapsed > DURATION_STAGE_1) {
            target.setDepth(-1);
        }
        if (elapsed < DURATION_STAGE_2) {
            target.setScale(initialScale + (float) (elapsed * scalePerSec));
        } else {
            if (firstCall) {
                firstCall = false;
                target.playCloud();
            }
            target.setSpeed(0);
            target.setScale(0.01f);
        }
    }

    @Override
    void finish() {
        firstCall = true;
        target.setScale(initialScale);
        target.setDepth(0);
    }
}