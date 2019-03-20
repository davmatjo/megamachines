package com.battlezone.megamachines.renderer.ui.menu;

import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.theme.Theme;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.renderer.ui.elements.Box;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.TrackStorageManager;
import com.battlezone.megamachines.world.track.generator.TrackCircleLoop;
import com.battlezone.megamachines.world.track.generator.TrackGenerator;
import com.battlezone.megamachines.world.track.generator.TrackLoopMutation;
import com.battlezone.megamachines.world.track.generator.TrackSquareLoop;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.BiConsumer;

public class TrackSelectionScene extends MenuScene {

    class TrackOption extends ListItem {

        private Track track;

        public TrackOption(String name, TrackGenerator generator) {
            this(name, generator.generateTrack());
        }

        public TrackOption(String name, Track track) {
            super(name, AssetManager.loadTexture(track.generateMinimap(Color.GRAY, Color.GRAY)));
            this.track = track;
        }

        public Track getTrack() {
            return track;
        }
    }

    private AbstractMenu menu;
    private BiConsumer<Track, Theme> startGame;
    private MakeTrackScene makeTrackScene;
    private TrackOption[] trackOptions;
    private TrackStorageManager storageManager;
    private ScrollingItems trackSelector;

    public TrackSelectionScene(AbstractMenu menu, Vector4f primaryColor, Vector4f secondaryColor, Box background, BiConsumer<Track, Theme> startGame) {
        super(primaryColor, secondaryColor, background);

        this.startGame = startGame;
        this.menu = menu;

        this.storageManager = new TrackStorageManager();
        this.makeTrackScene = new MakeTrackScene(menu, primaryColor, secondaryColor);

        this.trackOptions = getTrackOptions();

        var boxTop = getButtonY(0.5f);
        var boxBottom = getButtonY(-2f);
        var buttonHeight = Math.abs(boxTop - boxBottom);
        this.trackSelector = new ScrollingItems(BUTTON_X, (boxTop + boxBottom) / 2f, BUTTON_WIDTH, buttonHeight, trackOptions, (opt) -> startGame((TrackOption) opt), getPrimaryColor(), getSecondaryColor());

        init();
    }

    private void init() {
        addLabel("TRACK SELECTION", 2f, 0.8f, Colour.WHITE);

        addButton("MAKE NEW", -2f, this::makeNew, BUTTON_WIDTH / 2 - PADDING, BUTTON_HEIGHT, BUTTON_WIDTH / 2 + PADDING);
        addButton("BACK", -2f, menu::navigationPop, BUTTON_WIDTH / 2 - PADDING, BUTTON_HEIGHT, 0);

        addElement(trackSelector);

        hide();
    }


    private TrackOption[] getTrackOptions() {
        var options = new ArrayList<TrackOption>();
        options.add(new TrackOption("Loopity Loop", new TrackCircleLoop(20, 20, true)));
        options.add(new TrackOption("Rather Random", new TrackLoopMutation(20, 20)));
        options.add(new TrackOption("Simply Square", new TrackSquareLoop(20, 20, true)));
        var tracks = storageManager.getTracks();
        for (Track track : tracks) {
            options.add(new TrackOption("Custom", track));
        }
        return options.toArray(TrackOption[]::new);
    }

    private void startGame(TrackOption chosen) {
        menu.navigationPop();
        ThemeSelectionScene scene = new ThemeSelectionScene(menu, getPrimaryColor(), getSecondaryColor(), getBackground(), theme -> this.startGame.accept(chosen.getTrack(), theme));
        menu.navigationPush(scene);
    }

    private void makeNew() {
        menu.navigationPush(makeTrackScene);
    }

    @Override
    public void show() {
        super.show();
        //reload files
        this.trackOptions = getTrackOptions();
        if (this.trackSelector != null) {
            trackSelector.show();
            this.trackSelector.setItems(this.trackOptions);
        }
    }

    @Override
    public void hide() {
        super.hide();
        if (this.trackSelector != null)
            trackSelector.hide();
    }
}
