package com.battlezone.megamachines.renderer.ui.menu;

import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.Renderable;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.renderer.ui.elements.Box;
import com.battlezone.megamachines.renderer.ui.elements.Button;
import com.battlezone.megamachines.renderer.ui.elements.ImageButton;
import com.battlezone.megamachines.renderer.ui.elements.TextInput;

public class TrackManager implements Renderable {

    Box background;
    private float x, y, width, height;
    private TrackOption[] items;
    private Vector4f primaryColor, secondaryColor;
    private int page = 0;
    private ImageButton[] buttons;
    private TextInput[] inputs;
    private Button[] deleteButtons;
    private Button buttonUp, buttonDown;

    public TrackManager(float x, float y, float width, float height, TrackOption[] items, Vector4f primaryColor, Vector4f secondaryColor) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;


        for (int i = 0; i < items.length; i++) {
            items[i].setNewName(items[i].getName());
        }
        this.items = items;

        init();
    }

    private void init() {
        var maxPerPage = 5;
        var perPage = Math.min(items.length, maxPerPage);
        this.buttons = new ImageButton[perPage];
        this.inputs = new TextInput[perPage];

        var boxTop = y;
        var buttonWidth = height / maxPerPage;

        var boxSize = Math.min(buttonWidth, height);
        var padding = boxSize * 0.1f;
        boxSize = boxSize * 0.9f;

        boxTop = y + (maxPerPage - perPage) * (boxSize + padding);

        buttonUp = new Button(boxSize / 2, boxSize / 2, this.x + this.width - boxSize / 2, this.y + this.height - boxSize / 2, getPrimaryColor(), getSecondaryColor(), "U", padding);
        buttonUp.setAction(() -> changeOffset(-1));

        buttonDown = new Button(boxSize / 2, boxSize / 2, this.x + this.width - boxSize / 2, this.y, getPrimaryColor(), getSecondaryColor(), "D", padding);
        buttonDown.setAction(() -> changeOffset(1));

        for (int i = 0; i < perPage; i++) {
            final ListItem option = items[page + i];
            var button = new ImageButton(boxSize, boxSize, this.x, boxTop + (boxSize + padding) * i, "", option.getTexture());
            buttons[i] = button;

            var input = new TextInput(this.width - 2 * (boxSize + padding), boxSize, this.x + boxSize + padding, boxTop + (boxSize + padding) * i, primaryColor, padding * 2, 20, "NAME", option.getName());
            inputs[i] = input;
        }

        background = new Box(width, this.height + padding * 2, x, this.y - padding, Colour.RED);
    }

    private void injectNames() {
        //set new names on each items
        for (int i = 0; i < inputs.length; i++) {
            items[i + page].setNewName(inputs[i].getTextValue());
        }
    }

    public TrackOption[] getItems() {
        injectNames();
        return this.items;
    }

    public void setItems(TrackOption[] items) {
        for (int i = 0; i < items.length; i++) {
            items[i].setNewName(items[i].getName());
        }
        this.items = items;
        updateButtons();
    }

    private void changeOffset(int change) {
        if (page + change < 0 || page + change > items.length - buttons.length) {
            return;
        }
        injectNames();
        page += change;
        updateButtons();
    }

    private void updateButtons() {
        for (int i = 0; i < buttons.length; i++) {
            final TrackOption option = items[page + i];
            var button = buttons[i];
            //button.setText(option.getName());
            button.setTexture(option.getTexture());
            inputs[i].replaceText(option.getNewName());
        }
    }

    public Vector4f getPrimaryColor() {
        return primaryColor;
    }

    public Vector4f getSecondaryColor() {
        return secondaryColor;
    }

    @Override
    public void render() {
        //  background.render();
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].render();
            buttons[i].update();
        }

        for (int i = 0; i < inputs.length; i++) {
            inputs[i].render();
            inputs[i].update();
        }

        if (page > 0)
            buttonUp.render();
        if (page < items.length - buttons.length)
            buttonDown.render();

        buttonUp.update();
        buttonDown.update();
    }

    @Override
    public Shader getShader() {
        return null;
    }

    @Override
    public void delete() {

    }

    public void hide() {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].hide();
        }
        for (int i = 0; i < inputs.length; i++) {
            inputs[i].hide();
        }
        for (int i = 0; i < deleteButtons.length; i++) {
            deleteButtons[i].hide();
        }

        buttonUp.hide();
        buttonDown.hide();
    }

    public void show() {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].show();
        }

        buttonUp.show();
        buttonDown.show();
    }

}
