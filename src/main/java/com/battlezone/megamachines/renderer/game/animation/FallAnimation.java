package com.battlezone.megamachines.renderer.game.animation;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.math.MathUtils;

public class FallAnimation extends Animation {

    private static final double DURATION_STAGE_1 = 0.1,
            DURATION_STAGE_2 = 1.6,
            DURATION_STAGE_3 = 0.5,
            DURATION = DURATION_STAGE_2 + DURATION_STAGE_3;
    private static final float TARGET_SCALE = 0.4f;
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
    }

    @Override
    void update(double interval) {
        if (elapsed > DURATION_STAGE_1) {
            target.setDepth(-1);
            target.setSpeed(target.getSpeed() * 0.95);
        }
        if (elapsed < DURATION_STAGE_2) {
            float change = (float) (elapsed * scalePerSec);
            target.setScale(initialScale + change);
            target.setSpeed(target.getSpeed() * 0.95);
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
        target.setDepth(target.isEnlargedByPowerup > 0 ? 1 : 0);
    }
}