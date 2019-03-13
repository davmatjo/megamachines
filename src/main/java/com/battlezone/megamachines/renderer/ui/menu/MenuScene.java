package com.battlezone.megamachines.renderer.ui.menu;

import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.ui.Scene;
import com.battlezone.megamachines.renderer.ui.elements.*;

public class MenuScene extends Scene {

    private Vector4f primaryColor, secondaryColor;
    private Box background;

    static final float BUTTON_WIDTH = 3f;
    static final float BUTTON_HEIGHT = 0.25f;
    static final float BUTTON_X = -BUTTON_WIDTH / 2;
    static final float BUTTON_CENTRE_Y = -0.125f;
    static final float BUTTON_OFFSET_Y = 0.4f;
    static final float PADDING = 0.05f;

    private KeyboardNavigableHolder keyboardNavigableHolder;

    public MenuScene(Vector4f primaryColor, Vector4f secondaryColor, Box background) {
        this(primaryColor, secondaryColor, background, true);
    }

    public MenuScene(Vector4f primaryColor, Vector4f secondaryColor, Box background, boolean keyboardNavigable) {
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.background = background;

        if (keyboardNavigable) {
            keyboardNavigableHolder = new KeyboardNavigableHolder();
            addElement(keyboardNavigableHolder);
        }

        if (background != null)
            addElement(background);
    }

    public Label addLabel(String text, float position, float scale, Vector4f colour) {
        // centers the text horizontally
        float height = BUTTON_HEIGHT * scale;

        Label label = new Label(text, height, 0, getButtonY(position), colour);
        label.setPos(0 - label.getWidth() / 2, getButtonY(position));
        addElement(label);
        return label;
    }

    public void adjustLabelPosition(Label label, float position) {
        label.setPos(0 - label.getWidth() / 2, getButtonY(position));
    }

    public Button addButton(String title, float position) {
        return addButton(title, position, null, BUTTON_WIDTH, BUTTON_HEIGHT, 0);
    }

    public Button addButton(String title, float position, Runnable action) {
        return addButton(title, position, action, BUTTON_WIDTH, BUTTON_HEIGHT, 0);
    }

    public Button addButton(String title, float position, Runnable action, int col, int cols) {
        var buttonWidthWithPadding = BUTTON_WIDTH / (float) cols;
        var paddingPerButton = buttonWidthWithPadding * 0.1f;
        var totalPadding = (cols - 1) * paddingPerButton;
        var totalButtonWidth = BUTTON_WIDTH - totalPadding;
        float buttonWidth = totalButtonWidth / (float) cols;
        float xOffset = (col - 1) * buttonWidth + (col - 1) * paddingPerButton;

        return addButton(title, position, action, buttonWidth, BUTTON_HEIGHT, xOffset);
    }

    public Button addButton(String title, float position, Runnable action, float width, float height, float xOffset) {
        Button button = new Button(width, height, BUTTON_X + xOffset, getButtonY(position), primaryColor, secondaryColor, title, PADDING);
        button.setAction(action);
        addElement(button);
        if (keyboardNavigableHolder != null)
            keyboardNavigableHolder.addElement(button);
        return button;
    }

    public SeekBar addSeekbar(String title, float value, float position) {
        return addSeekbar(title, value, position, null, BUTTON_WIDTH, BUTTON_HEIGHT, 0, 0, PADDING);
    }

    public SeekBar addSeekbar(String title, float value, float position, Runnable onChange, float width, float height, float xOffset, float yOffset, float padding) {
        SeekBar sb = new SeekBar(width, height, BUTTON_X + xOffset, getButtonY(position) + yOffset, primaryColor, secondaryColor, title, value, padding);
        sb.setOnValueChanged(onChange);
        addElement(sb);
        //keyboardNavigableHolder.addElement(sb);
        return sb;
    }

    public NumericInput addNumericInput(String hint, int maxLength, float position) {
        NumericInput input = new NumericInput(BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_X, getButtonY(position), primaryColor, PADDING, maxLength, hint);
        addElement(input);
        if (keyboardNavigableHolder != null)
            keyboardNavigableHolder.addElement(input);
        return input;
    }

    public static float getButtonY(float numberFromCenter) {
        return BUTTON_CENTRE_Y + numberFromCenter * BUTTON_OFFSET_Y;
    }

    public Vector4f getPrimaryColor() {
        return primaryColor;
    }

    public Vector4f getSecondaryColor() {
        return secondaryColor;
    }

    public Box getBackground() {
        return background;
    }

}
