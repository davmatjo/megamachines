package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.input.Cursor;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.game.Shader;
import com.battlezone.megamachines.renderer.game.Texture;

public class Button extends Box implements Interactive {

    private final Label label;
    private final Texture texture;
    private final Vector4f primaryColour;
    private final Vector4f secondaryColour;
    private final Cursor cursor;
    private final float leftX;
    private final float bottomY;
    private final float rightX;
    private final float topY;
    private boolean active;

    public Button(float width, float height, float x, float y, Vector4f primaryColour, Vector4f secondaryColour, String label, float padding, Cursor cursor) {
        super(width, height, x, y, primaryColour);
        this.label = new Label(label, height - (padding * 2), x + padding, y - padding);
        this.texture = Texture.BLANK;
        this.primaryColour = primaryColour;
        this.secondaryColour = secondaryColour;
        this.cursor = cursor;
        this.leftX = x;
        this.bottomY = y;
        this.rightX = x + width;
        this.topY = y + height;
    }

    public Button(float width, float height, float x, float y, Vector4f primaryColour, Vector4f secondaryColour, Texture texture, String label, float padding, Cursor cursor) {
        super(width, height, x, y, primaryColour, texture);
        this.label = new Label(label, height - (padding * 2), x + padding, y - padding);
        this.texture = texture;
        this.primaryColour = primaryColour;
        this.secondaryColour = secondaryColour;
        this.cursor = cursor;
        this.leftX = x;
        this.bottomY = y;
        this.rightX = x + width;
        this.topY = y + height;
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
        if (this.cursor.getX() > this.leftX && this.cursor.getX() < this.rightX
            && this.cursor.getY() > bottomY && this.cursor.getY() < topY) {
            if (!active) {
                setColour(secondaryColour);
                active = true;
                System.out.println("active");
            }
        } else if (active) {
            active = false;
            System.out.println("inactive");
            setColour(primaryColour);
        }
    }

}
