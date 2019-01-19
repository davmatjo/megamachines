package com.battlezone.megamachines.renderer;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

public class Sprite {

    private Shader shader;

    private int indexBufferID;
    private int textureBufferID;
    private int vertexBufferID;
    private int indexCount;

    public Sprite(Model model, Shader shader) {
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

    /**
     * Binds the relevant buffers and draws the object
     */
    private void draw() {

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferID);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, textureBufferID);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferID);
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
    }

    /**
     * Binds the current shader, and uses the given texture to draw the sprite
     *
     * @param texture texture to map onto object
     */
    public void render(Texture texture) {
        shader.use();
        shader.setInt("sampler", 0);
        texture.bind();
        draw();
    }
}
