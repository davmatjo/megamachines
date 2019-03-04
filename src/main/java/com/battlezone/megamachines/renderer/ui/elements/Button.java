package com.battlezone.megamachines.renderer.ui.elements;


import com.battlezone.megamachines.events.mouse.MouseButtonEvent;
import com.battlezone.megamachines.input.Cursor;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.ui.Interactive;

public class Button extends Box implements Interactive {

    private final static Runnable DISABLED = () -> {
    };
    private Label label;
    private final Texture texture;
    private final Vector4f primaryColour;
    private final Vector4f secondaryColour;
    private final Cursor cursor;
    private final float leftX;
    private final float bottomY;
    private final float rightX;
    private final float topY;
    private final float labelHeight;
    private final float padding;
    private boolean hovered;
    private boolean enabled;
    private Runnable action;

    public Button(float width, float height, float x, float y, Vector4f primaryColour, Vector4f secondaryColour, String label, float padding) {
        super(width, height, x, y, primaryColour);
        MessageBus.register(this);
        this.enabled = true;
        this.padding = padding;
        this.labelHeight = height - (padding * 2);
        this.texture = Texture.BLANK;
        this.primaryColour = primaryColour;
        this.secondaryColour = secondaryColour;
        this.cursor = Cursor.getCursor();
        this.leftX = x;
        this.bottomY = y;
        this.rightX = x + width;
        this.topY = y + height;
        this.label = new Label("", 0, 0, 0);
        setText(label);
    }

    public Button(float width, float height, float x, float y, Vector4f primaryColour, Vector4f secondaryColour, Texture texture, String label, float padding) {
        super(width, height, x, y, primaryColour, texture);
        MessageBus.register(this);
        this.enabled = true;
        this.padding = padding;
        this.labelHeight = height - (padding * 2);
        this.texture = texture;
        this.primaryColour = primaryColour;
        this.secondaryColour = secondaryColour;
        this.cursor = Cursor.getCursor();
        this.leftX = x;
        this.bottomY = y;
        this.rightX = x + width;
        this.topY = y + height;
        this.label = new Label("", 0, 0, 0);
        setText(label);
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

    public void setText(String text) {
        this.label.delete();
        this.label = new Label(text, labelHeight, leftX + ((rightX - leftX) - Label.getWidth(text, labelHeight)) / 2f, bottomY + padding);
    }

    public void setText(String text, Vector4f colour) {
        this.label.delete();
        this.label = new Label(text, labelHeight, leftX + ((rightX - leftX) - Label.getWidth(text, labelHeight)) / 2f, bottomY + padding, colour);
    }

    @Override
    public void update() {
        if (this.cursor.getX() > this.leftX && this.cursor.getX() < this.rightX
                && this.cursor.getY() > bottomY && this.cursor.getY() < topY) {
            if (!hovered) {
                setColour(secondaryColour);
                hovered = true;
            }
        } else if (hovered) {
            hovered = false;
            setColour(primaryColour);
        }
    }

    public void setAction(Runnable r) {
        this.action = r;
    }

    @EventListener
    public void mouseClick(MouseButtonEvent e) {
        if (enabled && hovered && e.getAction() == MouseButtonEvent.PRESSED) {
            action.run();
        }
    }

    @Override
    public void hide() {
        this.hovered = false;
        this.enabled = false;
        setColour(primaryColour);
    }

    @Override
    public void show() {
        this.hovered = false;
        this.enabled = true;
    }

    boolean isHovered() {
        return hovered;
    }

    public void disable() {
        this.action = DISABLED;
    }
}