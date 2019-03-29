package com.battlezone.megamachines.renderer.ui.menu;

import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.ui.Scene;
import com.battlezone.megamachines.renderer.ui.elements.*;

/**
 * A base class for scenes that are menus. Provides convenience methods are adding ui elements
 */
public class MenuScene extends Scene {

    static final float BUTTON_WIDTH = 3f;
    static final float BUTTON_HEIGHT = 0.25f;
    static final float BUTTON_X = -BUTTON_WIDTH / 2;
    static final float BUTTON_CENTRE_Y = -0.125f;
    static final float BUTTON_OFFSET_Y = 0.4f;
    static final float PADDING = 0.05f;
    private Vector4f primaryColor, secondaryColor;
    private Box background;
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

    /**
     * Returns the y coordinate for a button based on a formula which allows buttons to be spaced by placing one at getButtonY(0) the next at getButtonY(1) etc
     *
     * @param numberFromCenter
     * @return
     */
    public static float getButtonY(float numberFromCenter) {
        return BUTTON_CENTRE_Y + numberFromCenter * BUTTON_OFFSET_Y;
    }

    /**
     * Adds a label to the scene
     *
     * @param text     the text
     * @param position The position index
     * @param scale    The scaling factor for the height
     * @param colour   The text colour
     * @return The created label
     */
    public Label addLabel(String text, float position, float scale, Vector4f colour) {
        // centers the text horizontally
        float height = BUTTON_HEIGHT * scale;

        Label label = new Label(text, height, 0, getButtonY(position), colour);
        label.setPos(0 - label.getWidth() / 2, getButtonY(position));
        addElement(label);
        return label;
    }

    /**
     * Changed the position of a label
     *
     * @param label    The label to move
     * @param position The new position index
     */
    public void adjustLabelPosition(Label label, float position) {
        label.setPos(0 - label.getWidth() / 2, getButtonY(position));
    }

    /**
     * Adds a button to the scene with the default height, width and x position and no action
     *
     * @param title    The button text
     * @param position The position index
     * @return The button
     */
    public Button addButton(String title, float position) {
        return addButton(title, position, null, BUTTON_WIDTH, BUTTON_HEIGHT, 0);
    }

    /**
     * Add a button to the scene with the default width and height
     *
     * @param title    The button text
     * @param position The position index
     * @param action   The runnable
     * @return The button
     */
    public Button addButton(String title, float position, Runnable action) {
        return addButton(title, position, action, BUTTON_WIDTH, BUTTON_HEIGHT, 0);
    }

    /**
     * Adds a button in a row
     *
     * @param title
     * @param position
     * @param action
     * @param col      The index of this button horizontally
     * @param cols     The number of buttons in this row
     * @return
     */
    public Button addButton(String title, float position, Runnable action, int col, int cols) {
        var buttonWidthWithPadding = BUTTON_WIDTH / (float) cols;
        var paddingPerButton = buttonWidthWithPadding * 0.1f;
        var totalPadding = (cols - 1) * paddingPerButton;
        var totalButtonWidth = BUTTON_WIDTH - totalPadding;
        float buttonWidth = totalButtonWidth / (float) cols;
        float xOffset = (col - 1) * buttonWidth + (col - 1) * paddingPerButton;

        return addButton(title, position, action, buttonWidth, BUTTON_HEIGHT, xOffset);
    }

    /**
     * Adds a button to the scene
     *
     * @param title
     * @param position
     * @param action
     * @param width
     * @param height
     * @param xOffset  The xoffset of this button from the normal position
     * @return
     */
    public Button addButton(String title, float position, Runnable action, float width, float height, float xOffset) {
        Button button = new Button(width, height, BUTTON_X + xOffset, getButtonY(position), primaryColor, secondaryColor, title, PADDING);
        button.setAction(action);
        addElement(button);
        if (keyboardNavigableHolder != null)
            keyboardNavigableHolder.addElement(button);
        return button;
    }

    /**
     * Add a seekbar to the scene
     *
     * @param title
     * @param value
     * @param position
     * @return
     */
    public SeekBar addSeekbar(String title, float value, float position) {
        return addSeekbar(title, value, position, null, BUTTON_WIDTH, BUTTON_HEIGHT, 0, 0, PADDING);
    }

    /**
     * Add a seekbar to the scene
     *
     * @param title
     * @param value
     * @param position
     * @param onChange The runnable to run when the value has changed
     * @param width
     * @param height
     * @param xOffset
     * @param yOffset
     * @param padding
     * @return
     */
    public SeekBar addSeekbar(String title, float value, float position, Runnable onChange, float width, float height, float xOffset, float yOffset, float padding) {
        SeekBar sb = new SeekBar(width, height, BUTTON_X + xOffset, getButtonY(position) + yOffset, primaryColor, secondaryColor, title, value, padding);
        sb.setOnValueChanged(onChange);
        addElement(sb);
        //keyboardNavigableHolder.addElement(sb);
        return sb;
    }

    /**
     * Add a numeric input field
     *
     * @param hint
     * @param maxLength
     * @param position
     * @return
     */
    public NumericInput addNumericInput(String hint, int maxLength, float position) {
        NumericInput input = new NumericInput(BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_X, getButtonY(position), primaryColor, PADDING, maxLength, hint);
        addElement(input);
        if (keyboardNavigableHolder != null)
            keyboardNavigableHolder.addElement(input);
        return input;
    }

    /**
     * Add a text input field
     *
     * @param hint
     * @param currentValue
     * @param maxLength
     * @param position
     * @return
     */
    public TextInput addTextInput(String hint, String currentValue, int maxLength, float position) {
        TextInput input = new TextInput(BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_X, getButtonY(position), primaryColor, PADDING, maxLength, hint, currentValue);
        addElement(input);
        if (keyboardNavigableHolder != null)
            keyboardNavigableHolder.addElement(input);
        return input;
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
