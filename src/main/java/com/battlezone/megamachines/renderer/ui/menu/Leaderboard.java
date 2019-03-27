package com.battlezone.megamachines.renderer.ui.menu;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.Renderable;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.renderer.ui.elements.Box;
import com.battlezone.megamachines.renderer.ui.elements.Label;
import com.battlezone.megamachines.util.AssetManager;

import java.util.Comparator;
import java.util.List;

public class Leaderboard implements Renderable {

    private float x, y, width, height;

    private List<RWDCar> cars;

    private Vector4f primaryColor, secondaryColor;

    private Label[] positionLabels = new Label[8];
    private Label[] nameLabels = new Label[8];
    private Box[] boxes = new Box[8];

    private Box background;

    public Leaderboard(float x, float y, float width, float height, List<RWDCar> cars, Vector4f primaryColor, Vector4f secondaryColor) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;

        cars.sort(Comparator.comparingInt(RWDCar::getPosition).reversed());
        this.cars = cars;

        init();
    }

    private void init() {
        var boxTop = y;
        var boxBottom = y + height;
        var buttonWidth = height / 8;

        var boxSize = Math.min(buttonWidth, height);
        var padding = boxSize * 0.1f;
        boxSize = boxSize * 0.9f;

        var width = 2.5f;
        var x = -width / 2;

        for (int i = 0; i < 8; i += 1) {
            final RWDCar car = cars.get(i);
            Texture t = AssetManager.loadTexture("/cars/car" + car.getModelNumber() + ".png");

            //cars are 20*44 pixels so we need to adjust aspect
            var box = new Box(boxSize / 20 * 44, boxSize, x + width - 2 * boxSize - 0.05f, boxTop + padding + (boxSize + padding) * (i), car.getColour(), t);
            boxes[i] = box;

            Label name = new Label(car.getName(), boxSize, -0.7f, boxTop + padding + (boxSize + padding) * (i), Colour.WHITE);
            nameLabels[i] = name;

            Label l = new Label(textForPosition(car.getPosition()), boxSize, 0, 0, Colour.WHITE);
            var labelWidth = l.getWidth();
            l.setPos(x + boxSize / 2, boxTop + padding + (boxSize + padding) * (i));
            positionLabels[i] = l;
        }

        background = new Box(width, this.height + padding * 2, x, this.y - padding, new Vector4f(0f, 0f, 0f, 0.3f));
    }

    private String textForPosition(byte position) {
        int pos = position + 1;
        String[] suffixes = {"st", "nd", "rd"};
        if (pos < 4) {
            return pos + suffixes[pos - 1];
        }
        return pos + "th";
    }


    public Vector4f getPrimaryColor() {
        return primaryColor;
    }

    public Vector4f getSecondaryColor() {
        return secondaryColor;
    }

    @Override
    public void render() {
        background.render();
        for (int i = 0; i < boxes.length; i++) {
            Shader.STATIC.setMatrix4f("texturePosition", Matrix4f.IDENTITY);
            boxes[i].render();
            Shader.STATIC.setMatrix4f("texturePosition", Matrix4f.IDENTITY);
            positionLabels[i].render();
            Shader.STATIC.setMatrix4f("texturePosition", Matrix4f.IDENTITY);
            nameLabels[i].render();
        }
    }

    @Override
    public Shader getShader() {
        return null;
    }

    @Override
    public void delete() {

    }
}
