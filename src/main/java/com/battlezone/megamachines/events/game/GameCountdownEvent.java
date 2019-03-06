package com.battlezone.megamachines.events.game;

public class GameCountdownEvent {

    private final int count;

    /**
     * Creates a GameCountdownEvent
     * @param count The current count
     */
    public GameCountdownEvent(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
