package com.battlezone.megamachines.renderer.ui.elements;

import com.battlezone.megamachines.events.keys.KeyEvent;
import com.battlezone.megamachines.input.KeyCode;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.messaging.EventListener;

public class NumericInput extends TextInput {

    public NumericInput(float width, float height, float x, float y, Vector4f primaryColour, float padding, int lengthLimit, String hint) {
        super(width, height, x, y, primaryColour, padding, lengthLimit, hint, "");
    }

    @Override
    @EventListener
    public void keyPress(KeyEvent event) {
        if (isEnabled() && event.getPressed()) {
            if (KeyCode.isNumber(event.getKeyCode())) {
                addLetter(KeyCode.toChar(event.getKeyCode()));
            } else if (event.getKeyCode() == KeyCode.PERIOD) {
                addLetter('.');
            } else if (event.getKeyCode() == KeyCode.BACKSPACE) {
                backspace();
            }
        }
    }
}
