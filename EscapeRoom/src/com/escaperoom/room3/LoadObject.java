package com.escaperoom.room3;

import java.io.File;
import java.io.FileNotFoundException;

import org.jogamp.java3d.*;
import org.jogamp.java3d.loaders.IncorrectFormatException;
import org.jogamp.java3d.loaders.ParsingErrorException;
import org.jogamp.java3d.loaders.objectfile.ObjectFile;
import org.jogamp.vecmath.Color3f;

public class LoadObject {
    public final static Color3f White = new Color3f(1.0f, 1.0f, 1.0f);
    public final static Color3f Grey = new Color3f(0.35f, 0.35f, 0.35f);
    public final static Color3f Black = new Color3f(0.0f, 0.0f, 0.0f);
    private static Color3f[] mtl_clrs = { White, Grey, Black };

    public static Appearance obj_Appearance(Color3f m_clr) {
        Material mtl = new Material();
        mtl.setShininess(32);
        mtl.setAmbientColor(mtl_clrs[0]);
        mtl.setDiffuseColor(m_clr);
        mtl.setSpecularColor(mtl_clrs[1]);
        mtl.setEmissiveColor(mtl_clrs[2]);
        mtl.setLightingEnable(true);

        Appearance app = new Appearance();
        app.setMaterial(mtl);
        return app;
    }

    public static BranchGroup loadObject(String objName) {
        ObjectFile loader = new ObjectFile(ObjectFile.RESIZE, (float) (60 * Math.PI / 180.0));
        BranchGroup objGroup = null;
        
        try {
            objGroup = loader.load(objName).getSceneGroup();
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + objName);
            e.printStackTrace();
        } catch (ParsingErrorException | IncorrectFormatException e) {
            System.err.println("Error loading object: " + objName);
            e.printStackTrace();
        }

        if (objGroup != null) {
            for (int i = objGroup.numChildren()-1; i >= 0; i--) {
                Shape3D shape = (Shape3D) objGroup.getChild(i);
                shape.setAppearance(obj_Appearance(new Color3f(0.5f, 0.35f, 0.05f))); // Brown color for chair
            }
        }
        
        return objGroup;
    }
}