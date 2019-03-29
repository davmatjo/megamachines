package com.battlezone.megamachines.renderer.ui.elements;

import com.battlezone.megamachines.events.keys.KeyEvent;
import com.battlezone.megamachines.input.Cursor;
import com.battlezone.megamachines.input.KeyCode;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.renderer.Model;
import com.battlezone.megamachines.renderer.Renderable;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.ui.Interactive;
import com.battlezone.megamachines.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

/**
 * A class to manage keyboard navigation of ui elements
 */
public class KeyboardNavigableHolder implements Interactive, Renderable {

    private ArrayList<KeyboardNavigable> elements;
    private Cursor cursor;
    private Pair<Double, Double> lastCursorPos;
    private KeyboardNavigable currentActive;

    public KeyboardNavigableHolder() {
        MessageBus.register(this);
        elements = new ArrayList<>();
        this.cursor = Cursor.getCursor();
        lastCursorPos = new Pair<>(cursor.getX(), cursor.getY());
    }

    /**
     * Add an element to this holder so that it can be keyboard controlled
     *
     * @param e The element
     */
    public void addElement(KeyboardNavigable e) {
        e.setManaged(true);
        elements.add(e);
    }

    /**
     * Removes an element from the holder, so it will no longer be keyboard controlled
     *
     * @param e The element
     */
    public void removeElement(KeyboardNavigable e) {
        e.setManaged(false);
        elements.remove(e);
    }

    @Override
    public void update() {
        if (this.cursor.getX() == lastCursorPos.getFirst() && this.cursor.getY() == lastCursorPos.getSecond()) {
            refreshActive();
            return;
        }
        lastCursorPos.set(this.cursor.getX(), this.cursor.getY());
        for (int i = 0; i < elements.size(); i++) {
            var element = elements.get(i);
            boolean active = (this.cursor.getX() > element.getLeftX() && this.cursor.getX() < element.getRightX() && this.cursor.getY() > element.getBottomY() && this.cursor.getY() < element.getTopY());
            elements.get(i).focusChanged(active);
            if (active)
                currentActive = element;
        }
    }

    @EventListener
    public void keyDown(KeyEvent e) {
        if (e.getPressed()) return;

        if (e.getKeyCode() == KeyCode.ENTER || e.getKeyCode() == KeyCode.SPACE) {
            if (currentActive != null)
                this.currentActive.runAction();
        } else if (e.getKeyCode() == KeyCode.DOWN || e.getKeyCode() == KeyCode.UP || e.getKeyCode() == KeyCode.LEFT || e.getKeyCode() == KeyCode.RIGHT) {
            Optional<KeyboardNavigable> next = Optional.empty();
            //If there is nothing selected, select the first item
            if (currentActive == null) {
                if (elements.size() > 0)
                    this.currentActive = elements.get(0);
                refreshActive();
                return;
            }

            //Otherwise sort the elements to get the one which is next along in the direction of the arrow button pressed

            Comparator<KeyboardNavigable> minX = (a, b) -> (Float.compare(a.getLeftX() - currentActive.getLeftX(), b.getLeftX() - currentActive.getLeftX()));
            Comparator<KeyboardNavigable> minY = (a, b) -> (Float.compare(a.getTopY() - currentActive.getTopY(), b.getTopY() - currentActive.getTopY()));

            if (e.getKeyCode() == KeyCode.DOWN)
                next = elements.stream().sorted((a, b) -> (Float.compare(b.getTopY(), a.getTopY()))).filter((el) -> el.getTopY() < currentActive.getTopY()).min(minX);
            if (e.getKeyCode() == KeyCode.UP)
                next = elements.stream().sorted((a, b) -> (Float.compare(a.getTopY(), b.getTopY()))).filter((el) -> el.getTopY() > currentActive.getTopY()).min(minX);
            if (e.getKeyCode() == KeyCode.RIGHT)
                next = elements.stream().sorted((a, b) -> (Float.compare(a.getLeftX(), b.getLeftX()))).filter((el) -> el.getLeftX() > currentActive.getLeftX()).min(minY);
            if (e.getKeyCode() == KeyCode.LEFT)
                next = elements.stream().sorted((a, b) -> (Float.compare(b.getLeftX(), a.getLeftX()))).filter((el) -> el.getLeftX() < currentActive.getLeftX()).min(minY);

            //If there is one, make that active
            next.ifPresent((element) -> {
                        this.currentActive = element;
                        refreshActive();
                    }
            );
        }


    }

    /**
     * Lets every element know whether or not it is currently focused
     */
    private void refreshActive() {
        for (int i = 0; i < elements.size(); i++) {
            var element = elements.get(i);
            element.focusChanged(element.equals(currentActive));
        }
    }

    @Override
    public void hide() {

    }

    @Override
    public void show() {

    }

    @Override
    public void draw() {

    }

    @Override
    public Model getModel() {
        return null;
    }

    @Override
    public void render() {

    }

    @Override
    public int getDepth() {
        return 0;
    }

    @Override
    public Shader getShader() {
        return null;
    }

    @Override
    public void delete() {

    }
}
