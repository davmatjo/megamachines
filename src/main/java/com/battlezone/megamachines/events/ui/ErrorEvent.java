package com.battlezone.megamachines.events.ui;

import com.battlezone.megamachines.world.BaseWorld;

public class ErrorEvent {

    private final String title;
    private final String message;
    private final int displayTime;
    private int framesDisplayed;

    /**
     * Create an error event which will display an error to the user on the screen
     * @param title First line of the error message
     * @param message Second line of the error messsage
     * @param displayTime Number of seconds to display the message for
     */
    public ErrorEvent(String title, String message, int displayTime) {
        this.title = title;
        this.message = message;
        this.displayTime = displayTime;
        this.framesDisplayed = 0;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public void increment() {
        framesDisplayed++;
    }

    public boolean isExpired() {
        return framesDisplayed / BaseWorld.TARGET_FPS > displayTime;
    }
}
