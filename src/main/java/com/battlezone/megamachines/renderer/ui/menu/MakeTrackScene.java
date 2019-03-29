package com.battlezone.megamachines.renderer.ui.menu;

import com.battlezone.megamachines.events.keys.KeyEvent;
import com.battlezone.megamachines.events.ui.ErrorEvent;
import com.battlezone.megamachines.input.KeyCode;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.renderer.ui.elements.Box;
import com.battlezone.megamachines.renderer.ui.elements.Label;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.TrackStorageManager;
import com.battlezone.megamachines.world.track.generator.TrackFromGridGenerator;

/**
 * A scene for handling making a new track
 */
public class MakeTrackScene extends MenuScene {

    private BaseMenu menu;
    private TrackEditor editor;
    private Label infoLabel;

    public MakeTrackScene(BaseMenu menu, Vector4f primaryColor, Vector4f secondaryColor) {
        super(primaryColor, secondaryColor, new Box(4f, 2f, -2f, -1f, Colour.BLACK), false);

        this.menu = menu;

        var size = getButtonY(2f) - getButtonY(-1.3f);
        this.editor = new TrackEditor(20, 0 - size / 2f, getButtonY(-1.3f), size, size);

        addButton("SAVE", -2f, this::save, BUTTON_WIDTH / 2 - PADDING, BUTTON_HEIGHT, BUTTON_WIDTH / 2 + PADDING);
        addButton("CANCEL", -2f, menu::navigationPop, BUTTON_WIDTH / 2 - PADDING, BUTTON_HEIGHT, 0);

        infoLabel = addLabel("Press SPACE to lay first piece", 2.5f, 0.2f, Colour.WHITE);
        addElement(editor);

        hide();
    }

    @Override
    public void show() {
        super.show();
        if (editor != null)
            editor.reset();
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
                editor.toggleEditing();
                //Change the message based on whether we are editing or moving cursor
                if (editor.isEditing())
                    infoLabel.setText("Use arrow keys to draw the track, space to move cursor");
                else
                    infoLabel.setText("Press SPACE to lay pieces");
                adjustLabelPosition(infoLabel, 2.5f);
            }
        }
    }

    /**
     * Save the new track
     */
    private void save() {
        var boolGrid = editor.getBoolGrid();
        //Generate a track grid from the bool frid
        var grid = Track.createFromBoolGrid(boolGrid);
        var valid = Track.isValidTrack(grid);
        if (!valid) {
            //fire an error event
            MessageBus.fire(new ErrorEvent("Invalid track", "", 2));
        } else {
            //Save the track and pop back
            var generator = new TrackFromGridGenerator(grid);
            var track = generator.generateTrack();
            (new TrackStorageManager()).saveTrack(track);
            menu.navigationPop();
            menu.showError(new ErrorEvent("Saved track!", "", 2, Colour.GREEN));
        }
    }

}
