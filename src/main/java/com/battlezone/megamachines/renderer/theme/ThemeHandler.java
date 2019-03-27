package com.battlezone.megamachines.renderer.theme;

public class ThemeHandler {

    private static Theme currentTheme = Theme.ICE;

    public static Theme getTheme() {
        return currentTheme;
    }

    public static void setTheme(Theme theme) {
        currentTheme = theme;
    }

}
