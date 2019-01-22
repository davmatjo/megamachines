package com.battlezone.megamachines.renderer.game;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

public abstract class AbstractRenderable implements Renderable {

    private Shader shader;

    private int indexBufferID;
    private int textureBufferID;
    private int vertexBufferID;
    private int indexCount;

    AbstractRenderable(Model model, Shader shader) {
        this.shader = shader;

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

    private FloatBuffer createBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private IntBuffer createBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private void start() {
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferID);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, textureBufferID);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferID);
    }

    public abstract void draw();

    private void stop() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
    }

    void delete() {
        glDeleteBuffers(vertexBufferID);
        glDeleteBuffers(textureBufferID);
        glDeleteBuffers(indexBufferID);
    }

    Shader getShader() {
        return shader;
    }

    int getIndexCount() {
        return indexCount;
    }

    public void render() {
        start();
        draw();
        stop();
    }

}
