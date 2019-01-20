package com.battlezone.megamachines.world;

import org.joml.Vector2f;

public class GameObject {

    private Vector2f position;
    private float scale;

    public GameObject(Vector2f position, float scale) {
        this.position = position;
        this.scale = scale;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public void setPosition(float x, float y) {
        this.position.x = x;
        this.position.y = y;
    }

    public void translate(float x, float y) {
        this.position.x += x;
        this.position.y += y;
    }

    public Vector2f getPosition() {
        return position;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

}
