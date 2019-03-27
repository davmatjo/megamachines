package com.battlezone.megamachines.renderer.ui.elements;

import com.battlezone.megamachines.events.keys.KeyEvent;
import com.battlezone.megamachines.events.mouse.MouseButtonEvent;
import com.battlezone.megamachines.input.KeyCode;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.renderer.ui.Interactive;

public class TextInput extends Button implements Interactive {

    private final static char CURSOR = '_';
    private final int lengthLimit;
    private final String hint;
    private String textValue = "";
    private boolean active = false;
    private boolean enabled = true;

    public TextInput(float width, float height, float x, float y, Vector4f primaryColour, float padding, int lengthLimit, String initial) {
        super(width, height, x, y, primaryColour, primaryColour, "", padding);
        super.setAction(() -> {
            if (!active) {
                active = true;
                setText(textValue + CURSOR);
            }
        });
        this.lengthLimit = lengthLimit;
        this.hint = initial;
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
            setText(textValue + CURSOR);
        }
    }

    void addLetter(char letter) {
        if (textValue.length() < lengthLimit) {
            textValue += letter;
            setText(textValue + CURSOR);
        }
    }

    @Override
    @EventListener
    public void mouseClick(MouseButtonEvent event) {
        super.mouseClick(event);
        if (!isHovered()) {
            active = false;
            if (textValue.isBlank()) {
                setText(hint, Colour.GREY);
            } else {
                setText(textValue);
            }
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
        return textValue.equals("") ? hint : textValue;
    }

    public boolean isEnabled() {
        return enabled && active;
    }

    @Override
    public void focusChanged(boolean active) {
        super.focusChanged(active);
        this.active = active;
    }
}
