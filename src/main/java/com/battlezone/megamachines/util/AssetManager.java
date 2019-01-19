package com.battlezone.megamachines.util;

import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.Texture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AssetManager {

    public static Texture loadTexture(String path) {

        try {
            BufferedImage texture = ImageIO.read(AssetManager.class.getResource(path));
            int width = texture.getWidth();
            int height = texture.getHeight();
            return new Texture(texture.getRGB(0, 0, width, height, null, 0, width),
                    width,
                    height);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Shader loadShader(String path) {
        return new Shader(readFile(path + ".vert"), readFile(path + ".frag"));
    }

    private static String readFile(String path) {
        try {
            Path file = Paths.get(AssetManager.class.getResource(path).toURI());
            byte[] bytes = Files.readAllBytes(file);
            return new String(bytes);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
