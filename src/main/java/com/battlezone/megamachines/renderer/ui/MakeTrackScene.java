package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.events.keys.KeyEvent;
import com.battlezone.megamachines.input.KeyCode;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;

public class MakeTrackScene extends MenuScene {

    private AbstractMenu menu;
    private TrackEditor editor;
    private Label infoLabel;

    public MakeTrackScene(AbstractMenu menu, Vector4f primaryColor, Vector4f secondaryColor) {
        super(primaryColor, secondaryColor, new Box(4f, 2f, -2f, -1f, Colour.GREEN));

        MessageBus.register(this);

        this.menu = menu;

        var size = getButtonY(2f) - getButtonY(-1.3f);
        this.editor = new TrackEditor(20, 0 - size / 2f, getButtonY(-1.3f), size, size);

        addButton("SAVE", -2f, this::save, BUTTON_WIDTH / 2 - PADDING, BUTTON_HEIGHT, BUTTON_WIDTH / 2 + PADDING);
        addButton("CANCEL", -2f, menu::navigationPop, BUTTON_WIDTH / 2 - PADDING, BUTTON_HEIGHT, 0);


        infoLabel = addLabel("Press SPACE to lay first piece", 2.5f, 0.3f, Colour.WHITE);
        addElement(editor);
    }

    @EventListener
    public void keyDown(KeyEvent event) {
        if (event.getPressed()) {
            if (event.getKeyCode() == KeyCode.LEFT)
                editor.moveCursor(-1, 0);
            if (event.getKeyCode() == KeyCode.RIGHT)
                editor.moveCursor(1, 0);
            if (event.getKeyCode() == KeyCode.UP)
                editor.moveCursor(0, 1);
            if (event.getKeyCode() == KeyCode.DOWN)
                editor.moveCursor(0, -1);
            if (event.getKeyCode() == KeyCode.SPACE) {
                infoLabel.setText("Use arrow keys to draw the track");
                editor.beginEditing();
            }
        }
    }

    private void save() {

    }

}
