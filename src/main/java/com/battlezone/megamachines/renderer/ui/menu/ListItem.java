package com.battlezone.megamachines.renderer.ui.menu;

import com.battlezone.megamachines.renderer.Texture;

/**
 * An item which can show in ScrollingItems. Has a name and a texture (image)
 */
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