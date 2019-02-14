package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.events.mouse.MouseButtonEvent;
import com.battlezone.megamachines.input.Cursor;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.game.DrawableRenderer;

public class SeekBar extends Box implements Interactive {

    private Label label;
    private final Vector4f secondaryColour;
    private final Cursor cursor;
    private final float leftX;
    private final float bottomY;
    private final float rightX;
    private final float topY;
    private final float labelHeight;
    private final float padding;
    private boolean held;
    private boolean active;
    private Runnable onValueChanged;

    private Box bar;

    private float value = 1.0f;
    private float fullWidth, height, x, y;

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

    private void refreshText() {
        var text = title + " " + Math.round(value * 100);
        this.label = new Label(text, labelHeight, leftX + ((rightX - leftX) - Label.getWidth(text, labelHeight)) / 2f, bottomY + padding);
    }

    @Override
    public void update() {
        if (this.cursor.getX() > this.leftX && this.cursor.getX() < this.rightX && this.cursor.getY() > bottomY && this.cursor.getY() < topY) {
            if (!active) {
                active = true;
            }
            if (held) {
                double offset = cursor.getX() - leftX;
                double frac = offset / fullWidth;
                this.value = (float) frac;
                refreshText();

                bar.delete();
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
        } if (e.getAction() == MouseButtonEvent.RELEASED) {
            this.held = false;
        }
    }

}
