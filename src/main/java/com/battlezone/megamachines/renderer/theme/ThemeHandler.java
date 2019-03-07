package com.battlezone.megamachines.renderer.theme;

public class ThemeHandler {

    private static Theme currentTheme = Theme.SPACE;

    public static void setTheme(Theme theme) {
        currentTheme = theme;
    }

    public static Theme getTheme() {
        return currentTheme;
    }

}
