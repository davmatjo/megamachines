package com.battlezone.megamachines.renderer.ui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Deprecated
public class FontConvertor {

    public static void toPNG(String filename) {
        BufferedImage image = new BufferedImage(750, 150, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = image.getGraphics();

        Font f = new Font(Font.MONOSPACED, Font.PLAIN, 48);

        g.setFont(f);
        g.setColor(Color.BLACK);
        g.drawString("abdcefghijklmnopqrstuvwxyz", 0, g.getFontMetrics().getAscent());
        g.drawString("ABCDEFGHIJKLMNOPQRSTUVWXYZ", 0, g.getFontMetrics().getAscent() * 2);
        g.drawString(" 1234567890?!", 0, g.getFontMetrics().getAscent() * 3);


        File output = new File(filename);
        try {
            ImageIO.write(image, "png", output);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
