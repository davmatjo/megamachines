package com.battlezone.megamachines.entities;

import com.battlezone.megamachines.ai.Driver;
import com.battlezone.megamachines.entities.powerups.Powerup;
import com.battlezone.megamachines.events.keys.KeyEvent;
import com.battlezone.megamachines.input.KeyCode;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.world.MultiplayerWorld;

public interface Controllable {
    /**
     * Gets the acceleration amount of this car
     *
     * @return This car's acceleration amount
     */
    double getAccelerationAmount();

    /**
     * Sets the acceleration amount of this car
     *
     * @param accelerationAmount The acceleration amount of this car
     */
    void setAccelerationAmount(double accelerationAmount);


    /**
     * Sets the brake amount of this car
     *
     * @return this car's brake amount
     */
    double getBrakeAmount();

    /**
     * Sets the brake amount of this car
     *
     * @param brakeAmount The brake amount of this car
     */
    void setBrakeAmount(double brakeAmount);

    /**
     * Gets this Controllable's turn amount
     *
     * @return This Controllable's turn amount
     */
    double getTurnAmount();

    /**
     * Sets the turn amount of this car
     *
     * @param turnAmount The turn amount of this car
     */
    void setTurnAmount(double turnAmount);

    /**
     * Gets the current powerup
     *
     * @return The current powerup
     */
    Powerup getCurrentPowerup();

    /**
     * Sets the current powerup of the car
     *
     * @param currentPowerup The current powerup
     */
    void setCurrentPowerup(Powerup currentPowerup);

    /**
     * Gets the driver of this car
     *
     * @return The driver of this car
     */
    Driver getDriver();

    /**
     * Sets the driver of this car
     *
     * @param driver The driver of this car
     */
    void setDriver(Driver driver);

    /**
     * Returns true if the controls are active, false otherwise
     *
     * @return true if the controls are active, fales otherwise
     */
    boolean isControlsActive();

    /**
     * Sets controlsActive
     *
     * @param controlsActive True if the controls should be active, false otherwise
     */
    void setControlsActive(boolean controlsActive);

    /**
     * Returns true if the game is currently paused, false otherwise
     *
     * @return true if the game is currently paused, false otherwise
     */
    boolean isPaused();

    /**
     * Sets isPaused
     *
     * @param paused True if the game is paused, false otherwise
     */
    void setPaused(boolean paused);

    @EventListener
    default void setDriverPressRelease(KeyEvent event) {
        if (getDriver() == null) {
            if (event.getPressed())
                setDriverPress(event.getKeyCode());
            else
                setDriverRelease(event.getKeyCode());
        }
    }

    /**
     * Reacts to a key being pressed
     *
     * @param keyCode The code of the key being pressed
     */
    default void setDriverPress(int keyCode) {
        if (keyCode == KeyCode.W) {
            setAccelerationAmount(1.0);
        } else if (keyCode == KeyCode.S) {
            setBrakeAmount(1.0);
        } else if (keyCode == KeyCode.A) {
            setTurnAmount(1.0);
        } else if (keyCode == KeyCode.D) {
            setTurnAmount(-1.0);
        } else if (keyCode == KeyCode.SPACE) {
            System.out.println("Attempt activated");
            if (getCurrentPowerup() != null && !MultiplayerWorld.isActive()) {
                System.out.println("activated");
                getCurrentPowerup().activate();
            } else if (getCurrentPowerup() == null) {
                System.err.println("Powerup was null");
            } else {
                System.err.println("Multiplayer world was active");
            }
        }
    }

    /**
     * Reacts to a key getting released
     *
     * @param keyCode The key code of the key getting released
     */
    default void setDriverRelease(int keyCode) {
        if (keyCode == KeyCode.W) {
            //TODO: make this a linear movement
            setAccelerationAmount(0.0);
        } else if (keyCode == KeyCode.S) {
            setBrakeAmount(0.0);
        } else if (keyCode == KeyCode.A) {
            if (getTurnAmount() == 1.0) {
                setTurnAmount(0.0);
            }
        } else if (keyCode == KeyCode.D) {
            if (getTurnAmount() == -1.0) {
                setTurnAmount(0.0);
            }
        }
    }
}
