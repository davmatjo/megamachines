package com.battlezone.megamachines.world;

import org.joml.Vector2f;

public class GameObject {

    private Vector2f position;

    public GameObject(Vector2f position) {
        this.position = position;
    }

    public Vector2f getPosition() {
        return position;
    }
}
