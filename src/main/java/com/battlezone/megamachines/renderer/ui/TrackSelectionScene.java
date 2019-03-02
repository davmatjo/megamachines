package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.generator.TrackCircleLoop;
import com.battlezone.megamachines.world.track.generator.TrackGenerator;
import com.battlezone.megamachines.world.track.generator.TrackLoopMutation2;
import com.battlezone.megamachines.world.track.generator.TrackSquareLoop;

import java.awt.*;
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

    public TrackSelectionScene(AbstractMenu menu, Vector4f primaryColor, Vector4f secondaryColor, Box background, Consumer<Track> startGame) {
        super(primaryColor, secondaryColor, background);

        this.startGame = startGame;
        this.menu = menu;
        this.makeTrackScene = new MakeTrackScene(menu, primaryColor, secondaryColor);
        init();
    }

    private void init() {
        addLabel("TRACK SELECTION", 2f, 0.8f, Colour.WHITE);

        var options = getTrackOptions();
        var boxTop = getButtonY(0.5f);
        var boxBottom = getButtonY(-2f);
        var boxSize = Math.abs(boxTop - boxBottom);
        var padding = boxSize * 0.1f;
        var index = 0;
        for (TrackOption option : options) {
            var button = new ImageButton(boxSize, boxSize, BUTTON_X + (boxSize + padding) * index, (boxTop + boxBottom) / 2, option.getName(), option.getTexture());
            button.setAction(() -> startGame(option.getTrack()));
            addElement(button);
            index++;
        }

        addButton("MAKE NEW", -2f, this::makeNew, BUTTON_WIDTH / 2 - PADDING, BUTTON_HEIGHT, BUTTON_WIDTH / 2 + PADDING);
        addButton("BACK", -2f, menu::navigationPop, BUTTON_WIDTH / 2 - PADDING, BUTTON_HEIGHT, 0);
    }

    private TrackOption[] getTrackOptions() {
        TrackOption[] options = new TrackOption[3];
        options[0] = new TrackOption("Loopity Loop", new TrackCircleLoop(20, 20, true));
        options[1] = new TrackOption("Sorta Square", new TrackLoopMutation2(20, 20));
        options[2] = new TrackOption("Really Regular", new TrackSquareLoop(20, 20, true));
        return options;
    }

    private void startGame(Track track) {
        this.startGame.accept(track);
        menu.navigationPop();
    }

    private void makeNew() {
        menu.navigationPush(makeTrackScene);
    }


}
