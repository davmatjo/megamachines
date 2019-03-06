package com.battlezone.megamachines.events.ui;

public class WindowResizeEvent {

    final float aspectRatio;

    /**
     * Creates a WindowResizeEvent
     * @param aspectRatio The new aspect
     */
    public WindowResizeEvent(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

}
