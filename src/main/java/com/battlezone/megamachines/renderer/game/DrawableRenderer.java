package com.battlezone.megamachines.renderer.game;

import com.battlezone.megamachines.renderer.Drawable;
import com.battlezone.megamachines.renderer.Renderable;
import com.battlezone.megamachines.renderer.Shader;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;

public class DrawableRenderer implements Renderable {
    /**
     * ID of the index buffer object on the GPU
     */
    private int indexBufferID;

    /**
     * ID of the texture buffer object on the GPU
     */
    private int textureBufferID;

    /**
     * ID of the vertex buffer object on the GPU
     */
    private int vertexBufferID;

    /**
     * Number of indices for drawing
     */
    private int indexCount;

    private final Drawable drawable;

    /**
     * Generates all the buffers on the GPU
     * @param drawable drawable to wrap this object around
     */
    public DrawableRenderer(Drawable drawable) {

        this.drawable = drawable;
        indexCount = drawable.getModel().getIndices().length;

        vertexBufferID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferID);
        glBufferData(GL_ARRAY_BUFFER, createBuffer(drawable.getModel().getVertices()), GL_STATIC_DRAW);

        textureBufferID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, textureBufferID);
        glBufferData(GL_ARRAY_BUFFER, createBuffer(drawable.getModel().getTextureCoordinates()), GL_STATIC_DRAW);

        indexBufferID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, createBuffer(drawable.getModel().getIndices()), GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    /**
     * Creates a FloatBuffer from a float array
     * @param data data used to create buffer
     * @return float buffer filled with floats
     */
    private FloatBuffer createBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    /**
     * Creates a Int from an int array
     * @param data data used to create buffer
     * @return int buffer filled with ints
     */
    private IntBuffer createBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    /**
     * Bind vertices, texture coordinates and indices
     */
    private void start() {
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferID);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, textureBufferID);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferID);
    }

    /**
     * Draw code specific to object that extends this
     */
    public void draw() {
        drawable.draw();
    }

    /**
     * Unbind buffers *SHOULD BE REMOVED ON RELEASE AS IT IS UNNECESSARY*
     */
    private void stop() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
    }

    /**
     * Delete all components of this object from the GPU
     */
    public void delete() {
        glDeleteBuffers(vertexBufferID);
        glDeleteBuffers(textureBufferID);
        glDeleteBuffers(indexBufferID);
    }

    /**
     * Get the shader used by this object so objects can be sorted by shader
     * @return Shader this object uses
     */
    public Shader getShader() {
        return drawable.getShader();
    }

    /**
     * Binds, draws, unbinds
     */
    public void render() {
        start();
        draw();
        stop();
    }
}