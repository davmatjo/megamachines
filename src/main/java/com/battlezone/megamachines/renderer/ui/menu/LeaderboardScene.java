package com.battlezone.megamachines.renderer.ui.menu;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.renderer.ui.elements.Box;

import java.util.List;

public class LeaderboardScene extends MenuScene {

    private BaseMenu menu;
    private List<RWDCar> cars;

    public LeaderboardScene(BaseMenu menu, Vector4f primaryColor, Vector4f secondaryColor, Box background, List<RWDCar> cars) {
        super(primaryColor, secondaryColor, background);
        this.menu = menu;
        this.cars = cars;

        var boxTop = getButtonY(0.5f);
        var boxBottom = getButtonY(-2f);
        var buttonHeight = Math.abs(boxTop - boxBottom);
        var leaderboard = new Leaderboard(BUTTON_X, (boxTop + boxBottom) / 2f, BUTTON_WIDTH, buttonHeight, cars, getPrimaryColor(), getSecondaryColor());
        addElement(leaderboard);

        addLabel("LEADERBOARD", 2, 0.7f, Colour.WHITE);

        addButton("BACK", -2, menu::navigationPop);
    }

}
