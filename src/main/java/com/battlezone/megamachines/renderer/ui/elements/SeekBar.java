package com.battlezone.megamachines.renderer.ui.elements;

import com.battlezone.megamachines.events.mouse.MouseButtonEvent;
import com.battlezone.megamachines.input.Cursor;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.game.DrawableRenderer;
import com.battlezone.megamachines.renderer.ui.Interactive;

public class SeekBar extends Box implements Interactive, KeyboardNavigable {

    private final Vector4f secondaryColour;
    private final Cursor cursor;
    private final float leftX;
    private final float bottomY;
    private final float rightX;
    private final float topY;
    private final float labelHeight;
    private final float padding;
    private Label label;
    private boolean held;
    private boolean active;
    private boolean managed;
    //Called when the value changes
    private Runnable onValueChanged;

    //This box draws the bar which represents the value chosen
    private Box bar;

    //The selected value
    private float value;
    //The size, position of the seekbar
    private float fullWidth, height, x, y;

    //The title of the seekbar
    private String title;

    public SeekBar(float width, float height, float x, float y, Vector4f primaryColour, Vector4f secondaryColour, String label, float value, float padding) {
        super(width, height, x, y, primaryColour);
        MessageBus.register(this);
        this.padding = padding;
        this.labelHeight = height - (padding * 2);
        this.secondaryColour = secondaryColour;
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
        this.value = value;
        refreshText();

        bar = new Box(width * value, height, x, y, secondaryColour);
    }

    @Override
    public void draw() {
        super.draw();
        (new DrawableRenderer(bar)).render();
        label.render();
    }

    @Override
    public Shader getShader() {
        return Shader.STATIC;
    }

    /**
     * Reloads the label to show the selected value
     */
    private void refreshText() {
        var text = title + " " + Math.round(value * 100);
        this.label = new Label(text, labelHeight, leftX + ((rightX - leftX) - Label.getWidth(text, labelHeight)) / 2f, bottomY + padding);
    }

    @Override
    public void update() {
        if (managed) return;
        if (this.cursor.getX() > this.leftX && this.cursor.getX() < this.rightX && this.cursor.getY() > bottomY && this.cursor.getY() < topY) {
            if (!active) {
                active = true;
            }
            if (held) {
                //if the seekbar is active and held then the value should be moved to the new value
                double offset = cursor.getX() - leftX;
                double frac = offset / fullWidth;
                //we take the fraction of the seekbar to the left of the click
                this.value = (float) frac;
                refreshText();

                bar.delete();
                //We make a new bar showing this value
                bar = new Box(this.value * fullWidth, height, x, y, secondaryColour);
                onValueChanged.run();
            }
        } else if (active) {
            active = false;
        }
    }

    @Override
    public void hide() {

    }

    @Override
    public void show() {

    }

    public void setOnValueChanged(Runnable r) {
        this.onValueChanged = r;
    }

    public float getValue() {
        return value;
    }

    @EventListener
    public void mouseClick(MouseButtonEvent e) {
        if (active && e.getAction() == MouseButtonEvent.PRESSED) {
            this.held = true;
        }
        if (e.getAction() == MouseButtonEvent.RELEASED) {
            this.held = false;
        }
    }

    @Override
    public void focusChanged(boolean active) {
        if (!active) {
            this.active = false;
        }
    }

    @Override
    public void setManaged(boolean managed) {
        this.managed = managed;
    }

    @Override
    public void runAction() {
        this.active = true;
    }

    @Override
    public float getTopY() {
        return topY;
    }

    @Override
    public float getBottomY() {
        return bottomY;
    }

    @Override
    public float getLeftX() {
        return leftX;
    }

    @Override
    public float getRightX() {
        return rightX;
    }

}
