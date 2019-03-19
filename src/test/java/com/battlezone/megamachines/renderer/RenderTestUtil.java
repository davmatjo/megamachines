package com.battlezone.megamachines.renderer;

import com.battlezone.megamachines.util.AssetManager;
import static org.junit.Assert.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class RenderTestUtil {

    public static void rendersAreEqual(String expectedFile, ByteBuffer actualBytes) {
        File file = new File(expectedFile);
        try {
            BufferedImage expected = ImageIO.read(file);
            BufferedImage actual = AssetManager.imageFromBytes(actualBytes, 1920, 1080);

            assertEquals(expected.getHeight(), actual.getHeight());
            assertEquals(expected.getWidth(), actual.getWidth());

            int width  = expected.getWidth();
            int height = expected.getHeight();

            // Loop over every pixel.
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // Compare the pixels for equality.
                    try {
                        assertEquals(expected.getRGB(x, y), actual.getRGB(x, y));
                    } catch (AssertionError e) {
                        AssetManager.saveImage(actual, expectedFile + "-failed.bmp");
                        throw e;
                    }
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("could not read expected image");
        }
    }
}
