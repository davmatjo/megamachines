package com.battlezone.megamachines.util;

import com.battlezone.megamachines.math.MathUtils;
import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.renderer.AnimatedTexture;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.StaticTexture;
import com.battlezone.megamachines.renderer.SubTexture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetManager {

    private static boolean isHeadless = true;

    // Font assets
    private final static HashMap<Character, SubTexture> mappings = new HashMap<>();
    private static final int CHARACTER_COUNT = 40;
    private static final Matrix4f charMatrix = Matrix4f.scale(1f / CHARACTER_COUNT, 1f, 1f, new Matrix4f());
    private static final SubTexture SPACE;

    static {
        // Process font assets
        final char[] font = "ABCDEFGHIJKLMNOPQRSTUVWXYZ.!? 0123456789".toCharArray();
        for (int i = 0; i < font.length; i++)
            mappings.put(font[i], new SubTexture(Matrix4f.translate(charMatrix, (float) i, 0f, 0, new Matrix4f())));
        SPACE = mappings.get(' ');
    }

    public static StaticTexture loadTexture(String path) {
        if (!isHeadless) {
            try {
                BufferedImage texture = ImageIO.read(AssetManager.class.getResource(path));
                return getStaticTexture(texture);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public static StaticTexture loadTexture(BufferedImage texture) {
        try {
            File outputfile = new File("saved.png");
            ImageIO.write(texture, "png", outputfile);
        } catch (IOException e) {
            // handle exception
        }
        return getStaticTexture(texture);

    }

    private static StaticTexture getStaticTexture(BufferedImage texture) {
        int width = texture.getWidth();
        int height = texture.getHeight();
        return new StaticTexture(texture.getRGB(0, 0, width, height, null, 0, width),
                width,
                height);
    }

    public static AnimatedTexture loadAnimation(String path, int frameCount, int speed) {
        List<StaticTexture> textures = new ArrayList<>();
        for (int i = 1; i <= frameCount; i++) {
            textures.add(AssetManager.loadTexture(path + i + ".png"));
        }
        return new AnimatedTexture(textures, speed);
    }

    public static Shader loadShader(String path) {
        return new Shader(readFile(path + ".vert"), readFile(path + ".frag"));
    }

    private static String readFile(String path) {
        try {
            URI uri = AssetManager.class.getResource(path).toURI();
            initFileSystem(uri);
            Path file = Paths.get(AssetManager.class.getResource(path).toURI());
            byte[] bytes = Files.readAllBytes(file);
            return new String(bytes);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void initFileSystem(URI uri) throws IOException {
        try {
            Paths.get(uri);
        } catch (FileSystemNotFoundException e) {
            Map<String, String> env = new HashMap<>();
            env.put("create", "true");
            FileSystems.newFileSystem(uri, env);
        }
    }

    public static SubTexture getChar(Character c) {
        // Convert to uppercase
        if (MathUtils.inRange((int) c, 'a', 'z'))
            return mappings.getOrDefault((char) (c - 32), SPACE);
        else
            return mappings.getOrDefault(c, SPACE);
    }

    public static void setIsHeadless(boolean isHeadless) {
        AssetManager.isHeadless = isHeadless;
    }

}
