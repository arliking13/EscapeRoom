import java.io.File;
import org.jogamp.java3d.*;
import org.jogamp.java3d.loaders.*;
import org.jogamp.java3d.loaders.objectfile.ObjectFile;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.vecmath.Color3f;

public class LoadObject {
    public static Appearance obj_Appearance(String fileName, int mtlNum) {
        // Default fallback material
        if (!new File(fileName.substring(0, fileName.length()-3) + "mtl").exists()) {
            return createDefaultAppearance();
        }

        // Load material from MTL
        MTLFile mtl = new MTLFile(fileName, mtlNum);
        return createMaterialAppearance(mtl);
    }
    
    private static Appearance createDefaultAppearance() {
        Material mat = new Material();
        mat.setAmbientColor(0.2f, 0.2f, 0.2f);
        mat.setDiffuseColor(0.8f, 0.6f, 0.4f); // Wood-like fallback
        mat.setSpecularColor(0.5f, 0.5f, 0.5f);
        mat.setShininess(32f);
        mat.setLightingEnable(true);
        
        Appearance app = new Appearance();
        app.setMaterial(mat);
        return app;
    }
    
    private static Appearance createMaterialAppearance(MTLFile mtl) {
        Material mat = new Material();
        mat.setAmbientColor(mtl.ambient);
        mat.setDiffuseColor(mtl.diffuse);
        mat.setSpecularColor(mtl.specular);
        mat.setEmissiveColor(mtl.emissive);
        mat.setShininess(mtl.shininess);
        mat.setLightingEnable(true);
        
        Appearance app = new Appearance();
        app.setMaterial(mat);
        
        // Only add texture if successfully loaded
        if (mtl.texture != null) {
            app.setTexture(mtl.texture);
        }
        
        return app;
    }
    
    public static BranchGroup loadObject(String objName) {
        // More tolerant loader settings
        int flags = ObjectFile.RESIZE | ObjectFile.LOAD_ALL;
        ObjectFile loader = new ObjectFile(flags, (float)Math.toRadians(60));
        
        try {
            BranchGroup objGroup = loader.load("room3/" + objName + ".obj").getSceneGroup();
            applyAppearances(objGroup, objName);
            return objGroup;
        } catch (Exception e) {
            System.err.println("Error loading " + objName + ": " + e.getMessage());
            return createErrorObject();
        }
    }
    
    private static void applyAppearances(BranchGroup objGroup, String objName) {
        for (int i = 0; i < objGroup.numChildren(); i++) {
            if (objGroup.getChild(i) instanceof Shape3D) {
                Shape3D shape = (Shape3D) objGroup.getChild(i);
                shape.setAppearance(obj_Appearance("room3/" + objName + ".obj", i));
            }
        }
    }
    
    private static BranchGroup createErrorObject() {
        Appearance app = new Appearance();
        app.setMaterial(new Material());
        BranchGroup bg = new BranchGroup();
        bg.addChild(new Box(0.1f, 0.1f, 0.1f, app)); // Red error cube
        return bg;
    }
}