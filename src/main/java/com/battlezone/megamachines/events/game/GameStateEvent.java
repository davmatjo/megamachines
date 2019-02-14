package com.battlezone.megamachines.events.game;

public class GameStateEvent {

    public enum GameState {
        PLAYING, PAUSED, MENU
    }

    private GameState newState;

    public GameStateEvent(GameState newState) {
        this.newState = newState;
    }

    public GameState getNewState() {
        return newState;
    }

}
