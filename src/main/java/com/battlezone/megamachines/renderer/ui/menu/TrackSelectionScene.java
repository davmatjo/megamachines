package com.battlezone.megamachines.renderer.ui.menu;

import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.theme.Theme;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.renderer.ui.elements.Box;
import com.battlezone.megamachines.renderer.ui.elements.Button;
import com.battlezone.megamachines.util.ArrayUtil;
import com.battlezone.megamachines.util.Triple;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.TrackStorageManager;
import com.battlezone.megamachines.world.track.generator.TrackCircleLoop;
import com.battlezone.megamachines.world.track.generator.TrackLoopMutation;
import com.battlezone.megamachines.world.track.generator.TrackSquareLoop;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TrackSelectionScene extends MenuScene {

    private BaseMenu menu;
    private Consumer<Triple<Track, Theme, Integer>> startGame;
    private MakeTrackScene makeTrackScene;
    private TrackOption[] trackOptions;
    private TrackStorageManager storageManager;
    private ScrollingItems trackSelector;
    private Button lapCountButton;
    private int lapCount = 3;

    public TrackSelectionScene(BaseMenu menu, Vector4f primaryColor, Vector4f secondaryColor, Box background, Consumer<Triple<Track, Theme, Integer>> startGame) {
        super(primaryColor, secondaryColor, background);

        this.startGame = startGame;
        this.menu = menu;

        this.storageManager = new TrackStorageManager();
        this.makeTrackScene = new MakeTrackScene(menu, primaryColor, secondaryColor);

        this.trackOptions = getTrackOptions();

        var boxTop = getButtonY(1.9f);
        var boxBottom = getButtonY(-0.5f);
        var buttonHeight = Math.abs(boxTop - boxBottom);
        this.trackSelector = new ScrollingItems(BUTTON_X, boxBottom, BUTTON_WIDTH, buttonHeight, trackOptions, (opt) -> startGame((TrackOption) opt), getPrimaryColor(), getSecondaryColor());

        init();
    }

    private void init() {
        addLabel("TRACK SELECTION", 2f, 0.8f, Colour.WHITE);

        addButton("MAKE NEW", -2f, this::makeNew, BUTTON_WIDTH / 2 - PADDING, BUTTON_HEIGHT, BUTTON_WIDTH / 2 + PADDING);
        addButton("BACK", -2f, menu::navigationPop, BUTTON_WIDTH / 2 - PADDING, BUTTON_HEIGHT, 0);

        lapCountButton = addButton("Laps: " + lapCount, -1f, this::toggleLaps, BUTTON_WIDTH / 2 - PADDING, BUTTON_HEIGHT, BUTTON_WIDTH / 2 + PADDING);
        addButton("RANDOM", -1f, this::randomTrack, BUTTON_WIDTH / 2 - PADDING, BUTTON_HEIGHT, 0);

        addElement(trackSelector);

        hide();
    }

    private void toggleLaps() {
        lapCount++;
        if (lapCount > 10) {
            lapCount = 1;
        }
        lapCountButton.setText("Laps: " + lapCount);
    }

    private void randomTrack() {
        startGame(ArrayUtil.randomElement(getTrackOptions()));
    }

    private TrackOption[] getTrackOptions() {
        var options = new ArrayList<TrackOption>();
        options.add(new TrackOption("Loopity Loop", new TrackCircleLoop(20, 20, true)));
        options.add(new TrackOption("Rather Random", new TrackLoopMutation(20, 20)));
        options.add(new TrackOption("Simply Square", new TrackSquareLoop(20, 20, true)));
        var tracks = storageManager.getTrackOptions();
        options.addAll(tracks);
        return options.toArray(TrackOption[]::new);
    }

    private void startGame(TrackOption chosen) {
        menu.navigationPop();
        ThemeSelectionScene scene = new ThemeSelectionScene(menu, getPrimaryColor(), getSecondaryColor(), getBackground(), theme -> this.startGame.accept(new Triple(chosen.getTrack(), theme, lapCount)));
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
