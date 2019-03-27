package com.battlezone.megamachines.renderer.game;

import com.battlezone.megamachines.renderer.Model;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

public class ModelBinding {
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

    /**
     * Generates all the buffers on the GPU
     *
     * @param model model to bind
     */
    public ModelBinding(Model model) {

        indexCount = model.getIndices().length;

        vertexBufferID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferID);
        glBufferData(GL_ARRAY_BUFFER, createBuffer(model.getVertices()), GL_STATIC_DRAW);

        textureBufferID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, textureBufferID);
        glBufferData(GL_ARRAY_BUFFER, createBuffer(model.getTextureCoordinates()), GL_STATIC_DRAW);

        indexBufferID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, createBuffer(model.getIndices()), GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    /**
     * Creates a FloatBuffer from a float array
     *
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
     *
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
    public void bind() {
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferID);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, textureBufferID);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferID);
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

}