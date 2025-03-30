package maze2d;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.JPanel;

import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.image.ImageException;
import org.jogamp.vecmath.*;

import maze2d.MazeGame;

public class TVPuzzle extends Behavior {
    private final TransformGroup tvScreen;
    private final MazeGame mazeGame;
    private final Canvas mazeCanvas;
    private boolean isActive = false;
    private BufferedImage bufferImage;
    private Graphics2D bufferGraphics;

    public TVPuzzle(TransformGroup tvScreen) {
        this.tvScreen = tvScreen;
        
        // Create the maze game
        mazeGame = new MazeGame();
        mazeCanvas = (Canvas)mazeGame.getContentPane().getComponent(0);
        mazeCanvas.setPreferredSize(new Dimension(400, 300));
        
        // Create buffer for rendering
        bufferImage = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
        bufferGraphics = bufferImage.createGraphics();
        
        // Set up behavior triggers
        setSchedulingBounds(new BoundingSphere(new Point3d(), 100.0));
    }

    @Override
    public void initialize() {
        wakeupOn(new WakeupOnElapsedFrames(0));
    }

    @Override
    public void processStimulus(Iterator<WakeupCriterion> criteria) {
        if (isActive) {
            // Update the maze game display
            renderToTexture();
        }
        wakeupOn(new WakeupOnElapsedFrames(0));
    }

    private void renderToTexture() {
        // Render the maze to our buffer
        mazeCanvas.paint(bufferGraphics);
        
        // Update the texture
        Appearance app = new Appearance();
        Texture2D texture = new Texture2D(Texture.BASE_LEVEL, Texture.RGB, 400, 300);
        texture.setImage(0, new ImageComponent2D(ImageComponent2D.FORMAT_RGB, bufferImage));
        texture.setEnable(true);
        app.setTexture(texture);
        
        // Get the screen shape from the TV (assuming it's the first child)
        Shape3D screenShape = (Shape3D)tvScreen.getChild(0);
        screenShape.setAppearance(app);
    }

    public void activate() {
        isActive = true;
        renderToTexture();
    }

    public void deactivate() {
        isActive = false;
    }
}