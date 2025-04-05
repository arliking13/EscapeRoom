import org.jogamp.java3d.*;
import org.jogamp.vecmath.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.util.Iterator;
import org.jogamp.java3d.WakeupCriterion;


public class CollisionPreventionBehavior extends Behavior {
    private final TransformGroup viewTransformGroup;
    private final WakeupOnElapsedFrames wakeup;
    private final BufferedImage collisionMap;
    private final int imageWidth;
    private final int imageHeight;
    private final float worldWidth = 8.0f;   // slightly larger than your scene
    private final float worldHeight = 8.0f;  // slightly larger than your scene


    private final float cellSizeX;
    private final float cellSizeY;

 // Match initial good position with spawn point
    private Vector3f previousGoodPosition = new Vector3f(-1.0f, 0.0f, -1.0f);


 // Option A: Use a local variable, then one assignment at the end
    public CollisionPreventionBehavior(TransformGroup viewTransformGroup, String collisionImagePath) {
        this.viewTransformGroup = viewTransformGroup;
        this.wakeup = new WakeupOnElapsedFrames(0);

        BufferedImage temp = null;
        try {
            temp = ImageIO.read(new File(collisionImagePath));
            if (temp != null) {
                System.out.println("✅ Loaded collision map: " + collisionImagePath + 
                                   " (" + temp.getWidth() + "x" + temp.getHeight() + ")");
            } else {
                System.err.println("❌ Failed to load collision map: " + collisionImagePath);
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to load collision map: " + e.getMessage());
        }

        // If loading failed, create fallback once
        if (temp == null) {
            temp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
            temp.setRGB(0, 0, 0xFFFFFF);
            System.err.println("⚠️ Using fallback collision map (all white)");
            imageWidth = imageHeight = 1;
            cellSizeX = cellSizeY = 1;
        } else {
            imageWidth = temp.getWidth();
            imageHeight = temp.getHeight();
            cellSizeX = worldWidth / imageWidth;
            cellSizeY = worldHeight / imageHeight;
        }

        // FINAL field assigned exactly once
        this.collisionMap = temp;
    }

    @Override
    public void initialize() {
        wakeupOn(wakeup);
    }

    @Override
    public void processStimulus(Iterator<WakeupCriterion> criteria) {
        Transform3D t3d = new Transform3D();
        viewTransformGroup.getTransform(t3d);
        Vector3f currentPos = new Vector3f();
        t3d.get(currentPos);

        float checkX = currentPos.x + worldWidth / 2f;
        float checkZ = currentPos.z + worldHeight / 2f;

        int col = (int) (checkX / cellSizeX);
        int row = imageHeight - 1 - (int) (checkZ / cellSizeY);  // flip vertically

        boolean isBlocked = false;
        String reason = "";

        // Check bounds first
        if (col < 0 || col >= imageWidth || row < 0 || row >= imageHeight) {
            isBlocked = true;
            reason = "Out of bounds";
        } else {
            // Color check
            int rgb = collisionMap.getRGB(col, row);
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = rgb & 0xFF;
            int avg = (r + g + b) / 3;

            // Only allow white (or near white) pixels as walkable
            isBlocked = avg < 200;
            if (isBlocked) {
                reason = "Blocked pixel: RGB(" + r + "," + g + "," + b + ")";
            }
        }

        if (isBlocked) {
            // Only log occasionally to avoid spamming the console
            if (Math.random() < 0.01) {
                System.out.println("🛑 Collision at [" + col + "," + row + "]: " + reason + 
                                  " Position: (" + currentPos.x + "," + currentPos.y + "," + currentPos.z + ")");
            }
            t3d.setTranslation(previousGoodPosition);
            viewTransformGroup.setTransform(t3d);
        } else {
            previousGoodPosition.set(currentPos);
        }

        wakeupOn(wakeup);
    }
    
}