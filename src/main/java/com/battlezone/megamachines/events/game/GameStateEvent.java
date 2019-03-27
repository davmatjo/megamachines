package com.battlezone.megamachines.events.game;

public class GameStateEvent {

    private GameState newState;

    /**
     * A GameStateEvent informs the system of a change in the state of the game
     *
     * @param newState The new state of the game
     */
    public GameStateEvent(GameState newState) {
        this.newState = newState;
    }

    public GameState getNewState() {
        return newState;
    }

    public enum GameState {
        PLAYING, PAUSED, MENU
    }

}
