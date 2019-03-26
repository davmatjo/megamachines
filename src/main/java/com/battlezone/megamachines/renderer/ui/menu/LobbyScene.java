package com.battlezone.megamachines.renderer.ui.menu;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.networking.server.Server;
import com.battlezone.megamachines.renderer.theme.Theme;
import com.battlezone.megamachines.renderer.ui.elements.Box;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.util.Triple;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.generator.TrackLoopMutation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LobbyScene extends MenuScene {

    private static final float PLAYER_AVATAR_WIDTH = 0.4f;
    private static final float PLAYER_AVATER_HEIGHT = 0.2f;
    private static final float PLAYER_AVATAR_POSITION_OFFSET = 0.5f;
    private static final float PLAYER_AVATAR_X = -1f;
    private static final float PLAYER_AVATAR_Y_TOP = 0.5f;
    private static final float PLAYER_AVATAR_Y_BOTTOM = 0f;

    private Triple<Track, Theme, Integer> options;

    private ArrayList<Box> playerModels;

    private BaseMenu menu;
    private TrackSelectionScene trackSelectionScene;
    private Consumer<Triple<Track, Theme, Integer>> start;

    public LobbyScene(BaseMenu menu, Vector4f primaryColor, Vector4f secondaryColor, Box background, Consumer<Triple<Track, Theme, Integer>> start, Runnable quit) {
        super(primaryColor, secondaryColor, background);
        this.playerModels = new ArrayList<>();
        this.start = start;
        this.menu = menu;
        this.trackSelectionScene = new TrackSelectionScene(menu, primaryColor, secondaryColor, background, (options) -> this.options = options);
        addButton("QUIT", -2, quit);
    }

    private Triple<Track, Theme, Integer> getOptions() {
        if (options == null) {
            return new Triple(new TrackLoopMutation(20, 20).generateTrack(), Theme.DEFAULT, 3);
        }
        return this.options;
    }

    public void setupHost() {
        this.addButton("START", -1, () -> this.start.accept(getOptions()), 1, 2);
        this.addButton("TRACK", -1, () -> menu.navigationPush(trackSelectionScene), 2, 2);
    }

    public void setPlayerModels(List<RWDCar> players) {
        this.playerModels.forEach(Box::delete);
        this.playerModels.forEach(this::removeElement);
        this.playerModels.clear();
        for (int i = 0; i < players.size(); i++) {
            this.playerModels.add(
                    new Box(
                            PLAYER_AVATAR_WIDTH,
                            PLAYER_AVATER_HEIGHT,
                            PLAYER_AVATAR_X + (i % (int) Math.ceil((Server.MAX_PLAYERS / 2.0))) * PLAYER_AVATAR_POSITION_OFFSET,
                            i >= Math.ceil(Server.MAX_PLAYERS / 2.0) ? PLAYER_AVATAR_Y_BOTTOM : PLAYER_AVATAR_Y_TOP,
                            players.get(i).getColour(),
                            AssetManager.loadTexture("/cars/car" + players.get(i).getModelNumber() + ".png")));
        }
        this.playerModels.forEach(this::addElement);
    }

    public void showLeaderboard(List<RWDCar> cars) {
        var leaderboardScene = new LeaderboardScene(menu, getPrimaryColor(), getSecondaryColor(), getBackground(), cars);
        menu.navigationPush(leaderboardScene);
    }


}
