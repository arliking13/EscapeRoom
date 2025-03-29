package com.escaperoom.room3;

import org.jogamp.java3d.*;
import org.jogamp.vecmath.AxisAngle4d;
import org.jogamp.vecmath.Vector3d;

public class CreateObjects {
    private BranchGroup sceneBG = new BranchGroup();
    
    public CreateObjects() {
        sceneBG.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        sceneBG.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
    }

    public BranchGroup createScene() {
        // Add chair to the scene
        sceneBG.addChild(createObject("ChairOld", 
            new AxisAngle4d(0, 1, 0, 0), // No rotation
            new Vector3d(0.0, -0.5, 0.0), // Position
            0.5)); // Scale
        
        return sceneBG;
    }

    private TransformGroup createObject(String name, AxisAngle4d rotation, Vector3d translation, double scale) {
        Transform3D transform = new Transform3D();
        transform.set(rotation);
        transform.setScale(scale);
        transform.setTranslation(translation);

        TransformGroup objTG = new TransformGroup(transform);
        objTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objTG.setName(name);
        
        // Load the object with correct path
        BranchGroup obj = LoadObject.loadObject("room3/" + name + ".obj");
        if (obj != null) {
            objTG.addChild(obj);
        } else {
            System.err.println("Failed to load object: " + name);
        }
        
        return objTG;
    }
}