package com.battlezone.megamachines.renderer.ui.elements;

import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.Renderable;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.util.AssetManager;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Label implements Renderable {

    private static final Texture FONT = AssetManager.loadTexture("/ui/font.png");
    private final Vector4f colour;
    private final List<Renderable> renderableCharacters = new ArrayList<>();
    private final float offset;
    private final float height;
    private String text;
    private float x;
    private float y;

    public Label(String text, float height, float x, float y) {
        this.height = height;
        this.offset = height / 20f;
        this.x = x;
        this.y = y;
        this.colour = Colour.BLACK;
        setText(text);
    }

    public Label(String text, float height, float x, float y, Vector4f colour) {
        this.height = height;
        this.offset = height / 20f;
        this.x = x;
        this.y = y;
        this.colour = colour;
        setText(text);
    }

    public static float getWidth(String text, float height) {
        return text.length() * (height / 20f + height);
    }

    /**
     * Change position of the label
     *
     * @param x New X of left
     */
    public void setX(float x) {
        this.x = x;
        setText(text);
    }

    /**
     * Change position of the label
     *
     * @param y New Y of bottom
     */
    public void setY(float y) {
        this.y = y;
        setText(text);
    }

    /**
     * Set the position of the label
     *
     * @param x Left
     * @param y Bottom
     */
    public void setPos(float x, float y) {
        this.x = x;
        this.y = y;
        setText(text);
    }

    @Override
    public void render() {
        FONT.bind();
        for (int i = 0; i < renderableCharacters.size(); i++)
            renderableCharacters.get(i).render();
    }

    @Override
    public Shader getShader() {
        return Shader.STATIC;
    }

    public float getWidth() {
        return text.length() * (height / 20f + height);
    }

    public float getHeight() {
        return height;
    }

    /**
     * Sets the text on the label
     *
     * @param text The text to set
     */
    public void setText(String text) {
        this.text = text;
        for (int i = 0; i < renderableCharacters.size(); i++)
            renderableCharacters.get(i).delete();
        renderableCharacters.clear();
        byte[] characters = text.getBytes(StandardCharsets.US_ASCII);
        for (int i = 0; i < characters.length; i++) {
            renderableCharacters.add(new Box(
                    height,
                    height,
                    x + i * height + offset * i,
                    y,
                    colour,
                    AssetManager.getChar((char) characters[i])));
        }
    }

    @Override
    public void delete() {
        for (int i = 0; i < renderableCharacters.size(); i++)
            renderableCharacters.get(i).delete();
    }
}
