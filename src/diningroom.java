import org.jogamp.java3d.*;
import org.jogamp.java3d.loaders.Scene;
import org.jogamp.java3d.loaders.objectfile.ObjectFile;
import org.jogamp.java3d.utils.universe.*;
import org.jogamp.vecmath.*;

import java.awt.*;
import javax.swing.*;

public class diningroom {

    public static void main(String[] args) {
        // 1. Setup 3D window
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas = new Canvas3D(config);

        JFrame frame = new JFrame("Dining Room with Textured Chair");
        frame.setLayout(new BorderLayout());
        frame.add(canvas, BorderLayout.CENTER);
        frame.setSize(1280, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // 2. Configure 3D universe
        SimpleUniverse universe = new SimpleUniverse(canvas);
        universe.getViewingPlatform().setNominalViewingTransform();

        // 3. Create scene root
        BranchGroup scene = new BranchGroup();

        // 4. Load and display chair with texture
        addTexturedChair(scene, "room2/Chair1.obj", "room2/textures/Chair_BaseColor.png");

        // 5. Add lighting
        addLighting(scene);

        // 6. Finalize scene
        scene.compile();
        universe.addBranchGraph(scene);
    }

    private static void addTexturedChair(BranchGroup scene, String objPath, String texturePath) {
        try {
            // Load .obj model
            ObjectFile objFile = new ObjectFile(ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY);
            Scene objScene = objFile.load(objPath);
            BranchGroup objGroup = objScene.getSceneGroup();

            // Apply texture to the chair
            Appearance chairAppearance = TextureLoader.loadTextureAppearance(texturePath);
            applyAppearanceToGroup(objGroup, chairAppearance);

            scene.addChild(objGroup);
        } catch (Exception e) {
            System.err.println("Failed to load chair: " + e.getMessage());
        }
    }

    private static void applyAppearanceToGroup(Group group, Appearance app) {
        for (int i = 0; i < group.numChildren(); i++) {
            Node child = group.getChild(i);
            if (child instanceof Shape3D) {
                ((Shape3D) child).setAppearance(app);
            } else if (child instanceof Group) {
                applyAppearanceToGroup((Group) child, app);
            }
        }
    }

    private static void addLighting(BranchGroup scene) {
        BoundingSphere bounds = new BoundingSphere();

        // Ambient light
        AmbientLight ambient = new AmbientLight(new Color3f(0.5f, 0.5f, 0.5f));
        ambient.setInfluencingBounds(bounds);
        scene.addChild(ambient);

        // Directional light
        DirectionalLight light = new DirectionalLight(
                new Color3f(1f, 1f, 1f),
                new Vector3f(-1f, -1f, -1f)
        );
        light.setInfluencingBounds(bounds);
        scene.addChild(light);
    }
}



