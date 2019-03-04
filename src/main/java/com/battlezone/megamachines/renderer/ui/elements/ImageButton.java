package com.battlezone.megamachines.renderer.ui.elements;

import com.battlezone.megamachines.input.Cursor;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.game.DrawableRenderer;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.renderer.ui.Interactive;

public class ImageButton extends Button implements Interactive {

    private Label label;
    private final Cursor cursor;
    private final float leftX;
    private final float bottomY;
    private final float rightX;
    private final float topY;
    private final float labelHeight;
    private boolean active;

    private Box image;

    private float fullWidth, height, x, y;

    private String title;

    private Texture texture;

    private float padding;

    /*going down the box we have

        padding
        imagw
        padding
        label
        padding
    */

    public ImageButton(float width, float height, float x, float y, String label, Texture texture) {
        super(width, height, x, y, Colour.WHITE, Colour.BLUE, "", 0);
        MessageBus.register(this);
        this.labelHeight = height * 0.1f;
        this.padding = labelHeight / 2;
        this.cursor = Cursor.getCursor();
        this.leftX = x;
        this.bottomY = y;
        this.rightX = x + width;
        this.topY = y + height;

        this.fullWidth = width;
        this.height = height;
        this.x = x;
        this.y = y;

        this.title = label;
        this.texture = texture;
        refreshText();

        image = new Box(width - padding * 2, height - labelHeight - padding * 3, x + padding, y + labelHeight + padding * 2, Colour.WHITE, texture);
    }

    @Override
    public void draw() {
        super.draw();
        (new DrawableRenderer(image)).render();
        label.render();
    }

    @Override
    public Shader getShader() {
        return Shader.STATIC;
    }

    private void refreshText() {
        // we need to add as much padding as is needed to stop the label going off the button
        var actualHeight = labelHeight;
        var width = Label.getWidth(title, actualHeight);
        while (width > fullWidth) {
            actualHeight -= labelHeight * 0.05;
            width = Label.getWidth(title, actualHeight);
        }
        this.label = new Label(title, actualHeight, (rightX + leftX - width) / 2f, bottomY + padding + (labelHeight - actualHeight) / 2);
    }

}
