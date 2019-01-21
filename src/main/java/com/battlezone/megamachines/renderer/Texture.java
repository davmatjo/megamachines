package com.battlezone.megamachines.renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL30.*;

public class Texture {

    private final int glObjectID;

    /**
     * Loads a texture given by an array of pixel values into an openGL texture object
     *
     * @param pixelValues   pixel values in argb
     * @param textureWidth  width of the texture
     * @param textureHeight height of the texture
     */
    public Texture(int[] pixelValues, int textureWidth, int textureHeight) {

        ByteBuffer pixels = BufferUtils.createByteBuffer(textureWidth * textureHeight * 4);

        // Place ARGB pixels into ByteBuffer as bytes in the form RGBA
        for (int pixel : pixelValues) {
            pixels.put((byte) ((pixel >> 16) & 0xff));  // Red Byte
            pixels.put((byte) ((pixel >> 8) & 0xff));   // Green
            pixels.put((byte) ((pixel) & 0xff));        // Blue
            pixels.put((byte) ((pixel >> 24) & 0xff));  // Alpha
        }

        pixels.flip();


        glObjectID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, glObjectID);

        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, textureWidth, textureHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
    }

    /**
     * Binds the texture to the current openGL state, ready for drawing
     */
    public void bind() {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, glObjectID);
    }

}
