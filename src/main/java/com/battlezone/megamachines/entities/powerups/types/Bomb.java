package com.battlezone.megamachines.entities.powerups.types;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.powerups.Powerup;
import com.battlezone.megamachines.entities.powerups.PowerupManager;
import com.battlezone.megamachines.entities.powerups.types.physical.BombDrop;
import com.battlezone.megamachines.physics.PhysicsEngine;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.game.Renderer;
import com.battlezone.megamachines.util.AssetManager;

/**
 * When activated, this powerup will place a bomb at the current position of the car.
 * Once placed, the bomb will explode in 3 seconds.
 * When the bomb explodes, it will shoot a number of projectiles in all directions
 * Projectiles will have mass, so when they hit a car, they will push it according to our collision calculations
 */
public class Bomb extends Powerup {
    public static final byte id = 2;
    private BombDrop bd;
    private boolean started = false;
    private static final Texture texture = AssetManager.loadTexture("/powerups/bomb_1.png");

    public Bomb(PowerupManager manager, PhysicsEngine pe, Renderer renderer) {
        super(3, manager, pe, renderer);
    }

    @Override
    public Texture getTexture() {
        return texture;
    }

    @Override
    protected void powerupPickup() {

    }

    @Override
    protected void powerupActivate() {
        bd = new BombDrop(holder.getX() - ((holder.getScale() + 1.1)) * Math.cos(Math.toRadians(holder.getRotation())), holder.getY() - ((holder.getScale() + 1.1)) * Math.sin(Math.toRadians(holder.getRotation())), physicsEngine, renderer);
    }

    @Override
    protected void powerupUpdate(double interval) {
        if (started) {
            elapsed += interval;
        }
    }

    @Override
    protected void powerupEnd() {
        for (int i = 0; i < physicsEngine.getAllCars().size(); i++) {
            final RWDCar car = physicsEngine.getAllCars().get(i);
            final double diffX = car.getX() - bd.getX(),
                    diffY = car.getY() - bd.getY(),
                    distance = Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2)),
                    angle = Math.atan2(diffY, diffX);
            car.addForce(2000 / (distance * distance), Math.toDegrees(angle), 100);
        }
        renderer.removeDrawable(bd);
    }

    @Override
    public byte getID() {
        return id;
    }
}