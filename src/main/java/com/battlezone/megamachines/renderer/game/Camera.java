package com.battlezone.megamachines.renderer.game;

import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.math.Matrix4f;

/**
 * The camera class provides the projection matrix for the world from it's current position
 *
 * @author David
 */
public class Camera {

    /**
     * Current position of the camera
     */
    private Vector3f position;

    /**
     * Projection matrix for this camera
     */
    private Matrix4f projection = new Matrix4f();

    /**
     * Temporary matrix that allows calculations without new matrix objects
     */
    private Matrix4f tempMatrix = new Matrix4f();

    /**
     * Creates a new camera for viewing the world
     * @param width width of the projection
     * @param height height of the projection
     */
    public Camera(float width, float height) {
        position = new Vector3f(0, 0, 0);
        setProjection(width, height);
    }

    /**
     * Changes the projection
     * @param width width of the projection
     * @param height height of the projection
     */
    public void setProjection(float width, float height) {
        Matrix4f.orthographic(-width / 2, width / 2, -height / 2, height / 2, projection);
    }

    /**
     * Sets the position of the camera
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     */
    public void setPosition(float x, float y, float z) {
        this.position.set(-x, -y, -z);
    }

    /**
     * Moves the camera by the specified coordinates
     * @param x x coordinate to add
     * @param y y coordinate to add
     * @param z z coordinate to add
     */
    public void translate(float x, float y, float z) {
        this.position.add(x, y, z);
    }

    /**
     * @return The projection matrix translated by the position of the camera
     */
    public Matrix4f getProjection() {
        return Matrix4f.translate(projection, position, tempMatrix);
    }
}
