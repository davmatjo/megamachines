package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.game.Renderable;
import com.battlezone.megamachines.renderer.game.Shader;
import com.battlezone.megamachines.renderer.game.SubTexture;
import com.battlezone.megamachines.renderer.game.Texture;
import com.battlezone.megamachines.util.AssetManager;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author David, Kieran
 */
public class Label implements Renderable {

    private static final Texture font = AssetManager.loadTexture("/ui/font.png");
    private static final int CHARACTER_COUNT = 29;
    private final List<Box> renderableCharacters = new ArrayList<>();
    private final float offset;
    private String text;
    private Matrix4f scaleResult = new Matrix4f();
    private Matrix4f translateResult = new Matrix4f();

    public Label(String string, float height, float x, float y) {
        byte[] characters = string.getBytes(StandardCharsets.US_ASCII);
        offset = height / 20f;
        for (int i = 0; i < characters.length; i++) {
            System.out.println("mine: " + characters[i]);
            System.out.println("mine: " + (float) (characters[i] - 65));
            renderableCharacters.add(new Box(
                    height,
                    height,
                    x + i * height + offset * i,
                    y,
                    new Vector4f(1, 1, 1, 0.5f),
                    new SubTexture(Matrix4f.scale(1f / CHARACTER_COUNT, 1, 1, scaleResult).multiply(Matrix4f.translate((float) (characters[i] - 65), 0f, 0f, translateResult)))));
            //new SubTexture(new Matrix4f(1f / CHARACTER_COUNT, 0, 0, 0, 0, 1f, 0, 0, 0, 0, 1f, 0, 0, 0, 0, 1f).translate((float) (characters[i] - 65), 0f, 0))));
        }
    }

    @Override
    public void render() {
        font.bind();
        for (var character : renderableCharacters) {
            character.render();
        }
    }

    @Override
    public Shader getShader() {
        return Shader.STATIC;
    }

//    public void editText(int position, )
}
