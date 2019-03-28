package com.battlezone.megamachines.renderer.ui.menu;

import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.renderer.ui.elements.Box;
import com.battlezone.megamachines.world.track.TrackStorageManager;

public class TrackManagementScene extends MenuScene {

    private BaseMenu menu;
    private TrackOption[] trackOptions;
    private TrackStorageManager storageManager;
    private TrackManager trackManager;

    public TrackManagementScene(BaseMenu menu, Vector4f primaryColor, Vector4f secondaryColor, Box background) {
        super(primaryColor, secondaryColor, background);
        this.menu = menu;

        this.storageManager = new TrackStorageManager();
        this.trackOptions = getTrackOptions();

        var boxTop = getButtonY(1.9f);
        var boxBottom = getButtonY(-1.2f);
        var buttonHeight = Math.abs(boxTop - boxBottom);
        this.trackManager = new TrackManager(BUTTON_X, boxBottom, BUTTON_WIDTH, buttonHeight, trackOptions, getPrimaryColor(), getSecondaryColor());

        init();
    }

    private void init() {
        addLabel("TRACK MANAGEMENT", 2f, 0.8f, Colour.WHITE);

        addButton("SAVE", -2f, this::saveChanges, 2, 2);
        addButton("BACK", -2f, menu::navigationPop, 1, 2);

        addElement(trackManager);

        hide();
    }

    private void saveChanges() {
        //get new options
        var newOptions = trackManager.getItems();
        for (TrackOption option : newOptions) {
            storageManager.rename(option.getName(), option.getNewName());
        }
        menu.navigationPop();
    }

    private TrackOption[] getTrackOptions() {
        var tracks = storageManager.getTrackOptions();
        return tracks.toArray(TrackOption[]::new);
    }

    @Override
    public void show() {
        super.show();
        //reload files
        this.trackOptions = getTrackOptions();
        if (this.trackManager != null) {
            trackManager.show();
            this.trackManager.setItems(this.trackOptions);
        }
    }

    @Override
    public void hide() {
        super.hide();
        if (this.trackManager != null)
            trackManager.hide();
    }
}
