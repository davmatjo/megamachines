package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.Renderable;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.util.AssetManager;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Label implements Renderable {

    private static final Texture FONT = AssetManager.loadTexture("/ui/font.png");
    private static final Vector4f COLOUR = Colour.BLACK;
    private String text;
    private final List<Renderable> renderableCharacters = new ArrayList<>();
    private final float offset;
    private final float height;
    private final float x;
    private final float y;

    public Label(String text, float height, float x, float y) {
        this.height = height;
        this.offset = height / 20f;
        this.x = x;
        this.y = y;
        setText(text);
    }

    @Override
    public void render() {
        FONT.bind();
        for (var character : renderableCharacters) {
            character.render();
        }
    }

    @Override
    public Shader getShader() {
        return Shader.STATIC;
    }

    public static float getWidth(String text, float height) {
        return text.length() * (height / 20f + height);
    }

    public void setText(String text) {
        renderableCharacters.forEach(Renderable::delete);
        renderableCharacters.clear();
        byte[] characters = text.getBytes(StandardCharsets.US_ASCII);
        for (int i = 0; i < characters.length; i++) {
            renderableCharacters.add(new Box(
                    height,
                    height,
                    x + i * height + offset * i,
                    y,
                    COLOUR,
                    AssetManager.getChar((char) characters[i])));
        }
    }

    @Override
    public void delete() {
        renderableCharacters.forEach(Renderable::delete);
    }
}
