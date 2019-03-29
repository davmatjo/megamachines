package com.battlezone.megamachines.renderer.ui.elements;

import com.battlezone.megamachines.events.keys.KeyEvent;
import com.battlezone.megamachines.events.mouse.MouseButtonEvent;
import com.battlezone.megamachines.input.KeyCode;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.renderer.ui.Interactive;

/**
 * Allows the user to input text
 */
public class TextInput extends Button implements Interactive {

    //The cursor character
    private final static char CURSOR = '_';
    //The max length of input
    private final int lengthLimit;
    //The hint to show when there is no input
    private final String hint;
    //The current user input
    private String textValue = "";
    private boolean active = false;
    private boolean enabled = true;

    public TextInput(float width, float height, float x, float y, Vector4f primaryColour, float padding, int lengthLimit, String initial, String currentValue) {
        super(width, height, x, y, primaryColour, primaryColour, "", padding);
        super.setAction(() -> {
            if (!active) {
                active = true;
                setText(textValue + CURSOR);
            }
        });
        //Initial value should be put in
        if (!currentValue.equals("")) {
            textValue = currentValue;
            setText(textValue);
        }
        this.lengthLimit = lengthLimit;
        this.hint = initial;
    }

    @EventListener
    public void keyPress(KeyEvent event) {
        if (enabled && active && (event.getPressed())) {
            if (KeyCode.isNumber(event.getKeyCode()) || KeyCode.isLetter(event.getKeyCode())) {
                addLetter(KeyCode.toChar(event.getKeyCode()));
            } else if (event.getKeyCode() == KeyCode.BACKSPACE) {
                backspace();
            }
        }

    }

    /**
     * Remove the last character
     */
    void backspace() {
        if (textValue.length() > 0) {
            //Remove the character then set text again but add a cursor in
            textValue = textValue.substring(0, textValue.length() - 1);
            setText(textValue + CURSOR);
        }
    }

    /**
     * Adds a letter to the end of the box
     *
     * @param letter
     */
    void addLetter(char letter) {
        if (textValue.length() < lengthLimit) {
            textValue += letter;
            //Append the letter and display the text with a cursor
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

    /**
     * @return The text currently displayed
     */
    public String getDisplayedValue() {
        return textValue.equals("") ? hint : textValue;
    }

    /**
     * Replace the textvalue
     *
     * @param newText
     */
    public void replaceText(String newText) {
        textValue = newText;
        setText(textValue);
    }

    /**
     * The currently input text
     *
     * @return
     */
    public String getTextValue() {
        return textValue;
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
