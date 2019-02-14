package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.events.keys.KeyEvent;
import com.battlezone.megamachines.input.KeyCode;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.renderer.Texture;

public class TextInput extends Box implements Interactive {

    private final float padding;
    private final float textHeight;
    private final float x;
    private final float y;
    private Label textLabel;
    private String textValue;
    private boolean enabled;

    public TextInput(float width, float height, float x, float y, float padding, Vector4f colour) {
        super(width, height, x, y, colour);
        MessageBus.register(this);
        this.padding = padding;
        this.textHeight = height - (padding * 2);
        this.x = x;
        this.y = y;
        this.textValue = "";
        this.textLabel = new Label("", 0f, 0f, 0f);
    }

    public TextInput(float width, float height, float x, float y, float padding, Vector4f colour, Texture texture) {
        super(width, height, x, y, colour, texture);
        MessageBus.register(this);
        this.padding = padding;
        this.textHeight = height - (padding * 2);
        this.x = x;
        this.y = y;
        this.textValue = "";
        this.textLabel = new Label("", 0f, 0f, 0f);
    }

    @EventListener
    public void keyPress(KeyEvent event) {
        if (enabled && event.getPressed()) {
            char key = KeyCode.toChar(event.getKeyCode());
            textValue += key;
            updateLabel(textValue);
            System.out.println(textValue);
        }
    }

    private void updateLabel(String newText) {
        textLabel.delete();
        textLabel = new Label(newText, textHeight, x, y);
    }

    public String getTextValue() {
        return textValue;
    }

    @Override
    public void draw() {
        super.draw();
        textLabel.render();
    }

    @Override
    public void update() {}

    @Override
    public void hide() {
        enabled = false;
    }

    @Override
    public void show() {
        enabled = true;
    }
}
