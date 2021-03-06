package com.battlezone.megamachines.renderer.ui.elements;


import com.battlezone.megamachines.events.mouse.MouseButtonEvent;
import com.battlezone.megamachines.input.Cursor;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.ui.Interactive;
import com.battlezone.megamachines.sound.SoundEvent;
import com.battlezone.megamachines.sound.SoundFiles;

/**
 * A button which responds to mouse clicks and runs an action if active at the time of the click and the cursor is over the button
 * Also changes colour if the button is highlighted
 */
public class Button extends Box implements Interactive, KeyboardNavigable {

    private final static Runnable DISABLED = () -> {
    };
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
    private Label label;
    private boolean hovered;
    private boolean enabled;
    // If a button is managed that means it does not need to manage the cursor itself
    private boolean managed;
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

    /**
     * Set the text on this label in the default colour (black)
     *
     * @param text The new text
     */
    public void setText(String text) {
        this.label.delete();
        this.label = new Label(text, labelHeight, leftX + ((rightX - leftX) - Label.getWidth(text, labelHeight)) / 2f, bottomY + padding);
    }

    /**
     * Set the text on this label in a particular colour
     *
     * @param text   The text
     * @param colour The colour
     */
    public void setText(String text, Vector4f colour) {
        this.label.delete();
        this.label = new Label(text, labelHeight, leftX + ((rightX - leftX) - Label.getWidth(text, labelHeight)) / 2f, bottomY + padding, colour);
    }

    @Override
    public void update() {
        //If the button is managed then it doesnt need to handle clicks itself
        if (managed) return;
        //Otherwise we need to update the hovered state
        if (this.cursor.getX() > this.leftX && this.cursor.getX() < this.rightX
                && this.cursor.getY() > bottomY && this.cursor.getY() < topY) {
            if (!hovered) {
                setHovered(true);
            }
        } else if (hovered) {
            setHovered(false);
        }
    }

    /**
     * Set the action to be performed if the button is clicked while enabled
     *
     * @param r A runnable to run
     */
    public void setAction(Runnable r) {
        this.action = r;
    }

    @EventListener
    public void mouseClick(MouseButtonEvent e) {
        //If, at the time of click, the button is hovered over, then run the action
        if (enabled && hovered && e.getAction() == MouseButtonEvent.PRESSED) {
            //Play a click sound
            MessageBus.fire(new SoundEvent(SoundFiles.CLICK, SoundEvent.PLAY_ONCE, 1));
            action.run();
        }
    }

    @Override
    public void hide() {
        this.hovered = false;
        this.enabled = false;
        //When hiding we should also reset the colour
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

    private void setHovered(boolean hovered) {
        this.hovered = hovered;
        setColour(hovered ? secondaryColour : primaryColour);
    }

    public void disable() {
        this.action = DISABLED;
    }

    @Override
    public void setManaged(boolean managed) {
        this.managed = managed;
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

    @Override
    public void focusChanged(boolean active) {
        setHovered(active);
    }

    @Override
    public void runAction() {
        if (enabled) {
            MessageBus.fire(new SoundEvent(SoundFiles.CLICK, SoundEvent.PLAY_ONCE, 1));
            action.run();
        }
    }

}
