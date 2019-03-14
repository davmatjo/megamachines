package com.battlezone.megamachines.entities;

import com.battlezone.megamachines.ai.Driver;
import com.battlezone.megamachines.entities.powerups.Powerup;
import com.battlezone.megamachines.events.keys.KeyEvent;
import com.battlezone.megamachines.input.KeyCode;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.world.MultiplayerWorld;

public interface Controllable {
    /**
     * Sets the acceleration amount of this car
     *
     * @param accelerationAmount The acceleration amount of this car
     */
    public void setAccelerationAmount(double accelerationAmount);

    /**
     * Sets the brake amount of this car
     *
     * @param brakeAmount The brake amount of this car
     */
    public void setBrakeAmount(double brakeAmount);

    /**
     * Gets this Controllable's turn amount
     *
     * @return This Controllable's turn amount
     */
    public double getTurnAmount();

    /**
     * Sets the turn amount of this car
     *
     * @param turnAmount The turn amount of this car
     */
    public void setTurnAmount(double turnAmount);

    /**
     * Gets the current powerup
     *
     * @return The current powerup
     */
    public Powerup getCurrentPowerup();

    /**
     * Sets the current powerup of the car
     *
     * @param currentPowerup The current powerup
     */
    public void setCurrentPowerup(Powerup currentPowerup);

    /**
     * Gets the driver of this car
     *
     * @return The driver of this car
     */
    public Driver getDriver();

    /**
     * Sets the driver of this car
     *
     * @param driver The driver of this car
     */
    public void setDriver(Driver driver);

    /**
     * Returns true if the controls are active, false otherwise
     *
     * @return true if the controls are active, fales otherwise
     */
    public boolean isControlsActive();

    /**
     * Sets controlsActive
     *
     * @param controlsActive True if the controls should be active, false otherwise
     */
    public void setControlsActive(boolean controlsActive);

    @EventListener
    public default void setDriverPressRelease(KeyEvent event) {
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
    public default void setDriverPress(int keyCode) {
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
    public default void setDriverRelease(int keyCode) {
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
