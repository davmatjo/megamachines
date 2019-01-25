package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.game.AbstractRenderable;
import com.battlezone.megamachines.renderer.game.Model;
import com.battlezone.megamachines.renderer.game.Shader;
import com.battlezone.megamachines.renderer.game.Texture;

public class Button extends Box implements Interactive {

    private Label label;

    public Button(float width, float height, float x, float y, Vector4f colour, String label, float padding) {
        super(width, height, x, y, colour);
        this.label = new Label(label, height - (padding * 2), x + padding, y - padding);
    }

    public Button(float width, float height, float x, float y, Vector4f colour, Texture texture, String label, float padding) {
        super(width, height, x, y, colour, texture);
        this.label = new Label(label, height - (padding * 2), x + padding, y - padding);
    }

    @Override
    public void draw() {
        super.draw();
        label.render();
    }

    @Override
    public Shader getShader() {
        return Shader.STATIC;
    }

    @Override
    public void update() {

    }

}
