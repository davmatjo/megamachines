package com.battlezone.megamachines.renderer.game;

/**
 * Provides models of game objects
 */
public class Model {

    private float[] vertices;
    private int[] indices;
    private float[] textureCoordinates;

    public Model(float[] vertices, int[] indices, float[] textureCoordinates) {
        this.vertices = vertices;
        this.indices = indices;
        this.textureCoordinates = textureCoordinates;
    }

    /**
     * A square model with texture vertices ready
     *
     * @return square model
     */
    public static Model generateSquare() {
        return new Model(
                new float[]{
                        -1, 1, 0,  // 0 - Top Left
                        1, 1, 0,  // 1 - Top Right
                        1, -1, 0,  // 2 - Bottom Right
                        -1, -1, 0,  // 3 - Bottom Left
                },
                new int[]{
                        0, 1, 2,
                        2, 3, 0,
                },
                new float[]{
                        0, 0,
                        1, 0,
                        1, 1,
                        0, 1,
                }
        );
    }

    @Deprecated
    public static Model generateCube() {
        return new Model(
                new float[]{
                        // front
                        -1.0f, -1.0f,  -0.5f,
                        1.0f, -1.0f,  -0.5f,
                        1.0f,  1.0f,  -0.5f,
                        -1.0f,  1.0f,  -0.5f,
                        // back
                        -1.0f, -1.0f, -1f,
                        1.0f, -1.0f, -1f,
                        1.0f,  1.0f, -1f,
                        -1.0f,  1.0f, -1f
                },
                new int[] {
                        // front
                        0, 1, 2,
                        2, 3, 0,
                        // right
                        1, 5, 6,
                        6, 2, 1,
                        // back
                        7, 6, 5,
                        5, 4, 7,
                        // left
                        4, 0, 3,
                        3, 7, 4,
                        // bottom
                        4, 5, 1,
                        1, 0, 4,
                        // top
                        3, 2, 6,
                        6, 7, 3
                },
                new float[] {
//                        0, 0,
//                        1, 0,
//                        1, 1,
//                        0, 1,
//                        0, 0,
//                        1, 0,
//                        1, 1,
//                        0, 1,
//                        0, 0,
//                        1, 0,
//                        1, 1,
//                        0, 1,
                }
        );
    }

    /**
     * Model of an ingame car
     * @return model for the ingame car
     */
    public static Model generateCar() {
        return new Model(
                new float[]{
                        -1, 0.5f, 0,  // 0 - Top Left
                        1, 0.5f, 0,  // 1 - Top Right
                        1, -0.5f, 0,  // 2 - Bottom Right
                        -1, -0.5f, 0,  // 3 - Bottom Left
                },
                new int[]{
                        0, 1, 2,
                        2, 3, 0,
                },
                new float[]{
                        0, 0,
                        1, 0,
                        1, 1,
                        0, 1,
                }
        );
    }

    /**
     * @return The vertices of this model
     */
    float[] getVertices() {
        return vertices;
    }

    /**
     * @return The indices of this model
     */
    public int[] getIndices() {
        return indices;
    }

    /**
     * @return The texture coordinates (relative to the texture used) of this model
     */
    float[] getTextureCoordinates() {
        return textureCoordinates;
    }
}
