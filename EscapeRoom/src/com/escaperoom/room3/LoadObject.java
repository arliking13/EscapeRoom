package com.escaperoom.room3;

import org.jogamp.java3d.*;
import org.jogamp.java3d.loaders.*;
import org.jogamp.java3d.loaders.objectfile.ObjectFile;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.vecmath.*;
import java.io.File;

public class LoadObject {
    public final static Color3f White = new Color3f(1.0f, 1.0f, 1.0f);
    public final static Color3f Grey = new Color3f(0.35f, 0.35f, 0.35f);
    public final static Color3f Black = new Color3f(0.0f, 0.0f, 0.0f);
    public final static Color3f Red = new Color3f(0.8f, 0.0f, 0.0f);
    public final static Color3f Green = new Color3f(0.0f, 0.8f, 0.0f);
    public final static Color3f Blue = new Color3f(0.0f, 0.0f, 0.8f);
    public final static Color3f Yellow = new Color3f(0.8f, 0.8f, 0.0f);

    public static Appearance obj_Appearance(Color3f m_clr) {
        Material mtl = new Material();
        mtl.setShininess(32);
        mtl.setAmbientColor(0.2f, 0.2f, 0.2f);
        mtl.setDiffuseColor(m_clr);
        mtl.setSpecularColor(White);
        mtl.setLightingEnable(true);

        Appearance app = new Appearance();
        app.setMaterial(mtl);
        return app;
    }

    public static BranchGroup loadObject(String objName) {
        System.out.println("\nAttempting to load: " + objName);
        
        // Try multiple possible locations
        String[] searchPaths = {
            "assets/room3/" + objName,
            "assets/" + objName,
            "src/assets/room3/" + objName, 
            "src/assets/" + objName,
            "src/main/resources/assets/room3/" + objName,
            objName
        };

        ObjectFile loader = new ObjectFile(ObjectFile.RESIZE, 
            (float)(60 * Math.PI / 180.0));
        loader.setFlags(ObjectFile.RESIZE | ObjectFile.TRIANGULATE);

        for (String path : searchPaths) {
            File file = new File(path);
            System.out.println("Checking: " + file.getAbsolutePath());
            
            if (file.exists()) {
                try {
                    System.out.println("Found! Loading: " + path);
                    BranchGroup objGroup = loader.load(path).getSceneGroup();
                    applyAppearances(objGroup, path);
                    return objGroup;
                } catch (Exception e) {
                    System.err.println("Error loading " + path + ": " + e.getMessage());
                }
            }
        }

        System.err.println("FAILED to load object: " + objName);
        return createErrorObject();
    }

    private static void applyAppearances(BranchGroup objGroup, String path) {
        if (objGroup != null) {
            for (int i = 0; i < objGroup.numChildren(); i++) {
                Node child = objGroup.getChild(i);
                if (child instanceof Shape3D) {
                    ((Shape3D)child).setAppearance(obj_Appearance(Grey));
                }
            }
        }
    }

    private static BranchGroup createErrorObject() {
        BranchGroup errorBG = new BranchGroup();
        Appearance app = obj_Appearance(Red);
        Box errorBox = new Box(0.5f, 0.5f, 0.5f, app);
        errorBG.addChild(errorBox);
        return errorBG;
    }
}