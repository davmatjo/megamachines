package com.battlezone.megamachines.events.game;

public class GameCountdownEvent {

    private final int count;

    public GameCountdownEvent(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
