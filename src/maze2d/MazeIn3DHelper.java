package maze2d;

import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.vecmath.*;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Helper class for converting the 2D maze into a texture
 * and creating a 3D shape ("wall") for it.
 */
public class MazeIn3DHelper {

    /**
     * 1) Renders your existing 2D MazePanel (or any Swing component)
     *    into a BufferedImage of the given size.
     */
    public static BufferedImage createMazeImage(MazePanel mazePanel, int width, int height) {
        // Set the MazePanel size so paint() uses these dimensions
        mazePanel.setSize(width, height);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // Let the MazePanel paint itself onto the image
        mazePanel.paint(g2d);

        g2d.dispose();
        return image;
    }

    /**
     * 2) Converts a BufferedImage into a Java3D Appearance (texture).
     */
    public static Appearance createMazeAppearance(BufferedImage mazeImage) {
        TextureLoader loader = new TextureLoader(mazeImage, "RGBA", null);
        Texture texture = loader.getTexture();
        if (texture == null) {
            // If somehow the image couldn't become a texture, return a default
            return new Appearance();
        }

        // Configure texture behavior
        texture.setBoundaryModeS(Texture.WRAP);
        texture.setBoundaryModeT(Texture.WRAP);
        texture.setMinFilter(Texture.BASE_LEVEL_LINEAR);
        texture.setMagFilter(Texture.BASE_LEVEL_LINEAR);

        // Basic material for lighting
        Material mat = new Material();
        mat.setAmbientColor(new Color3f(0.5f, 0.5f, 0.5f));
        mat.setDiffuseColor(new Color3f(1f, 1f, 1f));
        mat.setSpecularColor(new Color3f(0.2f, 0.2f, 0.2f));
        mat.setShininess(32f);
        mat.setLightingEnable(true);

        // Texture attributes
        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE);

        Appearance app = new Appearance();
        app.setMaterial(mat);
        app.setTexture(texture);
        app.setTextureAttributes(texAttr);

        return app;
    }

    /**
     * 3) Creates a thin Box with the given Appearance (the "screen"),
     *    positions it, and returns a TransformGroup to be added to your scene.
     */
    public static TransformGroup createMazeWall(Appearance mazeApp,
                                                float width, float height,
                                                Vector3d position,
                                                AxisAngle4d rotation) {
        // The Box constructor uses half-width, half-height, half-depth
        Box mazeWall = new Box(width / 2, height / 2, 0.001f,
                               Box.GENERATE_TEXTURE_COORDS, mazeApp);

        // Position & rotate
        Transform3D transform = new Transform3D();
        transform.setRotation(rotation);
        transform.setTranslation(position);

        TransformGroup tg = new TransformGroup(transform);
        tg.addChild(mazeWall);
        return tg;
    }
}
