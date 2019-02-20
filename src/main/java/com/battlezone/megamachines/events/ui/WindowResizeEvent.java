package com.battlezone.megamachines.events.ui;

public class WindowResizeEvent {

    final float aspectRatio;

    public WindowResizeEvent(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

}
