package com.battlezone.megamachines.renderer.ui.elements;

import com.battlezone.megamachines.input.Cursor;
import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.renderer.ui.Interactive;

public class ImageButton extends Button implements Interactive {

    private final Cursor cursor;
    private final float leftX;
    private final float bottomY;
    private final float rightX;
    private final float topY;
    private final float labelHeight;
    private Label label;

    private Box image;

    private float fullWidth, height, x, y;

    private String title;

    private Texture texture;

    private float padding;

    /*going down the box we have
        padding
        image
        padding
        label
        padding
    */

    public ImageButton(float width, float height, float x, float y, String label, Texture texture) {
        super(width, height, x, y, Colour.WHITE, Colour.BLUE, "", 0);

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
        refreshImage();
    }

    @Override
    public void draw() {
        super.draw();
        Shader.STATIC.setMatrix4f("texturePosition", Matrix4f.IDENTITY);
        image.render();
        Shader.STATIC.setMatrix4f("texturePosition", Matrix4f.IDENTITY);
        label.render();
    }

    @Override
    public Shader getShader() {
        return Shader.STATIC;
    }

    @Override
    public void setText(String text) {
        this.title = text;
        refreshText();
    }

    @Override
    public void setTexture(Texture texture) {
        this.texture = texture;
        refreshImage();
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

    private void refreshImage() {
        System.out.println("X IS " + x + " PADDING IS " + padding + " WIDTH IS " + fullWidth);
        image = new Box(fullWidth - padding * 2, height - labelHeight - padding * 3, x + padding, y + labelHeight + padding * 2, Colour.WHITE, texture);
    }

}
