import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.*;

public class Main {
    public static void main(String[] args) {
        // Configure the 3D view
        SimpleUniverse universe = new SimpleUniverse();
        universe.getViewingPlatform().setNominalViewingTransform();
        
        // Create scene with chair
        BranchGroup scene = createScene();
        
        // Add scene to universe
        universe.addBranchGraph(scene);
    }
    
    private static BranchGroup createScene() {
        BranchGroup scene = new BranchGroup();
        
        // Create chair with default material colors
        CreateObjects creator = new CreateObjects();
        TransformGroup chair = creator.createObject(
            "ChairOld",                          // OBJ filename (without extension)
            new AxisAngle4d(0, 1, 0, 0),        // No rotation
            new Vector3d(0, -0.5, 0),           // Position (lowered slightly)
            0.5                                 // Scale
        );
        
        scene.addChild(chair);
        scene.addChild(createLights());  // Add lighting
        
        return scene;
    }
    
    private static BranchGroup createLights() {
        BranchGroup lightGroup = new BranchGroup();
        
        // Ambient light (base illumination)
        AmbientLight ambient = new AmbientLight(
            new Color3f(0.3f, 0.3f, 0.3f));
        ambient.setInfluencingBounds(new BoundingSphere(new Point3d(), 100.0));
        
        // Directional light (sun-like)
        DirectionalLight directional = new DirectionalLight(
            new Color3f(0.7f, 0.7f, 0.7f),
            new Vector3f(-1f, -1f, -1f));
        directional.setInfluencingBounds(new BoundingSphere(new Point3d(), 100.0));
        
        lightGroup.addChild(ambient);
        lightGroup.addChild(directional);
        
        return lightGroup;
    }
}