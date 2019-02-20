package com.battlezone.megamachines.events.game;

import com.battlezone.megamachines.math.Vector2f;

public class CollisionEvent {

    private Vector2f collisionCoordinates;
    private double force;

    public CollisionEvent(Vector2f collisionCoordinates, double force) {
        this.collisionCoordinates = collisionCoordinates;
        this.force = force;
    }

    public Vector2f getCollisionCoordinates() {
        return collisionCoordinates;
    }

    public double getForce() {
        return force;
    }

}
