package com.battlezone.megamachines.renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    private Vector3f position;
    private Matrix4f projection;

    public Camera(int width, int height) {
        position = new Vector3f(0, 0, 0);
        setProjection(width, height);
    }

    public void setProjection(int width, int height) {
        projection = new Matrix4f().ortho2D(-width >> 1, width >> 1, -height >> 1, height >> 1);
    }

    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
    }

    public void translate(float x, float y, float z) {
        this.position.add(x, y, z);
    }

    public Matrix4f getProjection() {
        return projection.translate(position, new Matrix4f());
    }
}
