import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.universe.*;
import org.jogamp.vecmath.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        // Create a Canvas3D with larger initial size
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas = new Canvas3D(config);
        canvas.setPreferredSize(new Dimension(1280, 720));
        
        // Create a frame and add the canvas
        JFrame frame = new JFrame("3D Escape Room");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(canvas, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        
        // Configure the 3D view with our custom canvas
        SimpleUniverse universe = new SimpleUniverse(canvas);
        configureViewingPlatform(universe);
        
        // Customize textures before creating scene
        customizeTextures();
        
        // Create scene with all objects
        BranchGroup scene = createScene();
        
        // Add scene to universe
        universe.addBranchGraph(scene);
    }
    
    private static void customizeTextures() {
        // Customize textures for objects here
        LoadObject.setObjectTexture("room3", "rust_walls.jpg");
        LoadObject.setObjectTexture("ChairOld", "wood_planks.jpg");
        LoadObject.setObjectTexture("Desk", "wood.jpg");
        LoadObject.setObjectTexture("Locker", "metal.jpg");
        LoadObject.setObjectTexture("Door", "wood_door.jpg");
        LoadObject.setObjectTexture("Escape_door", "metal_door.jpg");
        LoadObject.setObjectTexture("Window_Casement_Frame", "window_frame.jpg");
        LoadObject.setObjectTexture("Baseboard", "wood_trim.jpg");
        LoadObject.setObjectTexture("Wall_light_left", "light_fixture.jpg");
        LoadObject.setObjectTexture("Wall_light_right", "light_fixture.jpg");
        LoadObject.setObjectTexture("Calling_lamp", "lamp.jpg");
        LoadObject.setObjectTexture("Paper", "paper.jpg");
        LoadObject.setObjectTexture("SwitchMain", "switch.jpg");
        LoadObject.setObjectTexture("SwitchHandle", "metal.jpg");
        LoadObject.setObjectTexture("KeypadDoorLock", "keypad.jpg");
        LoadObject.setObjectTexture("Lockers_door", "metal.jpg");
        LoadObject.setObjectTexture("Cross_left", "Door_Wood_Dif.jpg");
        LoadObject.setObjectTexture("Cross_middle", "Door_Wood_Dif.jpg");
        LoadObject.setObjectTexture("Cross_right", "Door_Wood_Dif.jpg");
        LoadObject.setObjectTexture("The_leftmost_cross", "Door_Wood_Dif.jpg");
        LoadObject.setObjectTexture("The_rightmost_cross", "Door_Wood_Dif.jpg");
    }
    
    private static void configureViewingPlatform(SimpleUniverse universe) {
        ViewingPlatform vp = universe.getViewingPlatform();
        View view = universe.getViewer().getView();
        
        // Configure view parameters
        view.setBackClipDistance(100.0);      // How far you can see
        view.setFrontClipDistance(0.1);       // How close objects can be before disappearing
        view.setFieldOfView(Math.toRadians(45));  // Wider field of view
        
        // Set nominal viewing transform with adjusted distance
        vp.setNominalViewingTransform();
        TransformGroup viewTransform = vp.getViewPlatformTransform();
        Transform3D t3d = new Transform3D();
        viewTransform.getTransform(t3d);
        
        // Move the view back and slightly up for better viewing
        t3d.setTranslation(new Vector3d(0, 1.5, 5));
        viewTransform.setTransform(t3d);
        
        // For proper window resizing behavior
        view.setScreenScalePolicy(View.SCALE_EXPLICIT);
        view.setScreenScale(1.0);
    }
    
    private static BranchGroup createScene() {
        BranchGroup scene = new BranchGroup();
        CreateObjects creator = new CreateObjects();
        
        // Room structure (centered)
        scene.addChild(creator.createObject("room3", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0, 0, 0), 1.0));
        
        // Furniture objects (spread out)
        scene.addChild(creator.createObject("ChairOld", new AxisAngle4d(0, 1, 0, 0), new Vector3d(-1.5, -0.5, -2), 0.5));
        scene.addChild(creator.createObject("Desk", new AxisAngle4d(0, 1, 0, Math.PI/2), new Vector3d(1.5, 0, -1.5), 0.8));
        scene.addChild(creator.createObject("Locker", new AxisAngle4d(0, 1, 0, 0), new Vector3d(-2, 0, 1.5), 0.9));
        
        // Doors and windows
        scene.addChild(creator.createObject("Door", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0, 0, -3.5), 1.0));
        scene.addChild(creator.createObject("Escape_door", new AxisAngle4d(0, 1, 0, Math.PI), new Vector3d(0, 0, 3.5), 1.0));
        scene.addChild(creator.createObject("Window_Casement_Frame", new AxisAngle4d(0, 1, 0, 0), new Vector3d(3.5, 1, 0), 1.0));
        
        // Wall elements
        scene.addChild(creator.createObject("Baseboard", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0, -0.5, 0), 1.0));
        scene.addChild(creator.createObject("Wall_light_left", new AxisAngle4d(0, 1, 0, 0), new Vector3d(-3, 1.8, 0), 0.7));
        scene.addChild(creator.createObject("Wall_light_right", new AxisAngle4d(0, 1, 0, 0), new Vector3d(3, 1.8, 0), 0.7));
        scene.addChild(creator.createObject("Calling_lamp", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0, 2, -3), 0.5));
        
        // Other objects
        scene.addChild(creator.createObject("Paper", new AxisAngle4d(0, 1, 0, 0), new Vector3d(1.2, 0.8, -1.3), 0.2));
        scene.addChild(creator.createObject("SwitchMain", new AxisAngle4d(0, 1, 0, 0), new Vector3d(-3.2, 1.5, 0.1), 0.1));
        scene.addChild(creator.createObject("SwitchHandle", new AxisAngle4d(0, 1, 0, 0), new Vector3d(-3.2, 1.5, 0.1), 0.1));
        scene.addChild(creator.createObject("KeypadDoorLock", new AxisAngle4d(0, 1, 0, 0), new Vector3d(-0.5, 1.2, -3.4), 0.2));
        scene.addChild(creator.createObject("Lockers_door", new AxisAngle4d(0, 1, 0, 0), new Vector3d(-2, 0.5, 1.5), 0.5));
        
        // Cross objects (wall decorations)
        scene.addChild(creator.createObject("Cross_left", new AxisAngle4d(0, 1, 0, 0), new Vector3d(-2, 1.8, -3.2), 0.5));
        scene.addChild(creator.createObject("Cross_middle", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0, 1.8, -3.2), 0.5));
        scene.addChild(creator.createObject("Cross_right", new AxisAngle4d(0, 1, 0, 0), new Vector3d(2, 1.8, -3.2), 0.5));
        scene.addChild(creator.createObject("The_leftmost_cross", new AxisAngle4d(0, 1, 0, 0), new Vector3d(-3, 1.8, -3.2), 0.5));
        scene.addChild(creator.createObject("The_rightmost_cross", new AxisAngle4d(0, 1, 0, 0), new Vector3d(3, 1.8, -3.2), 0.5));
        
        // Add enhanced lighting
        scene.addChild(createEnhancedLights());
        
        return scene;
    }
    
    private static BranchGroup createEnhancedLights() {
        BranchGroup lightGroup = new BranchGroup();
        BoundingSphere bounds = new BoundingSphere(new Point3d(), 100.0);
        
        // Ambient light (base illumination)
        AmbientLight ambient = new AmbientLight(new Color3f(0.4f, 0.4f, 0.4f));
        ambient.setInfluencingBounds(bounds);
        
        // Primary directional light (sun-like from upper left)
        DirectionalLight directional1 = new DirectionalLight(
            new Color3f(0.8f, 0.8f, 0.8f),
            new Vector3f(-0.5f, -1f, -0.5f));
        directional1.setInfluencingBounds(bounds);
        
        // Secondary directional light (fill light from right)
        DirectionalLight directional2 = new DirectionalLight(
            new Color3f(0.4f, 0.4f, 0.4f),
            new Vector3f(1f, -0.5f, -0.2f));
        directional2.setInfluencingBounds(bounds);
        
        // Point light near the wall lights
        PointLight pointLight1 = new PointLight(
            new Color3f(0.9f, 0.8f, 0.7f),
            new Point3f(-3f, 1.8f, 0f),
            new Point3f(1f, 0f, 0f));
        pointLight1.setInfluencingBounds(bounds);
        
        PointLight pointLight2 = new PointLight(
            new Color3f(0.9f, 0.8f, 0.7f),
            new Point3f(3f, 1.8f, 0f),
            new Point3f(1f, 0f, 0f));
        pointLight2.setInfluencingBounds(bounds);
        
        lightGroup.addChild(ambient);
        lightGroup.addChild(directional1);
        lightGroup.addChild(directional2);
        lightGroup.addChild(pointLight1);
        lightGroup.addChild(pointLight2);
        
        return lightGroup;
    }
}