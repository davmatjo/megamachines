package com.battlezone.megamachines.renderer.ui.menu;

import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.Renderable;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.ui.elements.Button;
import com.battlezone.megamachines.renderer.ui.elements.ImageButton;

import java.util.function.Consumer;

public class ScrollingItems implements Renderable {

    private float x, y, width, height;

    private ListItem[] items;
    private Consumer<ListItem> didChoose;

    private Vector4f primaryColor, secondaryColor;

    private int page = 0;
    private ImageButton[] buttons;
    private Button buttonLeft, buttonRight;

    public ScrollingItems(float x, float y, float width, float height, ListItem[] items, Consumer<ListItem> didChoose, Vector4f primaryColor, Vector4f secondaryColor) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.items = items;
        this.didChoose = didChoose;

        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;

        init();
    }

    private void init() {
        this.buttons = new ImageButton[3];

        var boxTop = y;
        var boxBottom = y + height;
        var buttonWidth = width / 4;

        var boxSize = Math.min(buttonWidth, height);
        var padding = boxSize * 0.1f;
        boxSize = boxSize * 0.9f;

        buttonLeft = new Button(boxSize / 2, boxSize / 2, this.x, (-boxSize / 2 + boxTop + boxBottom) / 2, getPrimaryColor(), getSecondaryColor(), "L", padding);
        buttonLeft.setAction(() -> changeOffset(-1));

        buttonRight = new Button(boxSize / 2, boxSize / 2, this.x + width - boxSize / 2, (-boxSize / 2 + boxTop + boxBottom) / 2, getPrimaryColor(), getSecondaryColor(), "R", padding);
        buttonRight.setAction(() -> changeOffset(1));

        for (int i = 0; i < 3; i++) {
            final ListItem option = items[page + i];
            var button = new ImageButton(boxSize, boxSize, this.x + boxSize / 2 + padding + (boxSize + padding) * (i), (-boxSize + boxTop + boxBottom) / 2, option.getName(), option.getTexture());
            button.setAction(() -> choseItem(option));
            buttons[i] = button;
        }
    }

    public void setItems(ListItem[] items) {
        this.items = items;
        updateButtons();
    }

    private void changeOffset(int change) {
        if (page + change < 0 || page + change > items.length - 3) {
            return;
        }
        page += change;
        updateButtons();
    }

    private void updateButtons() {
        for (int i = 0; i < 3; i++) {
            final ListItem option = items[page + i];
            var button = buttons[i];
            button.setText(option.getName());
            button.setTexture(option.getTexture());
            button.setAction(() -> choseItem(option));
        }
    }

    private void choseItem(ListItem item) {
        didChoose.accept(item);
    }

    public Vector4f getPrimaryColor() {
        return primaryColor;
    }

    public Vector4f getSecondaryColor() {
        return secondaryColor;
    }

    @Override
    public void render() {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].render();
            buttons[i].update();
        }

        buttonLeft.render();
        buttonRight.render();

        buttonLeft.update();
        buttonRight.update();
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

        buttonLeft.hide();
        buttonRight.hide();
    }

    public void show() {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].show();
        }

        buttonLeft.show();
        buttonRight.show();
    }

}
