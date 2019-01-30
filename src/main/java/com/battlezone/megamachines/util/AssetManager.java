package com.battlezone.megamachines.util;

import com.battlezone.megamachines.renderer.AnimatedTexture;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.StaticTexture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetManager {

    public static StaticTexture loadTexture(String path) {

        try {
            BufferedImage texture = ImageIO.read(AssetManager.class.getResource(path));
            int width = texture.getWidth();
            int height = texture.getHeight();
            return new StaticTexture(texture.getRGB(0, 0, width, height, null, 0, width),
                    width,
                    height);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static AnimatedTexture loadAnimation(String path, int frameCount, int speed) {
        List<StaticTexture> textures = new ArrayList<>();
        for (int i=1; i <= frameCount; i++) {
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

    private static void initFileSystem(URI uri) throws IOException
    {
        try
        {
            Paths.get(uri);
        }
        catch( FileSystemNotFoundException e )
        {
            Map<String, String> env = new HashMap<>();
            env.put("create", "true");
            FileSystems.newFileSystem(uri, env);
        }
    }
}
