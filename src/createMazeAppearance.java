import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.java3d.Texture;
import org.jogamp.java3d.TextureAttributes;
import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.Material;
import org.jogamp.vecmath.Color3f;

import java.awt.image.BufferedImage;

public static Appearance createMazeAppearance(BufferedImage mazeImage) {
    // Convert the BufferedImage into a Texture
    TextureLoader loader = new TextureLoader(mazeImage, "RGBA", null);
    Texture texture = loader.getTexture();
    if (texture == null) {
        // If loading failed, return a default gray Appearance
        return new Appearance();
    }

    // Configure texture
    texture.setBoundaryModeS(Texture.WRAP);
    texture.setBoundaryModeT(Texture.WRAP);
    texture.setMinFilter(Texture.BASE_LEVEL_LINEAR);
    texture.setMagFilter(Texture.BASE_LEVEL_LINEAR);

    // Set up material (lighting)
    Material mat = new Material();
    mat.setAmbientColor(new Color3f(0.5f, 0.5f, 0.5f));
    mat.setDiffuseColor(new Color3f(1f, 1f, 1f));
    mat.setSpecularColor(new Color3f(0.2f, 0.2f, 0.2f));
    mat.setShininess(32f);
    mat.setLightingEnable(true);

    // Set up texture attributes
    TextureAttributes texAttr = new TextureAttributes();
    texAttr.setTextureMode(TextureAttributes.MODULATE);

    // Put it all together
    Appearance appearance = new Appearance();
    appearance.setMaterial(mat);
    appearance.setTexture(texture);
    appearance.setTextureAttributes(texAttr);

    return appearance;
}
