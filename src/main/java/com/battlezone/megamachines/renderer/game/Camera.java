package com.battlezone.megamachines.renderer.game;

import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.math.Vector3f;

/**
 * The camera class provides the projection matrix for the world from it's current position
 *
 * @author David, Kieran
 */
public class Camera {

    private Vector3f position;
    private Matrix4f projection = new Matrix4f();
    private Matrix4f tempMatrix = new Matrix4f();

    public Camera(float width, float height) {
        position = new Vector3f(0, 0, 0);
        setProjection(width, height);
    }

    public void setProjection(float width, float height) {
        projection = Matrix4f.orthographic(-width / 2, width / 2, -height / 2, height / 2, projection);
    }

    public void setPosition(float x, float y, float z) {
        this.position.set(-x, -y, -z);
    }

    public void translate(float x, float y, float z) {
        this.position.add(x, y, z);
    }

    public Matrix4f getProjection() {
        return projection.multiply(Matrix4f.translate(position, tempMatrix), tempMatrix);
    }
}
