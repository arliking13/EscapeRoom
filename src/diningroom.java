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
        
        JFrame frame = new JFrame("Dining Room - Wireframe Mode");
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
        
        // 4. Load all objects with wireframe appearance
        Appearance wireframeApp = createWireframeAppearance();
        
        // Room structure
        addObject(scene, "room2", new Vector3d(0,0,0), wireframeApp);
        
        // Main furniture
        addObject(scene, "Table_Books_Carpet", new Vector3d(0,0,0), wireframeApp);
        addObject(scene, "Cabinet", new Vector3d(-3.5,0,0), wireframeApp);
        
        // Chairs
        addObject(scene, "Chair1", new Vector3d(0,0,-1.8), wireframeApp);
        addObject(scene, "Chair1", new Vector3d(0,0,1.8), wireframeApp);
        addObject(scene, "Chair2", new Vector3d(-1.8,0,0), wireframeApp);
        addObject(scene, "Chair2", new Vector3d(1.8,0,0), wireframeApp);
        
        // Decorations
        addObject(scene, "FrameArt1", new Vector3d(-4,1.8,0), wireframeApp);
        addObject(scene, "FrameArt2", new Vector3d(4,1.8,0), wireframeApp);

        // 5. Add basic lighting
        AmbientLight ambient = new AmbientLight(new Color3f(0.5f, 0.5f, 0.5f));
        ambient.setInfluencingBounds(new BoundingSphere());
        scene.addChild(ambient);

        DirectionalLight light = new DirectionalLight(
            new Color3f(0.8f, 0.8f, 0.8f),
            new Vector3f(-1f, -1f, -1f));
        light.setInfluencingBounds(new BoundingSphere());
        scene.addChild(light);

        // 6. Finalize scene
        universe.addBranchGraph(scene);
    }

    private static void addObject(BranchGroup scene, String objName, Vector3d position, Appearance app) {
        try {
            ObjectFile objFile = new ObjectFile(ObjectFile.RESIZE);
            Scene objScene = objFile.load("room2/" + objName + ".obj");
            BranchGroup objGroup = objScene.getSceneGroup();
            
            // Apply wireframe appearance to all shapes
            setAppearance(objGroup, app);
            
            // Position the object
            TransformGroup tg = new TransformGroup();
            Transform3D t3d = new Transform3D();
            t3d.setTranslation(position);
            tg.setTransform(t3d);
            tg.addChild(objGroup);
            
            scene.addChild(tg);
            System.out.println("Loaded: " + objName);
        } catch (Exception e) {
            System.err.println("Failed to load " + objName + ": " + e.getMessage());
        }
    }

    private static void setAppearance(Group group, Appearance app) {
        for (int i=0; i<group.numChildren(); i++) {
            Node child = group.getChild(i);
            if (child instanceof Shape3D) {
                ((Shape3D)child).setAppearance(app);
            } else if (child instanceof Group) {
                setAppearance((Group)child, app);
            }
        }
    }

    private static Appearance createWireframeAppearance() {
        Appearance app = new Appearance();
        PolygonAttributes polyAttrs = new PolygonAttributes();
        polyAttrs.setPolygonMode(PolygonAttributes.POLYGON_LINE);
        polyAttrs.setCullFace(PolygonAttributes.CULL_NONE);
        app.setPolygonAttributes(polyAttrs);
        
        Material mat = new Material();
        mat.setDiffuseColor(new Color3f(0.8f, 0.8f, 0.8f));
        app.setMaterial(mat);
        
        return app;
    }
}