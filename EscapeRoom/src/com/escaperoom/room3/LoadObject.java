package com.escaperoom.room3;

import org.jogamp.java3d.*;
import org.jogamp.java3d.loaders.*;
import org.jogamp.java3d.loaders.objectfile.ObjectFile;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.vecmath.*;
import java.io.*;

public class LoadObject {
    private static final Color3f DEFAULT_COLOR = new Color3f(0.7f, 0.7f, 0.7f);

    public static BranchGroup loadObject(String objPath) {
        System.out.println("Attempting to load: " + objPath);
        
        try {
            // Convert path to absolute and normalize
            File objFile = new File(objPath).getAbsoluteFile();
            System.out.println("Absolute path: " + objFile.getPath());
            
            if (!objFile.exists()) {
                System.err.println("File does not exist at: " + objFile.getPath());
                return createErrorObject("File not found:\n" + objFile.getName());
            }

            ObjectFile loader = new ObjectFile(ObjectFile.RESIZE | ObjectFile.TRIANGULATE);
            BranchGroup objGroup = loader.load(objFile.getPath()).getSceneGroup();
            
            if (objGroup != null) {
                applyDefaultAppearance(objGroup);
                return objGroup;
            }
        } catch (Exception e) {
            System.err.println("Load error: " + e.getMessage());
            return createErrorObject("Load failed:\n" + e.getMessage());
        }
        
        return createErrorObject("Unknown error\nloading OBJ");
    }

    private static void applyDefaultAppearance(BranchGroup group) {
        Material material = new Material();
        material.setDiffuseColor(DEFAULT_COLOR);
        material.setLightingEnable(true);

        Appearance app = new Appearance();
        app.setMaterial(material);

        for (int i = 0; i < group.numChildren(); i++) {
            Node child = group.getChild(i);
            if (child instanceof Shape3D) {
                ((Shape3D) child).setAppearance(app);
            }
        }
    }

    private static BranchGroup createErrorObject(String message) {
        System.out.println("Creating error object: " + message);
        
        BranchGroup errorBG = new BranchGroup();
        
        // Create red error box
        Material errorMat = new Material();
        errorMat.setDiffuseColor(new Color3f(1, 0, 0));
        errorMat.setLightingEnable(true);
        
        Appearance app = new Appearance();
        app.setMaterial(errorMat);
        errorBG.addChild(new Box(0.5f, 0.5f, 0.5f, app));
        
        return errorBG;
    }
}