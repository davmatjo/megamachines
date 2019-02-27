package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.world.track.generator.TrackCircleLoop;
import com.battlezone.megamachines.world.track.generator.TrackGenerator;
import com.battlezone.megamachines.world.track.generator.TrackLoopMutation2;

import java.util.function.Consumer;

public class TrackSelectionScene extends MenuScene {

    class TrackOption {

        private String name;
        private Texture texture;
        private TrackGenerator generator;

        public TrackOption(String name, Texture texture, TrackGenerator generator) {
            this.name = name;
            this.texture = texture;
            this.generator = generator;
        }

        public String getName() {
            return name;
        }

        public Texture getTexture() {
            return texture;
        }

        public TrackGenerator getGenerator() {
            return generator;
        }
    }

    private AbstractMenu menu;
    private Consumer<TrackGenerator> startGame;

    public TrackSelectionScene(AbstractMenu menu, Vector4f primaryColor, Vector4f secondaryColor, Box background, Consumer<TrackGenerator> startGame) {
        super(primaryColor, secondaryColor, background);

        this.startGame = startGame;
        this.menu = menu;
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
            button.setAction(() -> startGame(option.getGenerator()));
            addElement(button);
            index++;
        }

        addButton("MAKE NEW", -2f, null, BUTTON_WIDTH / 2 - PADDING, BUTTON_HEIGHT, BUTTON_WIDTH / 2 + PADDING);
        addButton("BACK", -2f, menu::navigationPop, BUTTON_WIDTH / 2 - PADDING, BUTTON_HEIGHT, 0);
    }

    private TrackOption[] getTrackOptions() {
        TrackOption[] options = new TrackOption[2];
        options[0] = new TrackOption("Loopity Loop", AssetManager.loadTexture("/tracks/loopity.png"), new TrackCircleLoop(20, 20, true));
        options[1] = new TrackOption("Kinda Square", AssetManager.loadTexture("/tracks/square.png"), new TrackLoopMutation2(20, 20));
        return options;
    }

    private void startGame(TrackGenerator generator) {
        this.startGame.accept(generator);
        menu.navigationPop();
    }


}
