package com.battlezone.megamachines.renderer.ui.menu;

import com.battlezone.megamachines.renderer.Texture;

public class ListItem {
    private String name;
    private Texture texture;

    public ListItem(String name, Texture texture) {
        this.name = name;
        this.texture = texture;
    }

    public String getName() {
        return name;
    }

    public Texture getTexture() {
        return texture;
    }
}