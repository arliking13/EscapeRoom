package com.escaperoom.room3;

import org.jogamp.java3d.*;
import org.jogamp.vecmath.*;
import java.io.File;

public class CreateObjects {
    private BranchGroup sceneRoot;

    public CreateObjects() {
        sceneRoot = new BranchGroup();
        sceneRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        
        // Add lighting
        addBasicLighting();
        
        // Try loading the object with proper path handling
        String objPath = getResourcePath("assets/room3/ChairOld.obj");
        System.out.println("Attempting to load from: " + objPath);
        
        TransformGroup objGroup = createObjectGroup(objPath);
        sceneRoot.addChild(objGroup);
    }

    private String getResourcePath(String relativePath) {
        // Try multiple possible locations
        String[] possiblePaths = {
            relativePath,
            "src/main/resources/" + relativePath,
            "EscapeRoom/" + relativePath,
            System.getProperty("user.dir") + "/" + relativePath
        };
        
        for (String path : possiblePaths) {
            File file = new File(path);
            if (file.exists()) {
                return file.getAbsolutePath();
            }
            System.out.println("Not found: " + file.getAbsolutePath());
        }
        
        return relativePath; // Fallback to original path
    }

    private void addBasicLighting() {
        // Ambient light
        AmbientLight ambient = new AmbientLight(new Color3f(0.5f, 0.5f, 0.5f));
        ambient.setInfluencingBounds(new BoundingSphere(new Point3d(), 100.0));
        sceneRoot.addChild(ambient);

        // Directional light
        DirectionalLight directional = new DirectionalLight(
            new Color3f(0.8f, 0.8f, 0.8f),
            new Vector3f(-1f, -1f, -1f)
        );
        directional.setInfluencingBounds(new BoundingSphere(new Point3d(), 100.0));
        sceneRoot.addChild(directional);
    }

    private TransformGroup createObjectGroup(String objPath) {
        TransformGroup tg = new TransformGroup();
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        // Load the object
        BranchGroup obj = LoadObject.loadObject(objPath);
        tg.addChild(obj);
        
        return tg;
    }

    public BranchGroup getScene() {
        return sceneRoot;
    }
}