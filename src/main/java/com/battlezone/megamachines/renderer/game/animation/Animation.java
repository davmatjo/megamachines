package com.battlezone.megamachines.renderer.game.animation;

public abstract class Animation {

    protected final double duration;
    protected double elapsed = 0;
    protected boolean running = false;

    public Animation(double dur) {
        this.duration = dur;
    }

    public void play() {
        running = true;
    }

    void tryUpdate(double interval) {
        if (running) {
            System.out.println("running");
            elapsed += interval;
            update(interval);
            if (elapsed > duration) {
                elapsed = 0;
                running = false;
                finish();
            }
        }
    }

    abstract void update(double interval);

    abstract void finish();

}
