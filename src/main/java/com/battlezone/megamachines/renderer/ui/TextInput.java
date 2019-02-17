package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.events.keys.KeyEvent;
import com.battlezone.megamachines.events.mouse.MouseButtonEvent;
import com.battlezone.megamachines.input.Cursor;
import com.battlezone.megamachines.input.KeyCode;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.renderer.Texture;

public class TextInput extends Button implements Interactive {

    private String textValue = "";
    private boolean active = false;
    private boolean enabled = true;
    private final int lengthLimit;

    public TextInput(float width, float height, float x, float y, Vector4f primaryColour, float padding, int lengthLimit) {
        super(width, height, x, y, primaryColour, primaryColour, "", padding);
        super.setAction(() -> {
            if (!active) {
                active = true;
                setText(textValue + "0");
            }
        });
        this.lengthLimit = lengthLimit;

    }

    public TextInput(float width, float height, float x, float y, Vector4f primaryColour, Texture texture, float padding, int lengthLimit) {
        super(width, height, x, y, primaryColour, primaryColour, texture, "", padding);
        super.setAction(() -> active = true);
        this.lengthLimit = lengthLimit;
    }


    @EventListener
    public void keyPress(KeyEvent event) {
        if (enabled && active && (event.getPressed())) {
            if (KeyCode.isNumber(event.getKeyCode()) || KeyCode.isNumber(event.getKeyCode())) {
                addLetter(KeyCode.toChar(event.getKeyCode()));
            } else if (event.getKeyCode() == KeyCode.BACKSPACE) {
                backspace();
            }
        }

    }

    void backspace() {
        if (textValue.length() > 0) {
            textValue = textValue.substring(0, textValue.length() - 1);
            setText(textValue + "0");
        }
    }

    void addLetter(char letter) {
        if (textValue.length() < lengthLimit) {
            textValue += letter;
            setText(textValue + "0");
        }
    }

    @Override
    @EventListener
    public void mouseClick(MouseButtonEvent event) {
        super.mouseClick(event);
        if (!isHovered()) {
            active = false;
            setText(textValue);
        }
    }

    @Override
    public void hide() {
        super.hide();
        enabled = false;
    }

    @Override
    public void show() {
        super.show();
        enabled = true;
    }

    public String getTextValue() {
        return textValue;
    }

    public boolean isEnabled() {
        return enabled && active;
    }
}