package com.battlezone.megamachines.renderer.game.animation;

public abstract class Animation {

    protected final double duration;
    protected double elapsed = 0;
    protected boolean running = false;
    private Runnable onFinished;

    public Animation(double dur) {
        this.duration = dur;
    }

    public void play() {
        running = true;
    }

    public void play(Runnable onFinished) {
        running = true;
        this.onFinished = onFinished;
    }

    void tryUpdate(double interval) {
        if (running) {
            System.out.println("running");
            elapsed += interval;
            update(interval);
            if (elapsed > duration) {
                elapsed = 0;
                running = false;
                if (onFinished != null) {
                    onFinished.run();
                    onFinished = null;
                }
                finish();
            }
        }
    }

    abstract void update(double interval);

    abstract void finish();

    public boolean isRunning() {
        return running;
    }

}
