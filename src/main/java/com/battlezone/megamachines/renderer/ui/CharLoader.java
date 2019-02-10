package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.math.MathUtils;
import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.renderer.SubTexture;

import java.util.HashMap;

/**
 * A class to hold the SubTextures for characters in.
 *
 * @author Kieran
 * @see SubTexture
 */
public class CharLoader {

    private final static HashMap<Character, SubTexture> mappings = new HashMap<>();
    private static final int CHARACTER_COUNT = 40;
    private static final Matrix4f charMatrix = Matrix4f.scale(1f / CHARACTER_COUNT, 1f, 1f, new Matrix4f());
    private static final SubTexture SPACE;

    static {

        final char[] font = "ABCDEFGHIJKLMNOPQRSTUVWXYZ.!? 0123456789".toCharArray();

        for (int i = 0; i < font.length; i++)
            mappings.put(font[i], new SubTexture(Matrix4f.translate(charMatrix, (float) i, 0f, 0, new Matrix4f())));

        SPACE = mappings.get(' ');
    }

    public static SubTexture get(Character c) {
        // Convert to uppercase
        if (MathUtils.inRange((int) c, 'a', 'z'))
            return mappings.getOrDefault((char) (c - 32), SPACE);
        else
            return mappings.getOrDefault(c, SPACE);
    }

}
