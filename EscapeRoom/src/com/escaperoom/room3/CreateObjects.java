package com.escaperoom.room3;

import org.jogamp.java3d.*;
import org.jogamp.java3d.loaders.*;
import org.jogamp.java3d.loaders.objectfile.*;
import org.jogamp.java3d.utils.geometry.Primitive;
import org.jogamp.vecmath.*;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.image.TextureLoader;

public class CreateObjects {
    private SharedGroup[] roomSG;
    public String[] SGObjects = {
        "Escape_door", "Locker's_door", "Locker", "Keypad.DoodLock", 
        "SwitchMain", "SwitchHandle", "Window_Casement_Frame", "Wall_light_left",
        "Wall_light_right", "Cross_left", "Cross_middle", "Cross_right"
    };
    
    public BranchGroup roomBG = new BranchGroup();
    public final Color3f White = new Color3f(1.0f, 1.0f, 1.0f);
    public final Color3f Grey = new Color3f(0.35f, 0.35f, 0.35f);
    public final Color3f Black = new Color3f(0.0f, 0.0f, 0.0f);
    
    public CreateObjects() {
        createSG();
    }

    public BranchGroup room() {
        roomBG.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        roomBG.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        
        // Main room structure
        roomBG.addChild(createObject("!room3", new AxisAngle4d(0, 0, 0, 0), 
            new Vector3d(0, 0, 0), 1.0));
        
        // Walls and floor
        roomBG.addChild(createObject("!Baseboard", new AxisAngle4d(0, 0, 0, 0), 
            new Vector3d(0, -0.5, 0), 1.0));
        
        // Windows
        roomBG.addChild(createObject("!Window_Casement_Frame", new AxisAngle4d(0, 1, 0, Math.PI/2), 
            new Vector3d(2.0, 0.5, 0), 0.5));
        
        // Wall lights
        roomBG.addChild(createObject("!Wall_light_left", new AxisAngle4d(0, 1, 0, Math.PI), 
            new Vector3d(-1.5, 1.5, -2.0), 0.3));
        roomBG.addChild(createObject("!Wall_light_right", new AxisAngle4d(0, 1, 0, Math.PI), 
            new Vector3d(1.5, 1.5, -2.0), 0.3));
        
        // Escape door
        roomBG.addChild(createObject("@Escape_door", new AxisAngle4d(0, 1, 0, Math.PI), 
            new Vector3d(0, 0, -2.5), 0.5));
        
        // Locker and locker door
        roomBG.addChild(createObject("!Locker", new AxisAngle4d(0, 0, 0, 0), 
            new Vector3d(-1.0, 0, 1.0), 0.3));
        roomBG.addChild(createObject("@Locker's_door", new AxisAngle4d(0, 1, 0, 0), 
            new Vector3d(-0.8, 0.5, 1.0), 0.3));
        
        // Keypad
        roomBG.addChild(createObject("@Keypad.DoodLock", new AxisAngle4d(0, 1, 0, Math.PI/2), 
            new Vector3d(1.0, 1.0, 1.0), 0.2));
        
        // Switch components
        roomBG.addChild(createObject("!SwitchMain", new AxisAngle4d(0, 0, 0, 0), 
            new Vector3d(1.5, 1.2, -1.0), 0.1));
        roomBG.addChild(createObject("@SwitchHandle", new AxisAngle4d(0, 0, 0, 0), 
            new Vector3d(1.5, 1.2, -1.0), 0.1));
        
        // Cross decorations
        roomBG.addChild(createObject("!Cross_left", new AxisAngle4d(0, 1, 0, Math.PI/4), 
            new Vector3d(-1.5, 1.0, 0), 0.2));
        roomBG.addChild(createObject("!Cross_middle", new AxisAngle4d(0, 1, 0, Math.PI/4), 
            new Vector3d(0, 1.0, 0), 0.2));
        roomBG.addChild(createObject("!Cross_right", new AxisAngle4d(0, 1, 0, Math.PI/4), 
            new Vector3d(1.5, 1.0, 0), 0.2));
        
        // Comics and papers
        roomBG.addChild(createObject("!Comics", new AxisAngle4d(0, 1, 0, Math.PI/2), 
            new Vector3d(-0.5, 0.8, -1.0), 0.1));
        roomBG.addChild(createObject("!Paper", new AxisAngle4d(0, 0, 0, 0), 
            new Vector3d(0.5, 0.5, 0.5), 0.05));
        
        // Chart
        roomBG.addChild(createObject("!ChartOld", new AxisAngle4d(0, 1, 0, Math.PI), 
            new Vector3d(0, 1.2, -1.5), 0.3));
        
        return roomBG;
    }

    public TransformGroup createObject(String name, AxisAngle4d rotation, Vector3d translation, double scale) {
        for (int i = 0; i < SGObjects.length; i++) {
            if (name.substring(1).equals(SGObjects[i])) {
                SharedGroup SG = roomSG[i];
                Link link = new Link(SG);
                link.setName(name);

                Transform3D transform = new Transform3D();
                transform.set(rotation);
                transform.setScale(scale);
                transform.setTranslation(translation);

                TransformGroup objTG = new TransformGroup(transform);
                objTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
                objTG.addChild(link);
                objTG.setName(name);
                objTG.setUserData(transform);

                return objTG;
            }
        }

        return createLooseObject(name, rotation, translation, scale);
    }

    public TransformGroup createLooseObject(String name, AxisAngle4d rotation, Vector3d translation, double scale) {
        Transform3D transform = new Transform3D();
        transform.set(rotation);
        transform.setScale(scale);
        transform.setTranslation(translation);

        TransformGroup objTG = new TransformGroup(transform);
        objTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objTG.setName(name);
        objTG.setUserData(transform);

        // Load the object file
        objTG.addChild(LoadObject.loadObject("objects/" + name.substring(1) + ".obj"));
        return objTG;
    }

    public void createSG() {
        roomSG = new SharedGroup[SGObjects.length];
        for (int i = 0; i < SGObjects.length; i++) {
            SharedGroup objSG = new SharedGroup();
            objSG.addChild(LoadObject.loadObject("objects/" + SGObjects[i] + ".obj"));
            objSG.getChild(0).setName(SGObjects[i]);
            objSG.setName(SGObjects[i]);
            objSG.compile();
            roomSG[i] = objSG;
        }
    }

    public TransformGroup createBox(String name, AxisAngle4d rotation, Vector3d translation, 
            float x, float y, float z, double scale, Appearance appearance) {
        Transform3D transform = new Transform3D();
        transform.set(rotation);
        transform.setScale(scale);
        transform.setTranslation(translation);

        TransformGroup objTG = new TransformGroup(transform);
        objTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objTG.setName(name);
        objTG.setUserData(transform);
        objTG.addChild(new Box(x, y, z, Primitive.GENERATE_NORMALS, appearance));

        return objTG;
    }
}

class LoadObject {
    public static BranchGroup loadObject(String filePath) {
        ObjectFile objFile = new ObjectFile(ObjectFile.RESIZE);
        BranchGroup objBG = null;
        
        try {
            Scene loadedScene = objFile.load(filePath);
            objBG = loadedScene.getSceneGroup();
        } catch (Exception e) {
            System.err.println("Error loading object: " + filePath);
            e.printStackTrace();
            // Create a simple box as fallback
            objBG = new BranchGroup();
            Appearance app = new Appearance();
            app.setMaterial(new Material());
            Box box = new Box(0.1f, 0.1f, 0.1f, app);
            objBG.addChild(box);
        }
        
        return objBG;
    }
    
    public static Appearance obj_Appearance(Color3f color) {
        Appearance app = new Appearance();
        Material mat = new Material();
        mat.setDiffuseColor(color);
        mat.setSpecularColor(White);
        mat.setShininess(64.0f);
        app.setMaterial(mat);
        return app;
    }
    
    private static final Color3f White = new Color3f(1.0f, 1.0f, 1.0f);
}