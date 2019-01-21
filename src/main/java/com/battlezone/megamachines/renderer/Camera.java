package com.battlezone.megamachines.renderer;

import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.math.Matrix4f;

/**
 * The camera class provides the projection matrix for the world from it's current position
 *
 * @author David
 */
public class Camera {

    private Vector3f position;
    private Matrix4f projection;

    public Camera(int width, int height) {
        position = new Vector3f(0, 0, 0);
        setProjection(width, height);
    }

    private void setProjection(int width, int height) {
        projection = Matrix4f.orthographic(-width >> 1, width >> 1, -height >> 1, height >> 1);
    }

    public void setPosition(float x, float y, float z) {
        this.position.set(-x, -y, -z);
    }

    public void translate(float x, float y, float z) {
        this.position.add(x, y, z);
    }

    public Matrix4f getProjection() {
        return projection.translate(position);
    }
}
