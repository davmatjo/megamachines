package com.battlezone.megamachines.renderer.ui.menu;

import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.renderer.ui.elements.Box;
import com.battlezone.megamachines.renderer.ui.elements.Button;
import com.battlezone.megamachines.renderer.ui.elements.ImageButton;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.TrackStorageManager;
import com.battlezone.megamachines.world.track.generator.TrackCircleLoop;
import com.battlezone.megamachines.world.track.generator.TrackGenerator;
import com.battlezone.megamachines.world.track.generator.TrackLoopMutation2;
import com.battlezone.megamachines.world.track.generator.TrackSquareLoop;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;

public class TrackSelectionScene extends MenuScene {

    class TrackOption {

        private String name;
        private Texture texture;
        private Track track;

        public TrackOption(String name, TrackGenerator generator) {
            this.name = name;
            this.track = generator.generateTrack();
            this.texture = AssetManager.loadTexture(track.generateMinimap(Color.GRAY, Color.GRAY));
        }

        public TrackOption(String name, Track track) {
            this.name = name;
            this.track = track;
            this.texture = AssetManager.loadTexture(track.generateMinimap(Color.GRAY, Color.GRAY));
        }

        public String getName() {
            return name;
        }

        public Texture getTexture() {
            return texture;
        }

        public Track getTrack() {
            return track;
        }
    }

    private AbstractMenu menu;
    private Consumer<Track> startGame;
    private MakeTrackScene makeTrackScene;
    private int page = 0;
    private ImageButton[] buttons;
    private TrackOption[] trackOptions;
    private TrackStorageManager storageManager;

    public TrackSelectionScene(AbstractMenu menu, Vector4f primaryColor, Vector4f secondaryColor, Box background, Consumer<Track> startGame) {
        super(primaryColor, secondaryColor, background);

        this.startGame = startGame;
        this.menu = menu;

        this.storageManager = new TrackStorageManager();
        this.makeTrackScene = new MakeTrackScene(menu, primaryColor, secondaryColor);
        this.buttons = new ImageButton[3];
        this.trackOptions = getTrackOptions();

        init();
    }

    private void init() {
        addLabel("TRACK SELECTION", 2f, 0.8f, Colour.WHITE);

        var boxTop = getButtonY(0.5f);
        var boxBottom = getButtonY(-2f);
        var buttonHeight = Math.abs(boxTop - boxBottom);
        var buttonWidth = BUTTON_WIDTH / 4;

        var boxSize = Math.min(buttonWidth, buttonHeight);
        var padding = boxSize * 0.1f;
        boxSize = boxSize * 0.9f;

        var buttonLeft = new Button(boxSize / 2, boxSize / 2, BUTTON_X, (boxTop + boxBottom) / 2, getPrimaryColor(), getSecondaryColor(), "L", padding);
        buttonLeft.setAction(() -> changeOffset(-1));
        addElement(buttonLeft);

        var buttonRight = new Button(boxSize / 2, boxSize / 2, BUTTON_X + BUTTON_WIDTH - boxSize / 2, (boxTop + boxBottom) / 2, getPrimaryColor(), getSecondaryColor(), "R", padding);
        buttonRight.setAction(() -> changeOffset(1));
        addElement(buttonRight);

        for (int i = 0; i < 3; i++) {
            TrackOption option = trackOptions[page + i];
            var button = new ImageButton(boxSize, boxSize, BUTTON_X + boxSize / 2 + padding + (boxSize + padding) * (i), (boxTop + boxBottom) / 2, option.getName(), option.getTexture());
            button.setAction(() -> startGame(option.getTrack()));
            addElement(button);
            buttons[i] = button;
        }

        addButton("MAKE NEW", -2f, this::makeNew, BUTTON_WIDTH / 2 - PADDING, BUTTON_HEIGHT, BUTTON_WIDTH / 2 + PADDING);
        addButton("BACK", -2f, menu::navigationPop, BUTTON_WIDTH / 2 - PADDING, BUTTON_HEIGHT, 0);

        hide();
    }

    private void changeOffset(int change) {
        if (page + change < 0 || page + change > trackOptions.length - 3) {
            return;
        }
        page += change;
        updateButtons();
    }

    private void updateButtons() {
        for (int i = 0; i < 3; i++) {
            TrackOption option = trackOptions[page + i];
            var button = buttons[i];
            button.setText(option.getName());
            button.setTexture(option.getTexture());
            button.setAction(() -> startGame(option.getTrack()));
        }
    }

    private TrackOption[] getTrackOptions() {
        var options = new ArrayList<TrackOption>();
        options.add(new TrackOption("Loopity Loop", new TrackCircleLoop(20, 20, true)));
        options.add(new TrackOption("Sorta Square", new TrackLoopMutation2(20, 20)));
        options.add(new TrackOption("Really Regular", new TrackSquareLoop(20, 20, true)));
        var tracks = storageManager.getTracks();
        for (Track track : tracks) {
            options.add(new TrackOption("Custom", track));
        }
        return options.toArray(TrackOption[]::new);
    }

    private void startGame(Track track) {
        this.startGame.accept(track);
        menu.navigationPop();
    }

    private void makeNew() {
        menu.navigationPush(makeTrackScene);
    }


}
