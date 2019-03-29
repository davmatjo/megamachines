package com.battlezone.megamachines.util;

import com.battlezone.megamachines.math.MathUtils;
import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.renderer.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetManager {

    // Font assets
    private final static HashMap<Character, SubTexture> mappings = new HashMap<>();
    private static final char[] font = "ABCDEFGHIJKLMNOPQRSTUVWXYZ.!?:_-/ 0123456789".toCharArray();
    private static final int CHARACTER_COUNT = font.length;
    private static final Matrix4f charMatrix = Matrix4f.scale(1f / CHARACTER_COUNT, 1f, 1f, new Matrix4f());
    private static final SubTexture SPACE;
    private static final Map<String, Texture> textureCache = new HashMap<>();
    private static boolean isHeadless = true;

    static {
        // Process font assets
        for (int i = 0; i < CHARACTER_COUNT; i++)
            mappings.put(font[i], new SubTexture(Matrix4f.translate(charMatrix, (float) i, 0f, 0, new Matrix4f())));
        SPACE = mappings.get(' ');
    }

    /**
     * Loads a texture from a file
     *
     * @param path Path to texture
     * @return A ready to use texture
     */
    public static Texture loadTexture(String path) {
        if (!isHeadless) {
            if (!textureCache.containsKey(path)) {
                try {
                    BufferedImage image = ImageIO.read(AssetManager.class.getResource(path));
                    Texture texture = getStaticTexture(image);
                    textureCache.put(path, texture);
                    return texture;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                return textureCache.get(path);
            }
        } else {
            return null;
        }
    }

    /**
     * Loads a texture from a BufferedImage
     *
     * @param texture BufferedImage to create the texture
     * @return A usable texture
     */
    public static StaticTexture loadTexture(BufferedImage texture) {
        try {
            File outputfile = new File("saved.png");
            ImageIO.write(texture, "png", outputfile);
        } catch (IOException e) {
            // handle exception
        }
        return getStaticTexture(texture);

    }

    // This method does the creation of the actual texture from a buffered image
    private static StaticTexture getStaticTexture(BufferedImage texture) {
        int width = texture.getWidth();
        int height = texture.getHeight();
        return new StaticTexture(texture.getRGB(0, 0, width, height, null, 0, width),
                width,
                height);
    }

    /**
     * Load an animation from a series of files
     *
     * @param path       Path to animation
     * @param frameCount Number of frames in the animation
     * @param speed      Speed that the animation should play in frames per second
     * @param loop       Whether this animation should loop
     * @return A usable AnimatedTexture
     */
    public static AnimatedTexture loadAnimation(String path, int frameCount, int speed, boolean loop) {
        List<Texture> textures = new ArrayList<>();
        for (int i = 1; i <= frameCount; i++) {
            textures.add(AssetManager.loadTexture(path + i + ".png"));
        }
        return new AnimatedTexture(textures, speed, loop);
    }

    /**
     * Loads the vertex shader and fragmentation shader from a specific path
     *
     * @param path     Location of the shaders (without te extension)
     * @param priority The render priority of the shader
     * @return A compiled shader
     */
    public static Shader loadShader(String path, int priority) {
        return new Shader(readFile(path + ".vert"), readFile(path + ".frag"), priority);
    }

    /**
     * Creates a BufferedImage from a ByteBuffer
     *
     * @param imageBytes The data of the image
     * @param width      width of the image
     * @param height     height of the image
     * @return A buffered image from the data
     */
    public static BufferedImage imageFromBytes(ByteBuffer imageBytes, int width, int height) {

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int i = (x + (width * y)) * 4;
                int r = imageBytes.get(i) & 0xFF;
                int g = imageBytes.get(i + 1) & 0xFF;
                int b = imageBytes.get(i + 2) & 0xFF;
                image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
            }
        }

        return image;
    }

    /**
     * Saves a buffered image to a given path
     *
     * @param image The image to save
     * @param path  the path to save the image to
     */
    public static void saveImage(BufferedImage image, String path) {
        try {
            File file = new File(path);
            ImageIO.write(image, "bmp", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Reads a text file
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

    // Compatibility for different filesystems
    private static void initFileSystem(URI uri) throws IOException {
        try {
            Paths.get(uri);
        } catch (FileSystemNotFoundException e) {
            Map<String, String> env = new HashMap<>();
            env.put("create", "true");
            FileSystems.newFileSystem(uri, env);
        }
    }

    /**
     * Return the portion of a texture for a given character
     *
     * @param c The character needed
     * @return The character as a texture
     */
    public static SubTexture getChar(Character c) {
        // Convert to uppercase
        if (MathUtils.inRange((int) c, 'a', 'z'))
            return mappings.getOrDefault((char) (c - 32), SPACE);
        else
            return mappings.getOrDefault(c, SPACE);
    }

    /**
     * @param isHeadless Whether we are going to be rendering things
     */
    public static void setIsHeadless(boolean isHeadless) {
        AssetManager.isHeadless = isHeadless;
    }

    /**
     * @return Whether we are going to be rendering things
     */
    public static boolean isHeadless() {
        return isHeadless;
    }
}
