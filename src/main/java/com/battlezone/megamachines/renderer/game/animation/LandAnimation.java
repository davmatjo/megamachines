package com.battlezone.megamachines.renderer.game.animation;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.math.MathUtils;

public class LandAnimation extends Animation {
    private static final double DURATION = 0.6;
    private static final double DURATION_STAGE_1 = DURATION / 2;
    private static final double DURATION_STAGE_2 = DURATION / 4;
    private static final double DURATION_STAGE_3 = DURATION / 4;
    private static final double TIME_STAGE_2 = DURATION_STAGE_1;
    private static final double TIME_STAGE_3 = DURATION_STAGE_1 + DURATION_STAGE_2;
    private static final float INTERMEDIATE_SCALE = 1.1f;
    private static final float STARTING_SCALE = 2.5f;
    private final RWDCar obj;
    private float scalePerSecDown1;
    private float scalePerSecDown2;
    private float scalePerSecUp;
    private float initialScale;

    /**
     * Creates a land animation
     * @param object Car that this animation will affect
     */
    public LandAnimation(RWDCar object) {
        super(DURATION);
        obj = object;
        initialScale = object.getScale();
        scalePerSecDown1 = MathUtils.lerpVelocity(STARTING_SCALE, initialScale, (float) (1 / DURATION_STAGE_1));
        scalePerSecUp = MathUtils.lerpVelocity(initialScale, initialScale * INTERMEDIATE_SCALE, (float) (1 / DURATION_STAGE_2));
        scalePerSecDown2 = MathUtils.lerpVelocity(initialScale, INTERMEDIATE_SCALE, (float) (1 / DURATION_STAGE_3));
    }

    /**
     * Sets up this animation
     */
    @Override
    void play() {
        initialScale = obj.isEnlargedByPowerup <= 0 ? 1.25f : 3f;
        scalePerSecDown1 = MathUtils.lerpVelocity(initialScale * STARTING_SCALE, initialScale, (float) (1 / DURATION_STAGE_1));
        scalePerSecUp = MathUtils.lerpVelocity(initialScale, INTERMEDIATE_SCALE, (float) (1 / DURATION_STAGE_2));
        scalePerSecDown2 = MathUtils.lerpVelocity(initialScale, INTERMEDIATE_SCALE, (float) (1 / DURATION_STAGE_3));
    }

    /**
     * Run the next frame of this animation
     * @param interval Time since the last call of the animation
     */
    @Override
    void update(double interval) {
        if (elapsed < TIME_STAGE_2) {
            obj.setScale(initialScale * STARTING_SCALE + (float) (elapsed * scalePerSecDown1));
        } else if (elapsed < TIME_STAGE_3) {
            obj.setScale(obj.getScale() + MathUtils.lerpVelocity(obj.getScale(), initialScale * INTERMEDIATE_SCALE, 0.1f));
        } else {
            obj.setScale(obj.getScale() + MathUtils.lerpVelocity(obj.getScale(), initialScale, 0.1f));
        }
    }

    /**
     * Clean up this animation when finished
     */
    @Override
    void finish() {
        if (obj.isEnlargedByPowerup <= 0) {
            initialScale = 1.25f;
        }
        obj.setScale(initialScale);
    }
}
