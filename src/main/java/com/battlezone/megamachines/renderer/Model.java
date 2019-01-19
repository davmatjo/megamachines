package com.battlezone.megamachines.renderer;

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
                        1, -1, 0,  // 2 - Bottom Left
                        -1, -1, 0,  // 3 - Bottom Right
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

    float[] getVertices() {
        return vertices;
    }

    int[] getIndices() {
        return indices;
    }

    float[] getTextureCoordinates() {
        return textureCoordinates;
    }
}
