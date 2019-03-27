package com.battlezone.megamachines.renderer.game.animation;

import java.util.Map;

public abstract class Animation {

    /**
     * Index for translating animations to network events.
     * <p>
     * IMPORTANT - Index MUST be power of 2
     */
    public static final Map<Byte, Class> INDEX_TO_ANIM = Map.of(
            (byte) 0x1, FallAnimation.class,
            (byte) 0x10, LandAnimation.class);
    public static final Map<Class, Byte> ANIM_TO_INDEX = Map.of(
            FallAnimation.class, (byte) 0x1,
            LandAnimation.class, (byte) 0x10);

    protected final double duration;
    protected double elapsed = 0;
    protected boolean running = false;
    private Runnable onFinished;

    public Animation(double dur) {
        this.duration = dur;
    }

    abstract void play();

    public void play(Runnable onFinished) {
        running = true;
        this.onFinished = onFinished;
        play();
    }

    void tryUpdate(double interval) {
        if (running) {
            elapsed += interval;
            update(interval);
            if (elapsed > duration) {
                elapsed = 0;
                running = false;
                finish();
                if (onFinished != null) {
                    onFinished.run();
                    onFinished = null;
                }
            }
        }
    }

    abstract void update(double interval);

    abstract void finish();

    public boolean isRunning() {
        return running;
    }

}
