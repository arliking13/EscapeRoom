package com.example.labyrinth;

import org.jogamp.java3d.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MazeTextureUpdater {
    private final MazePanel mazePanel;
    private final Texture2D texture;
    private final ImageComponent2D imageComponent;

    public MazeTextureUpdater(MazePanel panel, Texture2D texture) {
        this.mazePanel = panel;
        this.texture = texture;
        this.imageComponent = new ImageComponent2D(ImageComponent.FORMAT_RGBA, captureMazeImage());

        // Enable the capability to allow setting the image later
        imageComponent.setCapability(ImageComponent2D.ALLOW_IMAGE_WRITE);

        texture.setImage(0, imageComponent);

        // Update texture ~30 FPS
        new Timer(33, e -> updateTexture()).start();
    }


    private BufferedImage captureMazeImage() {
        int width = mazePanel.getWidth();
        int height = mazePanel.getHeight();
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        mazePanel.paint(g2d);
        g2d.dispose();
        return img;
    }

    public void updateTexture() {
        BufferedImage img = captureMazeImage();
        imageComponent.set(img);
        texture.setImage(0, imageComponent);
    }
}
